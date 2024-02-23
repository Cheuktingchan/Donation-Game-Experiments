sequence = [157, 167, 163, 161, 166, 164, 185, 178, 186, 200, 198, 216, 231, 254, 253, 276, 280, 293, 308, 348, 337, 374, 390, 420, 442, 467, 485, 513, 546, 573, 602, 624, 670, 699, 724, 774, 805, 829, 866]

# Calculate the moving average
window_size = 3
moving_average = [sum(sequence[i:i+window_size])/window_size for i in range(len(sequence)-window_size+1)]

# Calculate the differences between consecutive elements of the moving average
differences = [moving_average[i+1] - moving_average[i] for i in range(len(moving_average)-1)]

print(differences)