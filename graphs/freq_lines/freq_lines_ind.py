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

    # Plotting the line chart
    plt.figure(figsize=(10, 6))

    colormap = LinearSegmentedColormap.from_list('RedToBlue', ['red','white', 'blue'])
    # Plot each column as a line
    for i, col in enumerate(df.columns):
        color_value = (len(df.columns) - i) / (len(df.columns) - 1)  # Scale color based on index
        plt.plot(df.index * 100, df[col], label=f'{col - 5}', color=colormap(color_value))

    # Add labels and title
    plt.xlabel('Generations')
    plt.ylabel('Relative frequency')
    plt.grid(True)

    # Add colorbar as legend
    sm = ScalarMappable(cmap=colormap)
    sm.set_array([])  # fake an empty array
    cbar = plt.colorbar(sm, ticks=[-1, 0, 1], orientation='vertical')
    cbar.ax.set_yticklabels(['6', '6', '-5'])
    cbar.set_label('Strategy')

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