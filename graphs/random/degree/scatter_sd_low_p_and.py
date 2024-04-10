import matplotlib.pyplot as plt
import numpy as np

def plot_scatter_chart(x_values, y_values, std_dev_values, x_label="X-axis", y_label="Y-axis", output_file="graphs/images/2_n_coop_sd_low_p_present.png"):

    plt.errorbar(
    x_values,
    y_values,
    yerr=std_dev_values,  # Use 0 if standard deviation data is not available
    marker='o',
    label=f'Data points',
    color="grey",
    markerfacecolor="blue",
    markeredgecolor="blue",
    linestyle=''
)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    #plt.legend()

    plt.savefig(output_file)
    plt.show()

def main():
    x_values = [0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.875, 0.9375, 1]
    y_values = [0.8046665847000005, 0.7554818443000001, 0.7271982410000003,  0.7141386776, 0.7197897540000001, 0.6961270799999998, 0.6950158699999999, 0.6800241454999997,0.6973291651000002, 0.6893098767999999, 0.6841757777, 0.6900497680000004, 0.6943935737, 0.6932597566000002, 0.6689444309999999, 0.6791909987999998]
    std_dev_values = [0.06499067182769114, 0.07585734369742601, 0.07852258098951014, 0.0789239563882039, 0.07406179768059493, 0.06653337294903484, 0.08467540050017415, 0.08732846920725632, 0.07800970178053102, 0.0789239563882039, 0.07798336071387241, 0.0701525176590983, 0.0742915934521418, 0.07573224412088043, 0.0829292211214393, 0.06844515973655357]
    
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

    x_y_sd_values = sorted(list(zip(x_values, y_values, std_dev_values)) + low_p_x_y_sd_values, key=lambda x: x[0])
    x_values, y_values, std_dev_values = zip(*x_y_sd_values)


    plot_scatter_chart(x_values, y_values, std_dev_values, x_label="Connection probability", y_label="Cooperation rate")

if __name__ == "__main__":
    main()