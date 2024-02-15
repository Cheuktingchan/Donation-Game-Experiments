import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/4_n_coop.png"):
    plt.scatter(x_values, y_values, label="Data points")
    
    # Calculate the best fit line
    z = np.polyfit(x_values, y_values, 1)
    p = np.poly1d(z)
    plt.plot(x_values, p(x_values), 'r--', label="Line of best fit")

    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)
    plt.close()

def main():
    x_values = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
    y_values = [0.9470635834999998, 0.9440534266999997, 0.9473837085000002, 0.9466062141000002, 0.9527237895999996, 0.9564746493000001, 0.9540846417000001, 0.9511798078000001, 0.9571015487000001, 0.9533967499999997]

    plot_scatter_chart(x_values, y_values, x_label="Number of initial nodes", y_label="Cooperation rate")

if __name__ == "__main__":
    main()