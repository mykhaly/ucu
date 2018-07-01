import os

import cv2
import numpy as np
from matplotlib import pyplot as plt


def get_roi(target, window_size):
    return np.array([
        int(np.ceil(target[0])) - window_size[0] // 2,
        int(np.ceil(target[1])) - window_size[1] // 2,
        window_size[0],
        window_size[1]
    ])


def cut_roi(image, roi):
    return image[roi[1] - 1:roi[1] + roi[3] - 1,
           roi[0] - 1:roi[0] + roi[2] - 1]


def affine2d(image, p):
    return cv2.warpAffine(image,
                          np.array([[1 + p[0], p[2], p[4]],
                                    [p[1], 1 + p[3],  p[5]]]),
                          image.shape,
                          flags=cv2.INTER_LINEAR + cv2.WARP_INVERSE_MAP)


def jacobian(array):
    def j(p):
        x, y = p
        return np.array([[x, 0, y, 0, 1, 0],
                         [0, x, 0, y, 0, 1]])

    return np.apply_along_axis(j, 1, array)


def process_image():
    pass


def plot_image(image, window_size, roi, image_idx):
    top_left = (roi[0],
                roi[1])
    bottom_right = (top_left[0] + window_size[0],
                    top_left[1] + window_size[1])
    cv2.rectangle(image, top_left, bottom_right, 255, 2)
    plt.imshow(image, cmap='gray')
    plt.title(image_idx)
    plt.show()


def write_image(image, window_size, roi, image_idx, data_dir='../data/Coke/img/'):
    filepath = os.path.join(data_dir.replace('img', 'lk_output'),
                            "{}.jpg".format(str(image_idx).zfill(4)))
    top_left = (roi[0],
                roi[1])
    bottom_right = (top_left[0] + window_size[0],
                    top_left[1] + window_size[1])
    cv2.rectangle(image, top_left, bottom_right, 255, 2)
    cv2.imwrite(filepath, image)
