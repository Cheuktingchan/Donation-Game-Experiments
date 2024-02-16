import csv
import argparse
import os
import numpy as np

def calculate_std_dev(csv_file):
    with open(csv_file, 'r') as file:
        lines = file.read().split('\n')
        data = [list(filter(None, row.split(';'))) for row in lines if row.strip()]
        modified_data = [[list(map(float, inner.split(', '))) for inner in outer] for outer in data]

        modified_data = np.array(modified_data)

        std_dev = np.std(modified_data, axis=0)
        
        return std_dev

def write_std_dev_to_file(std_dev, csv_file, output_file):
    with open(output_file, 'a', newline='') as file:
        writer = csv.writer(file, delimiter=',')
        writer.writerow(["File:", csv_file])
        writer.writerow(["Standard Deviation"])
        for sublist in std_dev:
            writer.writerow(sublist)

def main():
    parser = argparse.ArgumentParser(description='Calculate column standard deviation')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    directory, filename = os.path.split(csv_file)
    output_filename = "std_dev_" + filename  # Add "std_dev_" prefix
    output_file = os.path.join(directory, output_filename)
    std_dev = calculate_std_dev(csv_file)

    write_std_dev_to_file(std_dev, csv_file, output_file)

if __name__ == "__main__":
    main()