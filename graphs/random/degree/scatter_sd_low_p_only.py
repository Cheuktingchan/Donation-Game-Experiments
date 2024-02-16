import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, std_dev_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/2_n_coop_sd_low_p_only.png"):
    plt.scatter(x_values, y_values, label="Data points", marker='o', color='blue')  # Adjust marker and color as needed

    z = np.polyfit(x_values, y_values, 1)
    p = np.poly1d(z)
    plt.plot(x_values, p(x_values), 'r--', label="Line of best fit")

    plt.errorbar(x_values, y_values, yerr=std_dev_values, fmt='none', ecolor='gray', capsize=5, label='Error Bars')

    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)
    plt.show()

def main():    
    low_p_x_y_sd_values = [(0.1, 0.7729090953030304, 0.06037599456690338),
                     (0.01, 0.9596833686567166, 0.021211538322537218),
                     (0.2, 0.717903091818182, 0.08219283088927141),
                     (0.05, 0.8323195737313436, 0.054668118530091694),
                     (0.005, 0.9761429065671642, 0.01364043748496727),
                     (0.15, 0.7442572965151515, 0.07276606059188014),
                     (0.025, 0.9163098192537316, 0.03406277593671282),
                     (0.075, 0.8023995116666669, 0.06054864267962909),
                     (0.175, 0.7396838009090908, 0.06740156784868169),
                     (0.225, 0.7097196516666667, 0.08077838127146267)]

    x_y_sd_values = sorted(low_p_x_y_sd_values, key=lambda x: x[0])
    x_values, y_values, std_dev_values = zip(*x_y_sd_values)


    plot_scatter_chart(x_values, y_values, std_dev_values, x_label="Connection probability", y_label="Cooperation rate")

if __name__ == "__main__":
    main()