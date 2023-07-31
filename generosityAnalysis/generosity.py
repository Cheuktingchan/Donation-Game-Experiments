#!pip install deeptime

import numpy as np
import math
import itertools
import sys
from deeptime.markov.msm import MarkovStateModel

def array_expand(array_set): 
  """creates tuples with the possible combinations of tuples expecting array_set[0] to be the number of agents in the tuple. Example usage: `array_expand(set([tuple(5,0,0,0)]))`"""
  add=set()
  finished=False
  while not finished:
    finished=True
    for a in array_set:
      for j in range(len(a)-1):
        if a[j]!=0:
          b=list(a)
          b[j]-=1
          b[j+1]+=1
          b=tuple(b)
          if b not in add and b not in array_set:
            add.add(b)
            finished=False
    array_set.update(add)
  return array_set

def make_indices(num_rep,num_s1,num_s2):
  """This function splits agents to all possible allocations. E.g., make_indices(6,3,2) will allocate the 3 agents to all possible 6 reputation values in the first tuple and 2 agents to all possible reputation values in the second."""  
  s1a=[0]*num_rep
  s2a=[0]*num_rep
  s1a[0]=num_s1
  s2a[0]=num_s2
  i1=array_expand(set([tuple(s1a)]))
  i2=array_expand(set([tuple(s2a)]))
  return itertools.product(i1,i2)


def make_index(array_set): 
  """A simple caching function which allows one to map from ints to tuple pairs and back. returns d,r where d[i] takes an index and returns a tuple and r[a] takes a tuple and returns the index"""
  i=0
  d={}
  r={}
  for a in array_set:
    d[i]=a
    r[a]=i
    i+=1
  return (d,r)

def increase_image(tup,image_index):
  """decreases the image at the current image_index, increases it at the next one, unless we're at maximum. Imageindex is an integer between 0 and 2*R-1, i.e., goes across the pair"""
  (n,m)=tup
  if image_index==len(n)-1 or image_index==2*len(n)-1:
    return tup
  n=list(n)
  m=list(m)
  if image_index<len(n):
    n[image_index]-=1
    n[image_index+1]+=1
  else:
    m[image_index-len(n)]-=1
    m[image_index-len(n)+1]+=1
  return (tuple(n),tuple(m))

def decrease_image(tup,image_index):
  """decreases the image at the current image_index, increases it at the next one, unless we're at maximum. Imageindex is an integer between 0 and 2*R-1, i.e., goes across the pair"""
  (n,m)=tup
  if image_index==0 or image_index==len(n):
    return tup
  n=list(n)
  m=list(m)
  if image_index<len(n):
    n[image_index]-=1
    n[image_index-1]+=1
  else:
    m[image_index-len(n)]-=1
    m[image_index-len(n)-1]+=1
  return (tuple(n),tuple(m))

def powermethod(m):
  """Use the power method to compute the dominant eigenvector"""
  t=MarkovStateModel(m)
  return t.stationary_distribution

def prob_donate(s_d,i_r,e_a,e_r,g,r):
  #small speedup: recalculate terms used multiple times, namely (r-s_d/r)
  if s_d>r:
    return 0
  x=(r-s_d)/r
  if i_r>=s_d:
    return (1-e_a)*((1-e_r)+e_r*(1-x+x*g))
  else:
    return (1-e_a)*((1-e_r)*g+e_r*(1-x+x*g))

def compute_transitions(tup,s1,s2,e_a,e_r,g):
  """takes in a ([n_1, ..., n_r],[m_1, ... m_r]) tuple and returns a [new_tuple]->likelihood tuple for those transitions"""
  probabilities={}

  (n,m)=tup
  num_agents=sum(n)+sum(m)
  for donor in range(len(n)+len(m)):
    for recipient in range(len(n)+len(m)):
      if (n+m)[donor]==0 or (n+m)[recipient]==0: #speedup: if there is no donor or recipient agent at this index, just continue.
        continue
      #donor and recipient are indices
      prob_play=0
      if donor!=recipient:
        prob_play=(n+m)[donor]*(n+m)[recipient]/(num_agents*(num_agents-1))
      else:
        prob_play=((n+m)[donor]**2-(n+m)[donor])/(num_agents*(num_agents-1))
      #we now have the likelihood of them playing against each other
      #now get the actual indices to work out the strategies.
      if donor-len(n)<0:
        strat1=s1
      else:
        strat1=s2
      if recipient-len(n)<0:
        strat2=s1
      else:
        strat2=s2
      image1=donor%len(n)
      image2=recipient%len(n)

      p_donate=prob_donate(strat1,image2,e_a,e_r,g,len(n))
      donate_tuple=increase_image(tup,donor)
      no_donate_tuple=decrease_image(tup,donor)
      
      probabilities[donate_tuple]=p_donate*prob_play+probabilities.get(donate_tuple,0)
      probabilities[no_donate_tuple]=(1-p_donate)*prob_play+probabilities.get(no_donate_tuple,0)
  return probabilities

