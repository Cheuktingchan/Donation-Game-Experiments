import csv
import argparse
import os

def average_columns(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)

        header = next(reader)
        data = list(reader)

        columns = zip(*data)

        averages = [sum(map(float, col)) / len(col) for col in columns]

        return header, averages

def write_averages_to_file(header, averages, csv_file, output_file):
    with open(output_file, 'a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["File:"] + [csv_file])
        writer.writerow(["Column names"] + header)
        writer.writerow(["Averages"] + averages)

def main():
    parser = argparse.ArgumentParser(description='Calculate column averages')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    directory, filename = os.path.split(csv_file)
    output_file = os.path.join(directory, "averages.csv")
    header, averages = average_columns(csv_file)

    write_averages_to_file(header, averages, csv_file, output_file)
    print(f"Averages written to {output_file}")
    print("Column names:", header)
    print("Averages:", averages)

if __name__ == "__main__":
    main()