import random
import numpy as np
import os

class NeuronskaMreza(object):

    def __init__(self, slojevi):
        self.broj_slojeva = len(slojevi)
        self.slojevi = slojevi
        self.sklonosti = [np.random.randn(y, 1) for y in slojevi[1:]]
        self.tezine = [np.random.randn(y, x)
                       for x, y in zip(slojevi[:-1], slojevi[1:])]

    def ucitaj_model(self):
        dir_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))

        if not os.path.exists(dir_path + '/model'):
            print("Model ne postoji!")

        self.slojevi = np.load(dir_path + '/model/sizes.npy')
        self.broj_slojeva = len(self.slojevi)
        self.sklonosti = np.load(dir_path + '/model/biases.npy')
        self.tezine = np.load(dir_path + '/model/weights.npy')

    def zapamti_model(self):
        dir_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))

        if not os.path.exists(dir_path + '/model'):
            os.mkdir(dir_path + '/model')

        np.save(dir_path + '/model/sizes', self.slojevi)
        np.save(dir_path + '/model/biases', self.sklonosti)
        np.save(dir_path + '/model/weights', self.tezine)

    def feedforward(self, a):
        for s, t in zip(self.sklonosti, self.tezine):
            a = sigmoid(np.dot(t, a)+s)
        return a

    def SGD(self, podaci_za_obucavanje, broj_epoha, mini_batch_velicina, eta,
            podaci_za_testiranje=None):

        podaci_za_obucavanje = list(podaci_za_obucavanje)
        n = len(podaci_za_obucavanje)

        if podaci_za_testiranje:
            podaci_za_testiranje = list(podaci_za_testiranje)
            n_test = len(podaci_za_testiranje)

        for j in range(broj_epoha):
            random.shuffle(podaci_za_obucavanje)
            mini_batches = [
                podaci_za_obucavanje[k:k + mini_batch_velicina]
                for k in range(0, n, mini_batch_velicina)]
            for mini_batch in mini_batches:
                self.azuriraj_mini_batch(mini_batch, eta)
            if podaci_za_testiranje:
                print("Epoch {} : {} / {}".format(j, self.izvrsi_testiranje(podaci_za_testiranje), n_test));
            else:
                print("Epoch {} complete".format(j))

    def azuriraj_mini_batch(self, mini_batch, eta):
        nabla_b = [np.zeros(b.shape) for b in self.sklonosti]
        nabla_w = [np.zeros(w.shape) for w in self.tezine]
        for x, y in mini_batch:
            delta_nabla_b, delta_nabla_w = self.backprop(x, y)
            nabla_b = [nb+dnb for nb, dnb in zip(nabla_b, delta_nabla_b)]
            nabla_w = [nw+dnw for nw, dnw in zip(nabla_w, delta_nabla_w)]
        self.tezine = [w - (eta / len(mini_batch)) * nw
                       for w, nw in zip(self.tezine, nabla_w)]
        self.sklonosti = [b - (eta / len(mini_batch)) * nb
                          for b, nb in zip(self.sklonosti, nabla_b)]

    def backprop(self, x, y):
        nabla_b = [np.zeros(b.shape) for b in self.sklonosti]
        nabla_w = [np.zeros(w.shape) for w in self.tezine]

        aktivacija = x
        vektor_aktivacija = [x]
        z_vektor = []
        for b, w in zip(self.sklonosti, self.tezine):
            z = np.dot(w, aktivacija)+b
            z_vektor.append(z)
            aktivacija = sigmoid(z)
            vektor_aktivacija.append(aktivacija)

        delta = self.vektor_parcijalnih_izvoda_funkcije_troska(vektor_aktivacija[-1], y) * \
                sigmoid_izvod(z_vektor[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, vektor_aktivacija[-2].transpose())

        for l in range(2, self.broj_slojeva):
            z = z_vektor[-l]
            sp = sigmoid_izvod(z)
            delta = np.dot(self.tezine[-l + 1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, vektor_aktivacija[-l-1].transpose())
        return (nabla_b, nabla_w)

    def izvrsi_testiranje(self, podaci_za_testiranje):
        """Return the number of test inputs for which the neural
        network outputs the correct result. Note that the neural
        network's output is assumed to be the index of whichever
        neuron in the final layer has the highest activation."""
        rezultati_testiranja = [(np.argmax(self.feedforward(x)), y)
                        for (x, y) in podaci_za_testiranje]
        return sum(int(x == y) for (x, y) in rezultati_testiranja)

    def vektor_parcijalnih_izvoda_funkcije_troska(self, output_activations, y):
        """Return the vector of partial derivatives \partial C_x /
        \partial a for the output activations."""
        return (output_activations-y)

def sigmoid(z):
    return 1.0/(1.0+np.exp(-z))

def sigmoid_izvod(z):
    return sigmoid(z)*(1-sigmoid(z))
