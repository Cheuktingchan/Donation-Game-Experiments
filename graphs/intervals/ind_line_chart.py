import pandas as pd
import matplotlib.pyplot as plt

def plot_line_chart_from_csv(csv_file, y_column, title="Line Chart", x_label="X-axis", y_label="Y-axis", output_file="graphs/images/csv_line_chart.png"):

    # Extract x and y values from the DataFrame
    df = pd.read_csv(csv_file, header=None)

    # Extract y values from the DataFrame
    y_values = df.iloc[y_column].str.split(';')[0][:-1] # takes first data from the 100 runs
    y_values = [float(value) for value in y_values]  # Convert y values to float

    # Create x values using row numbers as x values
    x_values = [i for i in range(100, 100001, 100)]

    # Create line chart
    plt.plot(x_values, y_values, label="Data points", color='blue')  # Adjust marker and color as needed
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)

def main():
    # Example CSV file (replace with your file)
    csv_file = "data/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g1000_net[0.0]_intervals1_endNFalse_reward-variances0-100.csv"

    y_column = 0

    plot_line_chart_from_csv(csv_file, y_column, title="", x_label="Generations", y_label="Reward variance", output_file="graphs/images/reward_var/0_sing_g1000.png")

if __name__ == "__main__":
    main()