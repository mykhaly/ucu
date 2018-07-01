import cv2 as cv
import numpy as np
from matplotlib import pyplot as plt


def ssd(a, b):
    return np.sum((a - b) ** 2)


def ncc(a, b):
    return np.sum(np.multiply(a, b))


def sad(a, b):
    return np.sum(np.absolute(a - b))


def normalize(img):
    return (img - np.mean(img)) / np.std(img)


def show_result(image, template_w, template_h, similarity_matrix, top_left_corner, method_name):
    img_copy = image.copy()
    bottom_right = (top_left_corner[0] + template_w, top_left_corner[1] + template_h)
    cv.rectangle(img_copy, top_left_corner, bottom_right, 255, 2)
    plt.subplot(121)
    plt.imshow(similarity_matrix, cmap='gray')
    plt.title('Matching Result')
    plt.xticks([])
    plt.yticks([])
    plt.subplot(122)
    plt.imshow(img_copy, cmap='gray')
    plt.title(method_name)
    plt.xticks([])
    plt.yticks([])
    plt.show()
