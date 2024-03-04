import csv
import matplotlib.pyplot as plt
import sys

def create_histogram(csv_file_path, save_path):
    # Read the CSV file
    data = []

    with open(csv_file_path, 'r') as csv_file:
        csv_reader = csv.reader(csv_file)
        for row in csv_reader:
            data.append(float(row[0]))

    # Create a histogram plot
    plt.hist(data, bins=[i/100 for i in range(101)], edgecolor='black')  # Bins in 0.01 intervals
    plt.xlabel('Cooperation rate')
    plt.ylabel('Frequency')

    # Save the plot
    plt.savefig(save_path)  # Save with the provided save path

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python script.py <csv_file_path> <save_path>")
        sys.exit(1)

    csv_file_path = sys.argv[1]
    save_path = sys.argv[2]

    create_histogram(csv_file_path, save_path)