import csv
import json
import re
import os
import sys

def remove_common_substring(s, start, end):
    pattern = re.escape(start) + "(.*?)" + re.escape(end)
    match = re.search(pattern, s)
    if match:
        return s.replace(match.group(), "")
    return s

if len(sys.argv) != 2:
    print("Usage: python script.py <csv_file_path>")
    sys.exit(1)

csv_file_path = sys.argv[1]
json_output_path = os.path.splitext(csv_file_path)[0] + ".json"

data = {}

with open(csv_file_path, newline='') as csvfile:
    csv_reader = csv.reader(csvfile)
    current_file = None

    for row in csv_reader:
        if row[0] == 'File:':
            current_file = row[1]
        else:
            current_file = remove_common_substring(current_file, "data/", "endNFalse_")
            current_file = remove_common_substring(current_file, "0-100", "csv")
            data[current_file] = list(map(float, row[1:]))

# Convert to JSON
with open(json_output_path, 'w') as jsonfile:
    json.dump(data, jsonfile, indent=2)

#print(f"Conversion completed. JSON file saved at: {json_output_path}")