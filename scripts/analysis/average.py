import csv
import argparse
import os

def average_columns(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)
        data = list(reader)
        columns = zip(*data)
        averages = [sum(map(float, col)) / len(col) for col in columns]
        return averages

def write_averages_to_file(averages, csv_file, output_file):
    with open(output_file, 'w', newline='') as file:  # Use 'w' mode to override the file
        writer = csv.writer(file)
        writer.writerow(["File:"] + [csv_file])
        writer.writerow(["Averages"] + averages)

def main():
    parser = argparse.ArgumentParser(description='Calculate column averages')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    directory, filename = os.path.split(csv_file)
    output_file = os.path.join(directory, "averages.csv")
    averages = average_columns(csv_file)

    write_averages_to_file(averages, csv_file, output_file)
    print(f"Averages written to {output_file}")
    print("Averages:", averages)

if __name__ == "__main__":
    main()