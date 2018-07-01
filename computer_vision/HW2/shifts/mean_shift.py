import os

import cv2 as cv
import numpy as np


def cut_roi(image, roi):
    return image[
           roi[1]:roi[1] + roi[3],
           roi[0]:roi[0] + roi[2]]


def get_hist(image):
    low = np.array((0., 60., 32.))
    high = np.array((180., 255., 255.))
    matching_pixels = set()
    for row in image:
        for pixel in row:
            bigger_than_low = pixel > low
            less_than_high = pixel < high
            if all(bigger_than_low) and all(less_than_high):
                matching_pixels.add(pixel[0])
    return matching_pixels

def get_window(target_point, window_size):
    return (int(np.ceil(target_point[0])) - window_size[0] // 2,
            int(np.ceil(target_point[1])) - window_size[1] // 2,
            window_size[0],
            window_size[1])


def mean_shift(src, window):
    num_of_iterations = 10
    min_distance = 2
    roi = cut_roi(src, window)
    centroid = np.zeros(2)
    for iter_cnt in range(num_of_iterations):
        new_centroid = np.array(
            [np.mean(np.argwhere(roi > 0)[:, 1]),
             np.mean(np.argwhere(roi > 0)[:, 0])])
        if np.linalg.norm(centroid - new_centroid) < min_distance:
            break
        else:
            centroid = new_centroid.copy()
            roi = cut_roi(src, get_window(centroid + window[:2], window[2:4]))
    return get_window(centroid + window[:2], window[2:4])


def calculate_back_projection(image, set_of_colors):
    set_of_colors = set(pixel[0] for pixel in set_of_colors)
    result = np.zeros(image.shape[:-1])

    h, w, _ = image.shape
    for i in range(h):
        for j in range(w):
            if image[i, j, 0] in set_of_colors:
                result[i, j] = 1
    return result
    # return np.array([1 if pixel[0] in set_of_colors else 0 for row in image for pixel in row], dtype='int8').reshape(image.shape[:-1])
    # return


image_folder = '../data/Coke/img/'
images = [cv.imread(os.path.join(image_folder, img_name))
          for img_name in sorted(os.listdir(image_folder))]

target_point = np.array([320, 200])
prev_img = images.pop()
window_size = [100, 100]
track_window = get_window(target_point, window_size)
track_window2 = get_window(target_point, window_size)


roi = cut_roi(prev_img, track_window)
hsv_roi = cv.cvtColor(roi, cv.COLOR_BGR2HSV)

mask = cv.inRange(hsv_roi, np.array((0., 60., 32.)), np.array((180., 255., 255.)))

roi_hist = cv.calcHist([hsv_roi], [0], mask, [180], [0, 180])
cv.normalize(roi_hist, roi_hist, 0, 255, cv.NORM_MINMAX)
term_crit = (cv.TERM_CRITERIA_EPS | cv.TERM_CRITERIA_COUNT, 10, 1)

for img_idx, frame in enumerate(images):
    print("Image: {}".format(img_idx))

    hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)
    dst = cv.calcBackProject([hsv], [0], roi_hist, [0, 180], 1)
    _, track_window = cv.meanShift(dst, track_window, term_crit)
    x, y, w, h = track_window
    frame = cv.rectangle(frame, (x, y), (x + w, y + h), 255, 2)

    track_window2 = mean_shift(dst, track_window2)
    x2, y2, w2, h2 = track_window2
    frame = cv.rectangle(frame, (x2, y2), (x2 + w2, y2 + h2), (0, 0, 255), 2)

    cv.imshow('', frame)
    k = cv.waitKey(1) & 0xff
    if k == 27:
        break
    elif k == 32:
        cv.waitKey(3000)

cv.destroyAllWindows()
