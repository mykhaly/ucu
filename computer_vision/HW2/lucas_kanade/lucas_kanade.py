import argparse
import os

import cv2
import numpy as np

from helper import affine2d, cut_roi, get_roi, jacobian, plot_image, write_image

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--roi', nargs='+', type=int, required=True)
    parser.add_argument('--dataset', type=str, default='../data/Coke/img/')
    args = parser.parse_args()

    image_folder = '../data/Coke/img/'
    images = [cv2.imread(os.path.join(image_folder, img_name), 0)
              for img_name in sorted(os.listdir(args.dataset or image_folder))]

    target_point = np.array([320, 200])
    window_size = (120, 120)
    region_of_interest = args.roi or get_roi(target_point, window_size)

    target = cut_roi(images.pop(0), region_of_interest)

    num_iterations = 50
    for idx, next_image in enumerate(images):
        print("Processing image: {}".format(idx + 1))
        img_copy = next_image.copy()
        params = np.zeros(6, dtype="float32")

        for i in range(num_iterations):
            candidate = cut_roi(affine2d(next_image, params), region_of_interest)

            gx = cv2.Sobel(next_image, cv2.CV_32F, 1, 0, ksize=3)
            gy = cv2.Sobel(next_image, cv2.CV_32F, 0, 1, ksize=3)

            gx_w = affine2d(gx, params)
            gy_w = affine2d(gy, params)

            gx_w = cut_roi(gx_w, region_of_interest)
            gy_w = cut_roi(gy_w, region_of_interest)

            X, Y = np.meshgrid(range(candidate.shape[0]),
                               range(candidate.shape[1]))

            coords2d = np.array([X.flatten(),
                                 Y.flatten()]).transpose()

            grad_image = np.array([gx_w.flatten(),
                                   gy_w.flatten()]).transpose()

            jacob = jacobian(coords2d)

            steepest_descent = np.empty(shape=(grad_image.shape[0],
                                               jacob.shape[2]),
                                        dtype='float64')

            for j in range(grad_image.shape[0]):
                steepest_descent[j] = np.dot(grad_image[j],
                                             jacob[j])

            hessian = np.dot(steepest_descent.transpose(),
                             steepest_descent)
            error_image = np.subtract(target, candidate, dtype='float64')

            cost = np.sum(steepest_descent * np.tile(error_image.flatten(),
                                                     (len(params), 1)).T, axis=0)
            dp = np.dot(np.linalg.inv(hessian), cost.T)
            eps = 10 ** (-2)
            norm = np.linalg.norm(dp)

            if norm < eps:
                break
            else:
                params += dp.T

        affine_transform = np.array([[1 + params[0], params[2], params[4]],
                                     [params[1], 1 + params[3], params[5]]])

        target_point = affine_transform.dot(np.append(target_point, 1))
        new_region_of_interest = get_roi(target_point, window_size)

        target = cut_roi(next_image, new_region_of_interest)
        region_of_interest = new_region_of_interest
        plot_image(img_copy, window_size, region_of_interest, idx + 1)
        # write_image(img_copy, window_size, region_of_interest, idx + 1)
