import matplotlib.pyplot as plt

def plot_line_chart(x_values, y_values, title="Line Chart", x_label="X-axis", y_label="Y-axis", output_file="graphs/random/degree/line_chart.png"):
    plt.plot(x_values, y_values, label="Line Chart")
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)
    plt.close()

def main():
    x_values = [0.0625, 0.125, 0.25, 0.5, 1]
    y_values = [0.8046665847000005, 0.7554818443000001, 0.7141386776, 0.6800241454999997, 0.6791909987999998]

    plot_line_chart(x_values, y_values, title="Chart showing cooperation rate in relation to p", x_label="p", y_label="cooperation rate")

if __name__ == "__main__":
    main()