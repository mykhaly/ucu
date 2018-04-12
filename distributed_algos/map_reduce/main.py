import random
import time
from multiprocessing import Manager, cpu_count

from helper import map_and_reduce_par, map_and_reduce_seq

max_number_of_cores = cpu_count()

manager = Manager()

par_times = []
seq_times = []
powers = range(0, 21)
for i in powers:
    number_of_elements = 2 ** i
    array = []
    for _ in range(number_of_elements):
        array.append(random.randint(0, 10))

    print("Input array: {}...".format(array[:20]))  # in order not to print too big string

    time0 = time.time()
    output_par = map_and_reduce_par(array, max_number_of_cores)
    time1 = time.time()
    output_seq = map_and_reduce_seq(array)
    time2 = time.time()

    par_times.append(time1 - time0)
    seq_times.append(time2 - time1)

    for key_par, value_par in output_par.items():
        value_seq = output_seq.get(key_par, "")
        if value_par != value_seq:
            if value_par == 0:
                continue
            print("Results are not equal. Key: {}, value_par: {}, value_seq: {}".format(
                key_par, value_par, output_seq[key_par]))

    print("Result: {}".format(output_par))
    print("Size: {}, parallel: {}, sequential: {}\n".format(number_of_elements, time1 - time0, time2 - time1))

# plt.plot(powers, seq_times)
# plt.plot(powers, par_times)
# plt.legend(["Sequential", "Parallel"])
# plt.xlabel("Log2 of a size of an array")
# plt.ylabel("Running time")
# plt.show()


