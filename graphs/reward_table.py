import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

fig, ax = plt.subplots()

fig.patch.set_visible(False)
ax.axis('off')
ax.axis('tight')

data = np.array([["Fully Connected", "1.854561674545454", "2.0670504241119185", "1.815454545454544"],
                 ["Bipartite", "1.8374877209090907", "3.5241717144350506", "1.4841818181818185"],
                 ["Random", "1.8543264572727272", "2.1319317078013134", "1.898999999999998"],
                 ["Community", "1.8185791536363631", "2.0567918049750498", "1.763181818181817"],
                 ["Scale-Free", "2.002451993636364", "20.128873593599696", "2.012545454545452"],
                 ["Small-World", "1.912169794545455", "2.4801792038818182", "1.7669090909090894"]])
df = pd.DataFrame(data, columns=["Network Topology:", "Reward Average:", "Reward Variance:", "Average Final Reward:"])

ax.table(cellText=df.values, colLabels=df.columns, loc='center')

fig.tight_layout()

plt.savefig('graphs/reward_table')