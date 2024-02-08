import pandas as pd

def calculate_differences(file1_path, file2_path):
    # Read CSV files into DataFrames
    df1 = pd.read_csv(file1_path, header=None)
    df2 = pd.read_csv(file2_path, header=None)

    # Assuming both DataFrames have the same structure (same number of columns and rows)
    # If the structure is different, you might need to align the data accordingly

    # Calculate the differences
    differences = df1 - df2

    print(df1)
    print(df2)
    return differences

if __name__ == "__main__":
    file1_path = "data/bipartite/50-50/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net1_endNFalse_coop-rate0-50.csv"
    file2_path = "data/bipartite/50-50/n100_m300_q1.0_mr0.001_ea0.000_ep0.000_nsFalse_genFalse_faFalse_frFalse_g100000_net1_endNFalse_coop-rate50-100.csv"

    result = calculate_differences(file1_path, file2_path)

    print("Differences:")
    print(result)