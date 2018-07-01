import os

import cv2 as cv
import numpy as np

from helper import ncc, normalize, sad, show_result, ssd


def main(image, template, methods):
    image_normalized = normalize(image)
    template_normalized = normalize(template)
    image_h, image_w = image.shape
    template_h, template_w = template.shape
    match_h, match_w = image_h - template_h + 1, image_w - template_w + 1
    match = np.zeros((match_h, match_w), dtype='float64')
    result = {method_name: match.copy() for method_name in methods}

    for i in range(match_h):
        for j in range(match_w):
            window = image[i:template_h + i, j:template_w + j]
            window_normalized = image_normalized[i:template_h + i, j:template_w + j]
            for method_name, values in methods.items():
                func = values['method']
                normalized = values['normalized']
                if normalized:
                    result[method_name][i, j] = func(template_normalized, window_normalized)
                else:
                    result[method_name][i, j] = func(template, window)

    for method_name, match in result.items():
        if methods[method_name]['argmax']:
            top_left = np.unravel_index(match.argmax(), match.shape)[::-1]
        else:
            top_left = np.unravel_index(match.argmin(), match.shape)[::-1]
        show_result(image, template_w, template_h, match, top_left, method_name)


if __name__ == '__main__':
    mthds = {
        'SAD': {
            'method': sad,
            'normalized': False,
            'argmax': False
        },
        'SSD': {
            'method': ssd,
            'normalized': False,
            'argmax': False
        },
        'NCC': {
            'method': ncc,
            'normalized': True,
            'argmax': True
        }
    }

    data_dir = 'data/Biker/img'
    img_name = '0001.jpg'
    template_name = 'template.png'
    img = cv.imread(os.path.join(data_dir, img_name), 0)
    tmplt = cv.imread(os.path.join(data_dir, template_name), 0)

    main(img, tmplt, mthds)
