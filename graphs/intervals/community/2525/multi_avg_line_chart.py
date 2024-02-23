import pandas as pd
import matplotlib.pyplot as plt

def plot_line_chart_from_csv(csv_files, data_legends, y_columns, title="Line Chart", x_label="X-axis", y_label="Y-axis", output_file="graphs/images/csv_line_chart.png"):

    # Create a list to store DataFrames
    dfs = []

    # Read each CSV file into a DataFrame and store it in the list
    for csv_file in csv_files:
        df = pd.read_csv(csv_file, header=None)
        dfs.append(df)

    # Extract y values from each DataFrame
    y_values_list = []
    for df, y_column in zip(dfs, y_columns):
        y_values = df.iloc[:, y_column].tolist()
        y_values = [float(value) for value in y_values]  # Convert y values to float
        y_values_list.append(y_values)

    # Create x values using row numbers as x values (assuming all files have the same number of rows)
    x_values = [i for i in range(100, 100001, 100)]

    # Create line chart for each set of y values
    for i, y_values in enumerate(y_values_list):
        plt.plot(x_values, y_values, label=data_legends[i])  # Adjust label, marker, and color as needed

    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)

def main():
    # Example CSV files and columns (replace with your files and columns)
    csv_files = [
        "data/intervals/community/25-25-25-25/4,0.0/average_n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final0-25.csv",
        "data/intervals/community/25-25-25-25/4,0.0/average_n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final25-50.csv",
        "data/intervals/community/25-25-25-25/4,0.0/average_n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final50-75.csv",
        "data/intervals/community/25-25-25-25/4,0.0/average_n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final75-100.csv",
    ]

    data_legends = ["25", "50", "75", "100"]
    y_columns = [0, 0, 0, 0]  # Adjust column indices as needed

    plot_line_chart_from_csv(csv_files, data_legends, y_columns, title="0.75", x_label="Generations", y_label="Reward average (not cumulative)", output_file="graphs/images/3_2525_avg_reward_fin_0.0.png")

if __name__ == "__main__":
    main()