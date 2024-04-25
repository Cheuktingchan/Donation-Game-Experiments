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
    plt.plot(df.index * 100, sum_of_products, label='Average strategy 0-50', color="red")

    # Add labels, title, legend, and grid
    plt.xlabel('Generations')
    plt.ylabel('Average strategy')
    plt.legend(loc='best')
    plt.grid(True)

    # Save and display the plot
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