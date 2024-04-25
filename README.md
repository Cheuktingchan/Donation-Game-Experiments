# ECAI-Forgiveness

This repository contains the implementation of the donation game with generosity and forgiveness as reported in "Generosity and the Emergence of Forgiveness in the Donation Game" (published in ECAI 2023).
Note that this is a refactored version of the code to increase readability. Please get in touch if you spot any issues.

The generosity analysis code is in Python, the main simulation is in Java (version 19). 

## Generosity Analysis

The analysis code is on the generosityAnalysis directory. `generosity_analysis.ipynb` is cleaned up code; `generosity.py` uses the multiprocessing package to speed things up and is the code actually used to generate the data.

## Donation Game Simulation

The code has been tested on Mac and Linux, but is untested on Windows. The following directory structure is assumed for the main simulation:

    src/ - the code itself
    bin/ - the compiled Java code
    data/ - for storing the output of simulations as csv files
    lib/ - contains commons-cli-1.5.0 as used by the main simulation class
    scripts/ - contains example scripts for running the simulation (based on the results in the ECAI paper)

To compile the main simulation code use:

    javac -d bin -classpath .:./src:./lib/commons-cli-1.5.0/commons-cli-1.5.0.jar src/Simulation.java

To run the simulation use (with appropriate arguments):

    java -classpath .:./bin:./lib/commons-cli-1.5.0/commons-cli-1.5.0.jar Simulation

To see the possible arguments run with:

    java -classpath .:./bin:./lib/commons-cli-1.5.0/commons-cli-1.5.0.jar Simulation -help

which should give the following output:

    usage: Simulation
    -ea,--actionNoise <arg>        Probability of donation action failing
                                    (action noise)
    -ep,--perceptionNoise <arg>    Probability of an observer incorrectly
                                    perceiving donor's action (perception
                                    noise)
    -fa,--forgiveness_action       Enable action forgiveness
    -fr,--forgiveness_reputation   Enable reputation (assessment) forgiveness
    -g,--generations <arg>         Generations to run simulation for
    -g1 <arg>                      Probability of assessment generosity
                                    [Schmid et al.]
    -g2 <arg>                      Probability of action generosity [Schmid
                                    et al.]
    -generosity                    Enable generosity [Schmid et al.,
                                    Scientific Reports, 2021]
    -h,--help                      Display this message
    -m,--pairs <arg>               Number of donor-recipient pairs per
                                    generation
    -mr,--mutation <arg>           Mutation rate
    -n,--size <arg>                Population size
    -ns,--preventNegativePayoffs   prevent negative payoffs (use base Nowak
                                    and Sigmund formulation)
    -q,--observation <arg>         Probability of observing an interaction
    -quiet                         Run with minimal output

The scripts used to generate the main results in the paper are contained in the scripts directory. Note that the forgiveness scripts will try to run 18 simulations simultaneously so if you have limited cores you may wish to run them individually.

## Network Simulation Parameters

-net <network> <parameter1> <parameter2>        Network parameters:
                                                Fully connected (network = 0): No parameters.
                                                Bipartite (network = 1): No parameters.
                                                Random (network = 2): Parameter1 specifies connection probability (decimal from 0-1).
                                                Community (network = 3): Parameter1 specifies the number of communities (integer divisible by n), and parameter2 specifies external link probability (decimal from 0-1).
                                                Scale-free (network = 4): Parameter1 specifies the number of initial nodes (integer less than n).
                                                Small-world (network = 5): Parameter1 specifies the neighbour distance (integer less than n), and parameter2 specifies rewiring probability (decimal from 0-1).

-outPart <args>                                 Specifies partitions to the output metrics (a list of integers which total n).
-intervals <arg>                                Specify intervals at which to collect metrics (integer less than g).
-local:                                         Constrains the reproduction pool to direct neighbours only.
-endN:                                          Ends the simulation when a strategy reaches 100 consecutive generations of above 0.98 relative frequency and outputs the number of generations taken.