def make_transition_matrix(num_s1,num_s2,s1,s2,e_a,e_r,g,r):
  tuples=list(make_indices(r,num_s1,num_s2))
  (index_to_tuple,tuple_to_index)=make_index(tuples)
  transition_matrix=np.zeros([len(tuples),len(tuples)])
  for t in tuples:
    transition_probs=compute_transitions(t,s1,s2,e_a,e_r,g)
    for p in transition_probs:
      transition_matrix[tuple_to_index[t],tuple_to_index[p]]=transition_probs[p]
  return transition_matrix   

def tuple_utility(tup,s1,s2,donate_util,recieve_util,e_a,e_r,g):
  """this returns a pair of utilities for each strategy by considering the likelihood that a pair of agents play each other and multiplying that by the likelihood of donating and the payoff for donating"""
  util={s1:0,s2:0}
  (n,m)=tup
  num_agents=sum(n)+sum(m)
  for donor in range(len(n)+len(m)):
    for recipient in range(len(n)+len(m)):
      if (n+m)[donor]==0 or (n+m)[recipient]==0: #speedup: if there is no donor or recipient agent at this index, just continue.
        continue
      #donor and recipient are indices
      prob_play=0
      if donor!=recipient:
        prob_play=(n+m)[donor]*(n+m)[recipient]/(num_agents*(num_agents-1))
      else:
        prob_play=((n+m)[donor]**2-(n+m)[donor])/(num_agents*(num_agents-1))
      #we now have the likelihood of them playing against each other
      #now get the actual indices to work out the strategies.
      if donor-len(n)<0:
        strat1=s1
      else:
        strat1=s2
      if recipient-len(n)<0:
        strat2=s1
      else:
        strat2=s2
      image1=donor%len(n)
      image2=recipient%len(n)

      p_donate=prob_donate(strat1,image2,e_a,e_r,g,len(n))
      util[strat1]+=p_donate*prob_play*donate_util
      util[strat2]+=p_donate*prob_play*recieve_util
  return util

def compute_utility(num_s1,num_s2,s1,s2,e_a,e_r,g,r,donate_util,recieve_util):
  "computes the utility for a specific distribution of s1 and s2 strategies across all tuples with that number"
  tm=make_transition_matrix(num_s1,num_s2,s1,s2,e_a,e_r,g,r)
  #ei=eig(tm,left=True,right=True)[2][0]
  vec=powermethod(tm)
  #so vec contains the probabilities of the tuple arising. What is the expected utility of the tuple playing each other?
  util={s1:0,s2:0}
  tuples=list(make_indices(r,num_s1,num_s2))
  (index_to_tuple,tuple_to_index)=make_index(tuples)
  for i in range(len(vec)):
    t=index_to_tuple[i]
    u=tuple_utility(t,s1,s2,donate_util,recieve_util,e_a,e_r,g)
    util[s1]+=u[s1]*vec[i]
    util[s2]+=u[s2]*vec[i]
  return util

def strat_transition_matrix(num_agents,e_a,e_r,g,r,donate_util,recieve_util,beta):
  fm=np.zeros([r+1,r+1])
  for i in range(r+1):
    for j in range(r+1):
      s1=i
      s2=j
      sm=0
      for na in range(1,num_agents):
        tmp=1
        for k in range(1,na+1):
          u=compute_utility(k,num_agents-k,s1,s2,e_a,e_r,g,r,donate_util,recieve_util)
          us1=u[s1]
          us2=u[s2]
          
          tmp*=math.exp(-beta*(us2-us1)) #faster than calling fermi_learning, constant is the beta parameter in fermi
        sm+=tmp
      fixation=1/(1+sm)
      fm[i,j]=fixation
  fm/=r    
  for i in range(r+1):
    fm[i,i]=1-(sum(fm[i])-fm[i,i])
  return fm

