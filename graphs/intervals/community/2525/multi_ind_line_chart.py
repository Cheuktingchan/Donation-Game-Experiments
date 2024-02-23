import pandas as pd
import matplotlib.pyplot as plt
import os

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
    # Example CSV files (replace with your files)
    csv_files = [
        "data/intervals/community/25-25-25-25/4,0.0/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final0-25.csv",
        "data/intervals/community/25-25-25-25/4,0.0/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final25-50.csv",
        "data/intervals/community/25-25-25-25/4,0.0/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final50-75.csv",
        "data/intervals/community/25-25-25-25/4,0.0/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net[3.0, 4.0, 0.0]_intervals100_endNFalse_reward-final75-100.csv",
    ]

    y_column = 0

    # Accumulate data from all CSV files
    all_x_values = []
    all_y_values = []
    all_titles = []

    for i, csv_file in enumerate(csv_files):
        df = pd.read_csv(csv_file, header=None)

        # Extract y values from the DataFrame
        y_values = df.iloc[y_column].str.split(';')[0][:-1]
        y_values = [float(value) for value in y_values]

        # Create x values using row numbers as x values
        x_values = [i for i in range(100, 100001, 100)]

        all_x_values.append(x_values)
        all_y_values.append(y_values)

        # Extract filename without extension to use as a title
        title = os.path.splitext(os.path.basename(csv_file))[0]
        all_titles.append(title)

    # Create line chart with all datasets on the same plot
    for x_values, y_values, title in zip(all_x_values, all_y_values, all_titles):
        plt.plot(x_values, y_values, label=title)

    plt.title("Combined Line Chart")
    plt.xlabel("Generations")
    plt.ylabel("Cooperation rate")
    plt.legend()
    plt.grid(True)
    plt.savefig("graphs/images/coop/combined_line_chart.png")

if __name__ == "__main__":
    main()