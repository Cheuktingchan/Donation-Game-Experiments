import json
import matplotlib.pyplot as plt
import numpy as np
import os

def load_json_files(file_paths):
    data_dict_list = []

    for file_path in file_paths:
        with open(file_path, 'r') as file:
            try:
                # Load JSON data from the file into a dictionary
                data_dict = json.load(file)

                # Append the dictionary to the list
                data_dict_list.append(data_dict)

                print(f"Loaded data from {file_path}")
            except json.JSONDecodeError as e:
                print(f"Error decoding JSON in {file_path}: {e}")

    return data_dict_list

def load_std_dev_files(file_paths):
    std_dev_list = []

    for file_path in file_paths:
        with open(file_path, 'r') as file:
            try:
                # Load JSON data from the file into a list or array
                std_dev_data = json.load(file)

                # Append the standard deviation data to the list
                std_dev_list.append(std_dev_data)

                print(f"Loaded standard deviation data from {file_path}")
            except json.JSONDecodeError as e:
                print(f"Error decoding JSON in {file_path}: {e}")

    return std_dev_list

def plot_attributes_with_error_bars(data_dict_list, std_dev_list, x_values, x_axis):
    # Get the list of attribute names excluding 'kAvFreq' and 'kFinFreq'
    attribute_names = set()
    for data_dict in data_dict_list:
        attribute_names.update(key for key in data_dict.keys() if key not in ['kAvFreq', 'kFinFreq'])

    # Iterate through each attribute
    for attribute_name in attribute_names:
        plt.figure()
        
        # Plot each dictionary's attribute values with error bars
        for i, (data_dict, std_dev_data) in enumerate(zip(data_dict_list, std_dev_list)):
            if attribute_name in data_dict and attribute_name not in ['kAvFreq', 'kFinFreq']:
                plt.errorbar(
                    x_values[i],
                    data_dict[attribute_name],
                    yerr=std_dev_data.get(attribute_name, 0),  # Use 0 if standard deviation data is not available
                    marker='o',
                    label=f'Graph {i+1}',
                    color="grey",
                    markerfacecolor="blue",
                    markeredgecolor="blue"
                )

        # Customize the plot as needed (add titles, labels, etc.)
        plt.xlabel(x_axis)

        if attribute_name == "coop-rate":
            y_label = "Cooperation rate"
        elif attribute_name == "reward-averages":
            y_label = "Reward averages (all generations)"
        elif attribute_name == "reward-final":
            y_label = "Reward average (final generation)"
        elif attribute_name == "reward-variances":
            y_label = "Reward variances (all generations)"
        plt.ylabel(y_label)
        #plt.legend()
        if x_axis == "Number of initial nodes":
            plt.xticks(np.arange(min(x_values), max(x_values) + 1, 2))

        # Show or save the plot (you can customize this part)#
        save_filename = f"{attribute_name.replace(' ', '_')}_error_bars_plot.png"
        save_filepath = os.path.join('graphs/images', save_filename)
        plt.savefig(save_filepath)
        plt.show()
        
random_p = ['data/random/0.0625/global/averages.json', 
                   'data/random/0.125/global/averages.json',
                   'data/random/0.1875/averages.json',
                   'data/random/0.25/global/averages.json',
                   'data/random/0.3125/averages.json',
                   'data/random/0.375/averages.json',
                   'data/random/0.4375/averages.json',
                   'data/random/0.5/global/averages.json',
                   'data/random/0.5625/averages.json',
                   'data/random/0.625/averages.json',
                   'data/random/0.6875/averages.json',
                   'data/random/0.75/averages.json',
                   'data/random/0.8125/averages.json',
                   'data/random/0.875/averages.json',
                   'data/random/0.9375/averages.json',
                   'data/original/global/averages.json'
                   ]

SD_random_p = ['data/random/0.0625/global/SDs.json', 
                   'data/random/0.125/global/SDs.json',
                   'data/random/0.1875/SDs.json',
                   'data/random/0.25/global/SDs.json',
                   'data/random/0.3125/SDs.json',
                   'data/random/0.375/SDs.json',
                   'data/random/0.4375/SDs.json',
                   'data/random/0.5/global/SDs.json',
                   'data/random/0.5625/SDs.json',
                   'data/random/0.625/SDs.json',
                   'data/random/0.6875/SDs.json',
                   'data/random/0.75/SDs.json',
                   'data/random/0.8125/SDs.json',
                   'data/random/0.875/SDs.json',
                   'data/random/0.9375/SDs.json',
                   'data/original/global/SDs.json'
                   ]

X_random_p = [0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.875, 0.9375, 1]

label_random_p = "Connection probability"

scale_free_ini = ['data/scale_free/4,2/averages.json', 
                   'data/scale_free/4,4(original)/global/averages.json',
                   'data/scale_free/4,6/averages.json',
                   'data/scale_free/4,8/averages.json',
                   'data/scale_free/4,10/averages.json',
                   'data/scale_free/4,12/averages.json',
                   'data/scale_free/4,14/averages.json',
                   'data/scale_free/4,16/averages.json',
                   'data/scale_free/4,18/averages.json',
                   'data/scale_free/4,20/averages.json',
                   ]

SD_scale_free_ini = ['data/scale_free/4,2/SDs.json', 
                   'data/scale_free/4,4(original)/global/SDs.json',
                   'data/scale_free/4,6/SDs.json',
                   'data/scale_free/4,8/SDs.json',
                   'data/scale_free/4,10/SDs.json',
                   'data/scale_free/4,12/SDs.json',
                   'data/scale_free/4,14/SDs.json',
                   'data/scale_free/4,16/SDs.json',
                   'data/scale_free/4,18/SDs.json',
                   'data/scale_free/4,20/SDs.json',
                   ]

