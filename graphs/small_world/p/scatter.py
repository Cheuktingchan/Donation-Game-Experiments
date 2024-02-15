import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/5_p_coop.png"):
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
    x_values = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]
    y_values = [0.7441164701999997, 0.7540913015999999, 0.7566542926999997, 0.7713557513999999, 0.7750260451000002, 0.7775201048999996, 0.7853979248000001, 0.8005127558000001, 0.8078540658999998, 0.8202411570999999]

    plot_scatter_chart(x_values, y_values, x_label="Rewiring probability", y_label="Cooperation rate")

if __name__ == "__main__":
    main()