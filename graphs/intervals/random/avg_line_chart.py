import pandas as pd
import matplotlib.pyplot as plt

def plot_line_chart_from_csv(csv_file, y_column, title="Line Chart", x_label="X-axis", y_label="Y-axis", output_file="graphs/images/csv_line_chart.png"):

    # Extract x and y values from the DataFrame
    df = pd.read_csv(csv_file, header=None)

    y_values = df.values.flatten().tolist()
    y_values = [float(value) for value in y_values]  # Convert y values to float

    # Create x values using row numbers as x values
    x_values = [i for i in range(100, 100001, 100)]

    # Create line chart with error bars
    plt.plot(x_values, y_values, color='blue')  # Plot average line separately
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)

def main():
    # Example CSV files (replace with your files)
    csv_file = "data/intervals/random/0.5/std_dev_n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[2.0, 0.5]_intervals100_endNFalse_coop-rate0-100.csv"

    y_column = 0

    plot_line_chart_from_csv(csv_file, y_column, title="", x_label="Generations", y_label="Cooperation rate", output_file="graphs/images/2_0.5_interval_avg_sd.png")

if __name__ == "__main__":
    main()