def cooperation_index(num_agents,e_a,e_r,g,r,donate_util,recieve_util,beta):
  """The cooperation index is calculated as the likelihood of ending up in a single agent state (c.f., the strat_transition_matrix)
     times the likelihood of having some distribution of images in that state times the likelihood that an agent will donate in that state."""
  sum=0
  stm=strat_transition_matrix(num_agents,e_a,e_r,g,r,donate_util,recieve_util,beta)
  p=powermethod(stm)
  #print(f"fixation probabilities: {p}")
  for i in range(len(p)):
    tm=make_transition_matrix(num_agents,0,i,0,e_a,e_r,g,r) #we can consider all agents being in the first tuple.
    vec=powermethod(tm)
    tuples=list(make_indices(r,num_agents,0))
    (index_to_tuple,tuple_to_index)=make_index(tuples)
    for j in range(len(vec)):
      current_tuple=index_to_tuple[j][0]
      probability_of_tuple=vec[j]
      #if probability_of_tuple>0.01:
      #  print(f"{i} prob: {probability_of_tuple} tuple: {index_to_tuple[j][0]}")
      for donor in range(len(current_tuple)):
        for recipient in range(len(current_tuple)):
          if current_tuple[donor]==0 or current_tuple[recipient]==0:
            continue 
          prob_play=0
          if donor!=recipient:
            prob_play=current_tuple[donor]*current_tuple[recipient]/(num_agents*(num_agents-1))
          else:
            prob_play=(current_tuple[donor]**2-current_tuple[donor])/(num_agents*(num_agents-1))

          image1=donor
          image2=recipient

          p_donate=prob_donate(i,image2,e_a,e_r,g,r)
          #if p_donate*prob_play*probability_of_tuple*p[i]>1e-18:
          #  print(f"d: {donor}, r: {recipient} pp: {prob_play} pd: {p_donate} p_t: {probability_of_tuple} i: {i} pi: {p[i]} res:{p_donate*prob_play*probability_of_tuple*p[i]}")

          sum+=p_donate*prob_play*probability_of_tuple*p[i]
  return sum  

#######################################
np.set_printoptions(precision=8,suppress=True,threshold=sys.maxsize)
import matplotlib.pyplot as plt
from collections import OrderedDict

linestyles_dict = OrderedDict(
    [('solid',               (0, ())),
     ('loosely dotted',      (0, (1, 10))),
     ('dotted',              (0, (1, 5))),
     ('densely dotted',      (0, (1, 1))),

     ('loosely dashed',      (0, (5, 10))),
     ('dashed',              (0, (5, 5))),
     ('densely dashed',      (0, (5, 1))),

     ('loosely dashdotted',  (0, (3, 10, 1, 10))),
     ('dashdotted',          (0, (3, 5, 1, 5))),
     ('densely dashdotted',  (0, (3, 1, 1, 1))),

     ('loosely dashdotdotted', (0, (3, 10, 1, 10, 1, 10))),
     ('dashdotdotted',         (0, (3, 5, 1, 5, 1, 5))),
     ('densely dashdotdotted', (0, (3, 1, 1, 1, 1, 1)))])

colors_dict = OrderedDict(
    [('1', '#ff9933'),
     ('2', '#009999'),
     ('3', '#cc66ff'),
     ('4', '#6699ff'),
     ('5', '#ff9900'),
     ('6', '#009933')])

lines_dict = OrderedDict(
     [('1', (0, (1, 5))),
     ('2', (0, (5, 5))),
     ('3', (0, (3, 5, 1, 5))),
     ('4', (0, (1, 10))),
     ('5', (0, (5, 10))),
     ('6', (0, (3, 10, 1, 10, 1, 10))),
     ('7', (0, (1, 1))),
     ('8', (0, (5, 1))),
     ('9', (0, (3, 1, 1, 1)))])

import multiprocessing



def evalpoint(x):
  return (cooperation_index(x,0.025,0.025,0,4,-0.1,1.0,10)), (cooperation_index(x,0.025,0.025,0.01,4,-0.1,1.0,10)), (cooperation_index(x,0.025,0.025,0.05,4,-0.1,1.0,10))


if __name__=="__main__":
  fig=plt.figure()
  plt.xlim(1.9,8.1)
  plt.ylim(-0.02,1.02)
  #xpoints = [0.0, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2]
  xpoints = [2, 3, 4, 5, 6, 7,8]
  pool=multiprocessing.Pool()
  ret=pool.map(evalpoint,xpoints)
  print(ret)
  #so ret now contains an array of arrays

  ypoints1=[]
  ypoints2=[]
  ypoints3=[]

  for y in ret:
      ypoints1.append(y[0])
      ypoints2.append(y[1])
      ypoints3.append(y[2])
    
  plt.plot(xpoints,ypoints1,color=colors_dict['1'], marker='o', linestyle=lines_dict['1'],
         label="0 generosity")
  plt.plot(xpoints,ypoints2,color=colors_dict['2'], marker='o', linestyle=lines_dict['2'],
         label="0.01 generosity")
  plt.plot(xpoints,ypoints3,color=colors_dict['3'], marker='o', linestyle=lines_dict['3'],
         label="0.05 generosity")
  plt.legend(loc='upper right')

  fn="delta_ag-4r-2-8.pdf"
  plt.savefig(fn)
  plt.close()