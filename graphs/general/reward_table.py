import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

fig, ax = plt.subplots()

fig.patch.set_visible(False)
ax.axis('off')
ax.axis('tight')

data = np.array([["Fully Connected", "1.8320033172727272", "2.0417034962047476", "1.875545454545453"],
                 ["Bipartite", "1.8274622400000002", "2.0995297428881816", "1.7667272727272716"],
                 ["Random", "1.8360651950999993", "2.0956479726271002", "1.9865699999999977"],
                 ["Community", "2.01033432", "2.0801554251133347", "1.8943636363636351"],
                 ["Scale-Free", "2.5486290709090915", "11.833281861172932", "2.4991818181818144"],
                 ["Small-World", "2.047955385454546", "2.5754532196228292", "2.058090909090907"]])
df = pd.DataFrame(data, columns=["Network Topology:", "Reward Average:", "Reward Variance:", "Average Final Reward:"])

ax.table(cellText=df.values, colLabels=df.columns, loc='center')

fig.tight_layout()

plt.savefig('graphs/general/reward_table')