import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import sys

def load_data(file_paths):
    data = []
    for file_path in file_paths:
        df = pd.read_csv(file_path, header=None)
        y_values = df.iloc[0].str.split(';') # takes first data from the 100 runs
        y_values = [float(value) for value in y_values]  # Convert y values to float
        data.append(y_values)
    return data

def plot_line_chart(file_paths, save_path):
    # Load data from file paths
    data = load_data(file_paths)

    # Create x values using row numbers as x values
    x_values = [i for i in range(100, 100001, 100)]

    # Plot data from each file path
    plt.figure(figsize=(10, 6))
    for i, y_values in enumerate(data):
        label = f'Data {i+1}'
        plt.plot(x_values, y_values, label=label)

    # Add labels, legend, and title
    plt.xlabel('Generations')
    plt.ylabel('Y-axis label')  # Provide appropriate y-axis label
    plt.legend()
    plt.grid(True)

    # Save the plot as an image
    plt.savefig(save_path)

    # Show the plot
    plt.show()

if __name__ == "__main__":
    # Check if both file_paths and save_path are provided as command-line arguments
    if len(sys.argv) != 4:
        print("Usage: python script.py <file_path1> <file_path2> <save_path>")
        sys.exit(1)

    # Extract file_paths and save_path from command-line arguments
    file_paths = [sys.argv[1], sys.argv[2]]
    save_path = sys.argv[3]

    # Call the function with the provided paths
    plot_line_chart(file_paths, save_path)