X_scale_free_ini = [2,
                4,
                6,
                8,
                10,
                12,
                14,
                16,
                18,
                20]

label_scale_free_ini = "Number of initial nodes"

small_world_p = ['data/small_world/4,0.1/averages.json', 
                   'data/small_world/4,0.2/averages.json', 
                   'data/small_world/4,0.3/averages.json', 
                   'data/small_world/4,0.4/averages.json',
                   'data/small_world/4,0.5(original)/global/averages.json', 
                   'data/small_world/4,0.6/averages.json', 
                   'data/small_world/4,0.7/averages.json', 
                   'data/small_world/4,0.8/averages.json', 
                   'data/small_world/4,0.9/averages.json', 
                   'data/small_world/4,1/averages.json']

SD_small_world_p = ['data/small_world/4,0.1/SDs.json', 
                   'data/small_world/4,0.2/SDs.json', 
                   'data/small_world/4,0.3/SDs.json', 
                   'data/small_world/4,0.4/SDs.json',
                   'data/small_world/4,0.5(original)/global/SDs.json', 
                   'data/small_world/4,0.6/SDs.json', 
                   'data/small_world/4,0.7/SDs.json', 
                   'data/small_world/4,0.8/SDs.json', 
                   'data/small_world/4,0.9/SDs.json', 
                   'data/small_world/4,1/SDs.json']

X_small_world_p = [0.1,
                    0.2,
                    0.3,
                    0.4,
                    0.5,
                    0.6,
                    0.7,
                    0.8,
                    0.9,
                    1]

label_small_world_p = "Rewiring probability"

community_n = [
                   'data/community/2/averages.json', 
                   'data/community/3/averages.json', 
                   'data/community/4(original)/global/averages.json',
                   'data/community/5/averages.json', 
                   'data/community/6/averages.json', 
                   'data/community/7/averages.json', 
                   'data/community/8/averages.json', 
                   'data/community/9/averages.json', 
                   'data/community/10/averages.json']

SD_community_n = [
                   'data/community/2/SDs.json', 
                   'data/community/3/SDs.json', 
                   'data/community/4(original)/global/SDs.json',
                   'data/community/5/SDs.json', 
                   'data/community/6/SDs.json', 
                   'data/community/7/SDs.json', 
                   'data/community/8/SDs.json', 
                   'data/community/9/SDs.json', 
                   'data/community/10/SDs.json']

X_community_n = [2,
                    3,
                    4,
                    5,
                    6,
                    7,
                    8,
                    9,
                    10]

label_community_n = "Number of communities"

community_p = ['data/community/inter_p/4,0.1/averages.json',
                   'data/community/inter_p/4,0.2/averages.json', 
                   'data/community/inter_p/4,0.3/averages.json', 
                   'data/community/inter_p/4,0.4/averages.json',
                   'data/community/inter_p/4,0.5/averages.json', 
                   'data/community/inter_p/4,0.6/averages.json', 
                   'data/community/inter_p/4,0.7/averages.json', 
                   'data/community/inter_p/4,0.8/averages.json', 
                   'data/community/inter_p/4,0.9/averages.json',
                   'data/community/inter_p/4,1/averages.json']

SD_community_p = ['data/community/inter_p/4,0.1/SDs.json',
                   'data/community/inter_p/4,0.2/SDs.json', 
                   'data/community/inter_p/4,0.3/SDs.json', 
                   'data/community/inter_p/4,0.4/SDs.json',
                   'data/community/inter_p/4,0.5/SDs.json', 
                   'data/community/inter_p/4,0.6/SDs.json', 
                   'data/community/inter_p/4,0.7/SDs.json', 
                   'data/community/inter_p/4,0.8/SDs.json', 
                   'data/community/inter_p/4,0.9/SDs.json',
                   'data/community/inter_p/4,1/SDs.json']

X_community_p = [0.1,
                    0.2,
                    0.3,
                    0.4,
                    0.5,
                    0.6,
                    0.7,
                    0.8,
                    0.9,
                    1]

label_community_p = "External link probability"

small_world_nei = ['data/small_world/neighbour/1/averages.json', 
                   'data/small_world/neighbour/2/averages.json', 
                   'data/small_world/neighbour/3/averages.json', 
                   'data/small_world/neighbour/4/averages.json',
                   'data/small_world/neighbour/5/averages.json', 
                   'data/small_world/neighbour/6/averages.json', 
                   'data/small_world/neighbour/7/averages.json', 
                   'data/small_world/neighbour/8/averages.json', 
                   'data/small_world/neighbour/9/averages.json', 
                   'data/small_world/neighbour/10/averages.json']

SD_small_world_nei = ['data/small_world/neighbour/1/SDs.json', 
                   'data/small_world/neighbour/2/SDs.json', 
                   'data/small_world/neighbour/3/SDs.json', 
                   'data/small_world/neighbour/4/SDs.json',
                   'data/small_world/neighbour/5/SDs.json', 
                   'data/small_world/neighbour/6/SDs.json', 
                   'data/small_world/neighbour/7/SDs.json', 
                   'data/small_world/neighbour/8/SDs.json', 
                   'data/small_world/neighbour/9/SDs.json', 
                   'data/small_world/neighbour/10/SDs.json']

X_small_world_nei = [1,
                    2,
                    3,
                    4,
                    5,
                    6,
                    7,
                    8,
                    9,
                    10]

label_small_world_nei = "Neighbour distance"

std_dev_list = load_std_dev_files(SD_small_world_nei)
data_dict_list = load_json_files(small_world_nei)
plot_attributes_with_error_bars(data_dict_list, std_dev_list, X_small_world_nei, label_small_world_nei)