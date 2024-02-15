import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/random/degree/scatter_chart.png"):
    plt.scatter(x_values, y_values, label= "Data points", marker='o', color='blue')  # Adjust marker and color as needed
    
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
    x_values = [0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.875, 0.9375, 1]
    y_values = [0.8046665847000005, 0.7554818443000001, 0.7271982410000003,  0.7141386776, 0.7197897540000001, 0.6961270799999998, 0.6950158699999999, 0.6800241454999997,0.6973291651000002, 0.6893098767999999, 0.6841757777, 0.6900497680000004, 0.6943935737, 0.6932597566000002, 0.6689444309999999, 0.6791909987999998]
    print(len(x_values), len(y_values))
    plot_scatter_chart(x_values, y_values, x_label="Connection probability", y_label="Cooperation rate")

if __name__ == "__main__":
    main()