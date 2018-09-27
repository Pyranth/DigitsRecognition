import mreza as mz

import numpy as np
import os
from PIL import Image
from os import listdir
from os.path import isfile, join

print("Ucitavanje slika...")
dir_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
slike = [f for f in listdir(dir_path + '/slike') if isfile(join(dir_path + '/slike', f))]
slike.sort()

print("Inicijalizacija neuronske mreze...")
mreza = mz.NeuronskaMreza([784, 30, 10])
print("Ucitavanje modela...")
mreza.ucitaj_model()

print("Prepoznavanje slika...")
for c in slike:
    slika = Image.open(dir_path + '/slike/' + c)
    slika = slika.convert('L')

    niz_piksela = np.array(slika)

    for i in range(0, len(niz_piksela)):
        for j in range(0, len(niz_piksela[i])):
            if niz_piksela[i][j] > 240:
                niz_piksela[i][j] = 255

    for i in range(0, len(niz_piksela)):
        for j in range(0, len(niz_piksela[i])):
            if niz_piksela[i][j] < 15:
                niz_piksela[i][j] = 0

    niz_piksela = np.reshape(niz_piksela, (784, 1))
    niz_piksela = 1 - niz_piksela / 255

    print(np.argmax(mreza.feedforward(niz_piksela)))


