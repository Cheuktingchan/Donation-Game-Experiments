import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/3_n_coop.png"):
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
    x_values = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    y_values = [0.6791909987999998, 0.681000198, 0.6918516263000005, 0.6838871848999999, 0.6845348639000002, 0.6965598242000003, 0.6847920422999999, 0.6836610186999996, 0.6757334428999998, 0.6827622401000001]

    plot_scatter_chart(x_values, y_values, x_label="Number of communities", y_label="Cooperation rate")

if __name__ == "__main__":
    main()