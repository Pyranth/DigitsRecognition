import mreza as mz

import numpy as np
import math
from scipy import ndimage
import cv2
import os
from PIL import Image
from os import listdir
from os.path import isfile, join

def getBestShift(img):
    cy,cx = ndimage.measurements.center_of_mass(img)

    rows,cols = img.shape
    shiftx = np.round(cols/2.0-cx).astype(int)
    shifty = np.round(rows/2.0-cy).astype(int)

    return shiftx,shifty

def shift(img,sx,sy):
    rows,cols = img.shape
    M = np.float32([[1,0,sx],[0,1,sy]])
    shifted = cv2.warpAffine(img,M,(cols,rows))
    return shifted

print("Ucitavanje slika...")
dir_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
slike = [f for f in listdir(dir_path + '/slike') if isfile(join(dir_path + '/slike', f))]
slike.sort()

print("Inicijalizacija neuronske mreze...")
mreza = mz.NeuronskaMreza([])
print("Ucitavanje modela...")
mreza.ucitaj_model()

print("Prepoznavanje slika...")
for c in slike:

    niz_piksela = cv2.imread(dir_path + '/slike/' + c, 0)
    niz_piksela = cv2.resize(255 - niz_piksela, (28, 28))

    # Uklanjanje mrlja
    (thresh, niz_piksela) = cv2.threshold(niz_piksela, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)

    # Uklanjane praznih redova i kolona
    while np.sum(niz_piksela[0]) == 0:
        niz_piksela = niz_piksela[1:]

    while np.sum(niz_piksela[:, 0]) == 0:
        niz_piksela = np.delete(niz_piksela, 0, 1)

    while np.sum(niz_piksela[-1]) == 0:
        niz_piksela = niz_piksela[:-1]

    while np.sum(niz_piksela[:, -1]) == 0:
        niz_piksela = np.delete(niz_piksela, -1, 1)

    rows, cols = niz_piksela.shape

    # Resize na 20x20
    if rows > cols:
        factor = 20.0 / rows
        rows = 20
        cols = int(round(cols * factor))
        niz_piksela = cv2.resize(niz_piksela, (rows, cols))
    else:
        factor = 20.0 / cols
        cols = 20
        rows = int(round(rows * factor))
        niz_piksela = cv2.resize(niz_piksela, (rows, cols))

    # Prosirivanje na 28x28
    colsPadding = (int(math.ceil((28 - cols) / 2.0)), int(math.floor((28 - cols) / 2.0)))
    rowsPadding = (int(math.ceil((28 - rows) / 2.0)), int(math.floor((28 - rows) / 2.0)))
    niz_piksela = np.lib.pad(niz_piksela, (colsPadding, rowsPadding), 'constant')

    # Centriranje
    shiftx, shifty = getBestShift(niz_piksela)
    shifted = shift(niz_piksela, shiftx, shifty)
    niz_piksela = shifted

    niz_piksela = np.reshape(niz_piksela, (784, 1))
    niz_piksela = niz_piksela / 255

    print(np.argmax(mreza.feedforward(niz_piksela)))