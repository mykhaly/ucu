import math
from collections import defaultdict
from multiprocessing import Manager, Process


def map_parallel(input_list, output_dict, lock):
    for number in input_list:
        try:
            output_dict[number] = output_dict[number] + "1"
        except KeyError:
            output_dict[number] = "1"


def map_seq(input_list, output_dict):
    for number in input_list:
        try:
            output_dict[number] = output_dict[number] + "1"
        except KeyError:
            output_dict[number] = "1"


def reduce_parallel(input_keys, input_dict, output_dict):
    for key in input_keys:
        summ = 0
        for _ in input_dict[key]:
            summ += 1
        output_dict[key] = summ


def reduce_seq(input_dict, output_dict):
    for k, v in input_dict.items():
        summ = 0
        for _ in v:
            summ += 1
        output_dict[k] = summ


def sort_par(dictionaries):
    keys = set()
    result = {}
    for dictionary in dictionaries:
        keys = keys | set(dictionary.keys())
    for key in keys:
        for dictionary in dictionaries:
            if key in dictionary.keys():
                if key not in result.keys():
                    result[key] = dictionary[key]
                else:
                    result[key] = result[key] + dictionary[key]
    return result


def get_number_of_cores_and_elements_per_core(length, max_number_of_cores):
    if length > max_number_of_cores:
        number_of_cores = max_number_of_cores
        number_of_elements_per_core = int(math.ceil(length / number_of_cores))
    else:
        number_of_cores = length
        number_of_elements_per_core = 1
    return number_of_cores, number_of_elements_per_core


def map_and_reduce_par(input_array, max_number_of_cores):
    manager = Manager()
    lock = manager.Lock()
    number_of_mappers, number_of_elements_per_mapper = \
        get_number_of_cores_and_elements_per_core(len(input_array), max_number_of_cores)

    # print("Number of mappers: {}".format(number_of_mappers))

    map_outputs = []
    for i in range(number_of_mappers):
        map_outputs.append(manager.dict())
    # reduce_output = manager.dict()

    processes = []

    for core_number in range(number_of_mappers):
        begin = number_of_elements_per_mapper * core_number
        end = begin + number_of_elements_per_mapper
        arguments = input_array[begin:end], map_outputs[core_number], lock
        process = Process(target=map_parallel, args=arguments)
        process.start()
        processes.append(process)

    for process in processes:
        process.join()
    reduce_input = sort_par(map_outputs)
    reduce_input_keys = sorted(reduce_input.keys())
    reduce_input = manager.dict(reduce_input)
    number_of_keys_per_reducer = math.ceil(len(reduce_input_keys) / max_number_of_cores)
    number_of_reducers = int(min(math.ceil(len(reduce_input_keys) / number_of_keys_per_reducer),
                             max_number_of_cores))

    reduce_outputs = []
    processes = []
    # print("Number of reducers: {}".format(number_of_reducers))
    for core_number in range(number_of_reducers):
        reduce_output = manager.dict()
        reduce_outputs.append(reduce_output)
        begin = number_of_keys_per_reducer * core_number
        end = begin + number_of_keys_per_reducer
        arguments = reduce_input_keys[begin:end], reduce_input, reduce_output
        process = Process(target=reduce_parallel, args=arguments)
        process.start()
        processes.append(process)

    for process in processes:
        process.join()

    result = sort_par(reduce_outputs)
    # print("Par map output: {}".format(reduce_input))
    # print("Par reduce output: {}".format(reduce_output))

    return result


def map_and_reduce_seq(input_array):
    map_output = {}
    reduce_output = {}
    map_seq(input_array, map_output)
    reduce_seq(map_output, reduce_output)
    # print("Seq map output: {}".format(map_output))
    # print("Seq reduce output: {}".format(reduce_output))

    return reduce_output
