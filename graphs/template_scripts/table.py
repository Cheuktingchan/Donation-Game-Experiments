import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

fig, ax = plt.subplots()

fig.patch.set_visible(False)
ax.axis('off')
ax.axis('tight')

data = np.array([["Fully Connected", "0.678519747272727", "1510.858585858586", "1.1313131313131313"],
                 ["Bipartite", "0.6974649471717173", "1742.6973947895792", "1.276553106212425"],
                 ["Random", "0.8178155033333335", "1824.2685370741483", "-0.10020040080160321"],
                 ["Community", "0.6832742443434342", "1752.4488977955912", "0.9278557114228457"],
                 ["Scale-Free", "0.9439366895959593", "1089.2705410821643", "-1.7675350701402806"],
                 ["Small-World", "0.7585019951515152", "1783.246492985972", "0.6593186372745491"]])
df = pd.DataFrame(data, columns=["Network Topology:", "Cooperation Rate:", "Av. Conv. Generations:", "Av. Conv. Strategy:"])

ax.table(cellText=df.values, colLabels=df.columns, loc='center')

fig.tight_layout()

plt.savefig('graphs/coop_table')