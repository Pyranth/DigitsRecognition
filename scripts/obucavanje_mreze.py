import mnist_loader as mnist_loader
import mreza as mz

print("Ucitavanje podataka za obuku...")
podaci_za_obucavanje, podaci_za_validaciju, podaci_za_testiranje = mnist_loader.load_data_wrapper()

print("Inicijalizacija neuronske mreze...")
mreza = mz.NeuronskaMreza([784, 30, 10])
print("Obucavanje neuronske mreze...")
mreza.SGD(podaci_za_obucavanje, 30, 10, 3.0)
print("Testiranje neuronske mreze...")
print(mreza.izvrsi_testiranje(list(podaci_za_testiranje)))
print("Sacuvavanje modela...")
mreza.zapamti_model()
print("Obucavanje neuronske mreze je uspjesno zavrseno!")
