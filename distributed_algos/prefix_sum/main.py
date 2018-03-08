import multiprocessing
import random
from time import time
from matplotlib import pyplot as plt
from prefix_sum import parallel, sequential

random.seed(0)


seq_times = []
par_times = []
powers = range(21)

for power in powers:
    array_sze = 2 ** power
    array = []
    for i in range(array_sze):
        array.append(random.randint(-1000, 1000))

    t0 = time()
    result_seq = sequential(array)
    time_diff = time() - t0

    print("Resulting time of sequential algorithm for array of size {} is: {} seconds".format(
        len(result_seq), time_diff))
    seq_times.append(time_diff)
    array = multiprocessing.Array("i", array)

    t0 = time()
    result_par = parallel(array)
    time_diff = time() - t0
    print("Resulting time of parallel algorithm for array of size {} is: {} seconds".format(
        len(result_par), time_diff))
    par_times.append(time_diff)
    for i in range(array_sze):
        if result_par[i] != result_seq[i]:
            print("Resulting rrays are not equal, mismatched index: {}".format(i))
            break

plt.plot(powers, seq_times)
plt.plot(powers, par_times)
plt.legend(["Sequential", "Parallel"])
plt.xlabel("Log2 of the size of an array")
plt.ylabel("Running time")
plt.show()
