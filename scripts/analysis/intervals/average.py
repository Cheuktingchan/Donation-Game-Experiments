import csv
import argparse
import os

def average_rows(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file, delimiter=';')

        data = list(reader)

        averages = []

        for row in data:
            numeric_values = [float(value) for value in row if value.replace('.', '', 1).isdigit()]
            row_average = sum(numeric_values) / len(numeric_values) if numeric_values else 0.0
            averages.append(row_average)

        return averages

def write_averages_to_file(averages, csv_file, output_file):
    with open(output_file, 'a', newline='') as file:
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
    averages = average_rows(csv_file)

    write_averages_to_file(averages, csv_file, output_file)
    print(f"Averages written to {output_file}")
    print("Averages:", averages)

if __name__ == "__main__":
    main()