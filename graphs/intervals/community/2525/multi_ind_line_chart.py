import pandas as pd
import matplotlib.pyplot as plt
import os

def main():
    # Example CSV files (replace with your files)
    csv_files = [
        "data/intervals/community/g1000/25-25-25-25/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g1000_net[3.0, 4.0, 0.25]_intervals1_endNFalse_coop-rate0-25.csv",
        "data/intervals/community/g1000/25-25-25-25/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g1000_net[3.0, 4.0, 0.25]_intervals1_endNFalse_coop-rate25-50.csv",
        "data/intervals/community/g1000/25-25-25-25/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g1000_net[3.0, 4.0, 0.25]_intervals1_endNFalse_coop-rate50-75.csv",
        "data/intervals/community/g1000/25-25-25-25/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g1000_net[3.0, 4.0, 0.25]_intervals1_endNFalse_coop-rate75-100.csv",
    ]

    y_column = 0

    # Accumulate data from all CSV files
    all_x_values = []
    all_y_values = []
    all_titles = []

    for i, csv_file in enumerate(csv_files):
        df = pd.read_csv(csv_file, header=None)

        # Extract y values from the DataFrame
        y_values = df.iloc[y_column].str.split(';')[0][:100]
        y_values = [float(value) for value in y_values]

        # Create x values using row numbers as x values
        x_values = [i for i in range(1, 101, 1)]

        all_x_values.append(x_values)
        all_y_values.append(y_values)

        # Extract filename without extension to use as a title
        title = os.path.splitext(os.path.basename(csv_file))[0]
        all_titles.append(title)

    all_titles = ["0-25", "25-50", "50-75", "75-100"]

    # Create line chart with all datasets on the same plot
    for x_values, y_values, title in zip(all_x_values, all_y_values, all_titles):
        plt.plot(x_values, y_values, label=title)

    plt.xlabel("Generations")
    plt.ylabel("Cooperation rate")
    plt.legend()
    plt.grid(True)
    plt.savefig("graphs/images/coop/g100_25252525combined_line_chart.png")

if __name__ == "__main__":
    main()