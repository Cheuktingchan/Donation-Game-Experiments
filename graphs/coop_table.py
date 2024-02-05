import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

fig, ax = plt.subplots()

fig.patch.set_visible(False)
ax.axis('off')
ax.axis('tight')

data = np.array([["Fully Connected", "0.6868746971717172", "1731.4646464646464", "0.7676767676767676"],
                 ["Bipartite", "0.6805510081818179", "1662.3967935871744", "0.9018036072144289"],
                 ["Random", "0.6867875790909092", "1724.9639278557115", "0.9298597194388778"],
                 ["Community", "0.6735478304040402", "1776.5490981963928", "0.9739478957915831"],
                 ["Scale-Free", "0.7416488873737374", "914.9498997995992", "-0.3587174348697395"],
                 ["Small-World", "0.7082110378787876", "1613.7214428857715", "0.9018036072144289"]])
df = pd.DataFrame(data, columns=["Network Topology:", "Cooperation Rate:", "Av. Conv. Generations:", "Av. Conv. Strategy:"])

ax.table(cellText=df.values, colLabels=df.columns, loc='center')

fig.tight_layout()

plt.savefig('graphs/coop_table')