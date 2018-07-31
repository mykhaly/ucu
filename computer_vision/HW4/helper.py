import itertools as it

import numpy as np


def get_homography(source, destination):
    h = np.zeros((source.size, 9))
    for idx, ((x_src, y_src), (x_dest, y_dest)) in enumerate(zip(source, destination)):
        row_idx = idx * 2

        h[row_idx][0:3] = np.array([-x_src, -y_src, -1])
        h[row_idx][6:9] = np.array([x_dest * x_src, x_dest * y_src, x_dest])

        h[row_idx+1][3:6] = np.array([-x_src, -y_src, -1])
        h[row_idx+1][6:9] = np.array([y_dest * x_src, y_dest * y_src, y_dest])

    evs, evcs = np.linalg.eig(np.dot(h.T, h))
    return evcs.T[np.argmin(evs)].reshape(3, 3)


def apply_homography(point, homography):
    new_point = homography.dot(np.hstack((point, np.ones(1))))
    return new_point[:2] / new_point[-1]


def find_homography(source, destination):
    matching_points = np.array(list(it.combinations(range(source.shape[0]), 4)))
    min_error = float('inf')
    best_match = None
    homography = None
    for match in matching_points:
        curr_homography = get_homography(source[match], destination[match])
        transformed_src = np.apply_along_axis(lambda x: apply_homography(x, curr_homography), axis=1, arr=source)
        error = np.sum(np.sqrt(np.sum(np.square(destination - transformed_src), axis=1)))
        if min_error > error:
            min_error = error
            best_match = match
            homography = curr_homography

    return homography, best_match
