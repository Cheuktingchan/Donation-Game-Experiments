import csv
import argparse
import statistics
import os

def std_dev_columns(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)

        data = list(reader)

        columns = zip(*data)

        std_devs = [statistics.pstdev(map(float, col)) for col in columns]

        return std_devs

def write_std_to_file(SDs, csv_file, output_file):
    with open(output_file, 'a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["File:"] + [csv_file])
        writer.writerow(["Averages"] + SDs)

def main():
    parser = argparse.ArgumentParser(description='Calculate column standard deviations')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    directory, filename = os.path.split(csv_file)
    output_file = os.path.join(directory, "SDs.csv")
    SDs = std_dev_columns(csv_file)

    write_std_to_file(SDs, csv_file, output_file)
    print(f"Averages written to {output_file}")
    print("SD:", SDs)
if __name__ == "__main__":
    main()