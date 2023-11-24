import csv
import argparse

def average_columns(csv_file):
    with open(csv_file, 'r') as file:
        reader = csv.reader(file)

        header = next(reader)
        data = list(reader)

        columns = zip(*data)

        averages = [sum(map(float, col)) / len(col) for col in columns]

        return header, averages

def main():
    parser = argparse.ArgumentParser(description='Calculate column averages from a CSV file.')
    parser.add_argument('csv_file', help='Path to the CSV file')

    args = parser.parse_args()
    csv_file = args.csv_file

    header, averages = average_columns(csv_file)

    # Print the results
    print("Column names:", header)
    print("Averages:", averages)

if __name__ == "__main__":
    main()