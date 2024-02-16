import pandas as pd
import argparse
import os

def calculate_and_save_std_dev(file_path):
    # Read the CSV file into a pandas DataFrame without headers
    df = pd.read_csv(file_path, sep=';', header=None)

    # Drop columns with missing values (NaN)
    df = df.dropna(axis=1)

    # Calculate the column-wise standard deviation
    column_std_dev = df.std(axis=0)

    # Get the original file name and directory
    file_dir, file_name = os.path.split(file_path)

    # Add "std_dev_" prefix to the original file name
    output_file_name = "std_dev_" + file_name

    # Save the standard deviations to a new CSV file
    output_file_path = os.path.join(file_dir, output_file_name)
    column_std_dev.to_csv(output_file_path, index=False, header=False)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Calculate column-wise standard deviation from a CSV file and save to a new file.')
    parser.add_argument('file', help='Path to the CSV file')

    args = parser.parse_args()
    calculate_and_save_std_dev(args.file)