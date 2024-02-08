import numpy as np
import matplotlib.pyplot as plt

data = np.random.randn(1000)

plt.hist(data, bins=12, color='blue', alpha=0.7)

plt.title('Histogram Plot')
plt.xlabel('Strategy')
plt.ylabel('Relative Frequency')

plt.savefig('graphs/histogram_plot.png')
