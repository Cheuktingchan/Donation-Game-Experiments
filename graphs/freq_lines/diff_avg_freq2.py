import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import sys

def get_data_lists(csv_file):
    with open(csv_file, 'r') as file:
        with open(csv_file[:-8] + "50-100.csv", 'r') as file1:
            lines = file.read().split('\n')
            data = [list(filter(None, row.split(';'))) for row in lines if row.strip()]
            modified_data = [[list(map(float, inner.split(', '))) for inner in outer] for outer in data]
            modified_data = np.array(modified_data)

            lines1 = file1.read().split('\n')
            data1 = [list(filter(None, row.split(';'))) for row in lines1 if row.strip()]
            modified_data1 = [[list(map(float, inner.split(', '))) for inner in outer] for outer in data1]
            modified_data1 = np.array(modified_data1)

            return (modified_data, modified_data1)
    
def plot_line_chart(file_path, additional_file, save_path):
    # Load the CSV files into DataFrames
    d1, d2 = get_data_lists(file_path)
    diff = []
    for i in range(len(d1)):
        df1 = pd.DataFrame(data=d1[i])
        df2 = pd.DataFrame(data=d2[i])
        # Sum of products of each column
        sum_of_products1 = np.sum(df1.values * (df1.columns.values-5), axis=1)
        sum_of_products2 = np.sum(df2.values * (df2.columns.values-5), axis=1)
        diff.append([abs(x - y) for x, y in zip(sum_of_products1, sum_of_products2)])
    
    averages = np.mean(diff, axis=0)

    print(np.mean(averages,axis=0), np.std(averages, axis=0))
    # Plotting the sum of products as a single line
    plt.figure(figsize=(10, 6))
    plt.plot(df1.index * 100, averages, label='p=0', color="red")

    d1, d2 = get_data_lists(additional_file)
    diff = []
    for i in range(len(d1)):
        df1 = pd.DataFrame(data=d1[i])
        df2 = pd.DataFrame(data=d2[i])
        # Sum of products of each column
        sum_of_products1 = np.sum(df1.values * (df1.columns.values-5), axis=1)
        sum_of_products2 = np.sum(df2.values * (df2.columns.values-5), axis=1)
        diff.append([abs(x - y) for x, y in zip(sum_of_products1, sum_of_products2)])
    
    averages = np.mean(diff, axis=0)

    print(np.mean(averages,axis=0), np.std(averages, axis=0))

    plt.plot(df1.index * 100, averages, label='p=0.01', color="blue")

    # Add labels, title, legend, and grid
    plt.xlabel('Generations')
    plt.ylabel('Average strategy difference')
    plt.grid(True)
    plt.legend()
    # Save and display the plot
    plt.savefig(save_path)
    plt.show()

if __name__ == "__main__":
    # Check if file_path, additional_file, and save_path are provided as command-line arguments
    if len(sys.argv) != 4:
        print("Usage: python script.py <file_path> <additional_file> <save_path>")
        sys.exit(1)

    # Extract file_path, additional_file, and save_path from command-line arguments
    file_path = sys.argv[1]
    additional_file = sys.argv[2]
    save_path = sys.argv[3]

    # Call the function with the provided paths
    plot_line_chart(file_path, additional_file, save_path)