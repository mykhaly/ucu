import os

import cv2 as cv
import numpy as np
from matplotlib import pyplot as plt

from helper import apply_homography, find_homography


photo = cv.imread(os.path.join('data', 'pokemon_05.jpg'))
updated_photo = photo.copy()

poster = cv.imread(os.path.join('data', 'pokemon_00.jpg'))

poster_corrds = np.array([
    [0, 0],         # top left
    [0, 1800],      # top right
    [1674, 1800],   # bottom right
    [1674, 0],      # bottom left
    [463, 976],     # pikachu
    [1023, 417],    # lugia
    [1152, 1480],   # cynda,
    [625, 1754],    # pokemon
    [373, 799],     # slowk
    [1590, 635]     # eleki
])

photo_coords = np.array([
    [379, 972],  # top left
    [814, 733],  # top right
    [584, 154],  # bottom right
    [260, 503],  # bottom left
    [530, 702],  # pikachu
    [370, 612],  # lugia
    [570, 385],  # cynda,
    [707, 505],  # pokemon
    [501, 760],  # slowk
    [365, 420]   # eleki
])

homography, best_match = find_homography(photo_coords, poster_corrds)

contour = np.array([(435, 700),
                    (590, 670),
                    (568, 428),
                    (420, 480)],
                   dtype=np.int)

width, height, _ = updated_photo.shape

for i in range(width):
    for j in range(height):
        if cv.pointPolygonTest(contour, (i, j), False) != -1:
            x, y = apply_homography(np.array([i, j]).T, homography)
            updated_photo[i, j] = poster[int(x), int(y)]

cv.imwrite('output.jpg', updated_photo)


for index in range(len(poster_corrds)):
    if index in best_match:
        color = (255, 0, 0)
        cv.circle(poster, tuple(poster_corrds[index][::-1]), 30, color, -1)
        cv.circle(updated_photo, tuple(photo_coords[index][::-1]), 20, color, -1)

    else:
        color = (0, 0, 255)
        cv.rectangle(poster,
                     (poster_corrds[index][1] - 20, poster_corrds[index][0] - 20),
                     (poster_corrds[index][1] + 20, poster_corrds[index][0] + 20),
                     color,
                     -1)
        cv.rectangle(updated_photo,
                     (photo_coords[index][1] - 12, photo_coords[index][0] - 12),
                     (photo_coords[index][1] + 12, photo_coords[index][0] + 12),
                     color,
                     -1)

plt.figure(num=None, figsize=(12, 9), dpi=80, facecolor='w', edgecolor='k')

plt.subplot(131)
plt.title('Original photo')
plt.imshow(cv.cvtColor(photo, cv.COLOR_BGR2RGB))
plt.xticks([])
plt.yticks([])

plt.subplot(132)
plt.title('Updated photo')
plt.imshow(cv.cvtColor(updated_photo, cv.COLOR_BGR2RGB))
plt.xticks([])
plt.yticks([])

plt.subplot(133)
plt.title('Poster')
plt.imshow(cv.cvtColor(poster, cv.COLOR_BGR2RGB))
plt.xticks([])
plt.yticks([])
plt.show()
