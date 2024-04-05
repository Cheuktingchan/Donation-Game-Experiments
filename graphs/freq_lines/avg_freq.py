import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import LinearSegmentedColormap
from matplotlib.cm import ScalarMappable
import sys

def first_row(csv_file):
    with open(csv_file, 'r') as file:
        lines = file.read().split('\n')
        data = [list(filter(None, row.split(';'))) for row in lines if row.strip()]
        modified_data = [[list(map(float, inner.split(', '))) for inner in outer] for outer in data]

        modified_data = np.array(modified_data)

        averages = np.mean(modified_data, axis=0)

        return modified_data[0]

def plot_line_chart(file_path, save_path):
    # Load the CSV file into a DataFrame
    df = pd.DataFrame(data=first_row(file_path))

    # Sum of products of each column
    sum_of_products = np.sum(df.values * (df.columns.values-5), axis=1)

    # Plotting the sum of products as a single line
    plt.figure(figsize=(10, 6))

    plt.plot(df.index * 100, sum_of_products, label='Average strategy', color="red")
    
    df = pd.read_csv("data/intervals/original/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net0_intervals100_endNFalse_reward-variances0-100.csv", header=None)

    # Extract y values from the DataFrame
    y_values = df.iloc[0].str.split(';')[0][:-1] # takes first data from the 100 runs
    y_values = [float(value) for value in y_values]  # Convert y values to float

    # Create x values using row numbers as x values
    x_values = [i for i in range(100, 100001, 100)]

    # Create line chart
    plt.plot(x_values, y_values, label="Reward variance", color='blue')  # Adjust marker and color as needed
    plt.legend()
    plt.grid(True)

    # Add labels and title
    plt.xlabel('Generations')
    plt.ylabel('')
    plt.grid(True)

    # Save the plot as an image
    plt.savefig(save_path)

    # Show the plot
    plt.show()

if __name__ == "__main__":
    # Check if both file_path and save_path are provided as command-line arguments
    if len(sys.argv) != 3:
        print("Usage: python script.py <file_path> <save_path>")
        sys.exit(1)

    # Extract file_path and save_path from command-line arguments
    file_path = sys.argv[1]
    save_path = sys.argv[2]

    # Call the function with the provided paths
    plot_line_chart(file_path, save_path)