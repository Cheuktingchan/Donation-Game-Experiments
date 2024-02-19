import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, std_dev_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/2_n_coop_sd.png"):
    plt.scatter(x_values, y_values, label="Data points", marker='o', color='blue')  # Adjust marker and color as needed

    z = np.polyfit(x_values, y_values, 1)
    p = np.poly1d(z)
    plt.plot(x_values, p(x_values), 'r--', label="Line of best fit")

    plt.errorbar(x_values, y_values, yerr=std_dev_values, fmt='none', ecolor='gray', capsize=5, label='Error bars')

    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)
    plt.show()

def main():
    x_values = [0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.875, 0.9375, 1]
    y_values = [0.8046665847000005, 0.7554818443000001, 0.7271982410000003,  0.7141386776, 0.7197897540000001, 0.6961270799999998, 0.6950158699999999, 0.6800241454999997,0.6973291651000002, 0.6893098767999999, 0.6841757777, 0.6900497680000004, 0.6943935737, 0.6932597566000002, 0.6689444309999999, 0.6791909987999998]
    std_dev_values = [0.06499067182769114, 0.07585734369742601, 0.07852258098951014, 0.0789239563882039, 0.07406179768059493, 0.06653337294903484, 0.08467540050017415, 0.08732846920725632, 0.07800970178053102, 0.0789239563882039, 0.07798336071387241, 0.0701525176590983, 0.0742915934521418, 0.07573224412088043, 0.0829292211214393, 0.06844515973655357]
    print(len(x_values), len(y_values), len(std_dev_values))
    plot_scatter_chart(x_values, y_values, std_dev_values, x_label="Connection probability", y_label="Cooperation rate")

if __name__ == "__main__":
    main()