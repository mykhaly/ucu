import multiprocessing

import math
max_number_of_cores = multiprocessing.cpu_count()


def up(array, depth_level, number_of_elements_per_core, core_number):
    left_index = number_of_elements_per_core * core_number
    right_index = number_of_elements_per_core * (core_number + 1)
    step = 2 ** depth_level
    for index in range(left_index + step - 1, right_index, step):
        array[index] += array[index - step // 2]


def down(array, depth_level, number_of_elements_per_core, core_number):
    left_index = number_of_elements_per_core * core_number
    right_index = number_of_elements_per_core * (core_number + 1)
    step = 2 ** depth_level
    for index in range(left_index + step - 1, right_index, step):
        tmp = array[index]
        array[index] += array[index - step // 2]
        array[index - step // 2] = tmp


def get_number_of_cores_and_elements_per_core(length, depth_level):
    number_of_elements_per_core = 2 ** depth_level
    number_of_cores = length // number_of_elements_per_core
    if number_of_cores > max_number_of_cores:
        number_of_cores = max_number_of_cores
        number_of_elements_per_core = length // number_of_cores
    return number_of_cores, number_of_elements_per_core


def parallel(array, print_enabled=False):
    length = len(array)
    depth = int(math.log(length, 2)) + 1

    if print_enabled:
        print("UP STARTED")
    for depth_level in range(1, depth + 1):
        processes = []
        number_of_cores, number_of_elements_per_core = get_number_of_cores_and_elements_per_core(
            length, depth_level)
        if print_enabled:
            string = "Depth level: {}\nNumber of cores in use: {}\nNumber of elements per core: {}"
            print(string.format(depth_level, number_of_cores, number_of_elements_per_core))

        for core_number in range(number_of_cores):
            arguments = array, depth_level, number_of_elements_per_core, core_number
            process = multiprocessing.Process(target=up, args=arguments)
            process.start()
            processes.append(process)
        for process in processes:
            process.join()

    sum_of_all_elements = array[-1]
    array[-1] = 0

    if print_enabled:
        print("DOWN STARTED")
    for depth_level in range(depth, 0, -1):
        processes = []
        number_of_cores, number_of_elements_per_core = get_number_of_cores_and_elements_per_core(
            length, depth_level)
        if print_enabled:
            string = "Depth level: {}\nNumber of cores in use: {}\nNumber of elements per core: {}"
            print(string.format(depth_level, number_of_cores, number_of_elements_per_core))

        for core_number in range(number_of_cores):
            arguments = array, depth_level, number_of_elements_per_core, core_number
            process = multiprocessing.Process(target=down, args=arguments)
            process.start()
            processes.append(process)
        for process in processes:
            process.join()

    return array[1:] + [sum_of_all_elements]


def sequential(array):
    result = [array[0]]
    for i in range(1, len(array)):
        result.append(result[i - 1] + array[i])
    return result
