import matplotlib.pyplot as plt

# Data from the LaTeX table
external_link_probability = [0.001, 0.005, 0.01, 0.05, 0.1, 0.5, 1]
mean = [1.419, 0.576, 0.358, 0.162, 0.119, 0.074, 0.068]
std_dev = [0.243, 0.144, 0.098, 0.049, 0.039, 0.025, 0.021]

# Create the plot
plt.errorbar(external_link_probability, mean, yerr=std_dev, fmt='none', capsize=5, color='grey', zorder=1)
plt.scatter(external_link_probability, mean, color='blue', zorder=2)
plt.xlabel('External link probability')
plt.ylabel('Average strategy difference')
#plt.title('Mean and standard deviation of average strategy difference')

# Show the plot
plt.savefig("ASD-p.png")