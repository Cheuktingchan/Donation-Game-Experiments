import csv
import argparse
import os
import numpy as np

def average_columns(csv_file):
    with open(csv_file, 'r') as file:
        lines = file.read().split('\n')
        data = [list(filter(None, row.split(';'))) for row in lines if row.strip()]
        modified_data = [[list(map(float, inner.split(', '))) for inner in outer] for outer in data]

        # Convert the original array to a numpy array
        modified_data = np.array(modified_data)

        # Calculate the mean along axis 1
        averages = np.mean(modified_data, axis=0)

        # Print the averages
        print(len(averages))
        
        return averages

def write_averages_to_file(averages, csv_file, output_file):
    with open(output_file, 'a', newline='') as file:
        writer = csv.writer(file, delimiter=',')
        writer.writerow(["File:", csv_file])
        writer.writerow(["Averages"])
        for sublist in averages:
            writer.writerow(sublist)

def main():
    parser = argparse.ArgumentParser(description='Calculate column averages')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    directory, filename = os.path.split(csv_file)
    output_file = os.path.join(directory, "averages_k.csv")
    averages = average_columns(csv_file)

    write_averages_to_file(averages, csv_file, output_file)

if __name__ == "__main__":
    main()