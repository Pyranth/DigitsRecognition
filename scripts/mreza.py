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
        for b, w in zip(self.sklonosti, self.tezine):
            a = sigmoid(np.dot(w, a)+b)
        return a

    def SGD(self, training_data, epochs, mini_batch_size, eta,
            test_data=None):

        training_data = list(training_data)
        n = len(training_data)

        if test_data:
            test_data = list(test_data)
            n_test = len(test_data)

        for j in range(epochs):
            random.shuffle(training_data)
            mini_batches = [
                training_data[k:k+mini_batch_size]
                for k in range(0, n, mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)
            if test_data:
                print("Epoch {} : {} / {}".format(j,self.evaluate(test_data),n_test));
            else:
                print("Epoch {} complete".format(j))

    def update_mini_batch(self, mini_batch, eta):
        """Update the network's weights and biases by applying
        gradient descent using backpropagation to a single mini batch.
        The ``mini_batch`` is a list of tuples ``(x, y)``, and ``eta``
        is the learning rate."""
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
        """Return a tuple ``(nabla_b, nabla_w)`` representing the
        gradient for the cost function C_x.  ``nabla_b`` and
        ``nabla_w`` are layer-by-layer lists of numpy arrays, similar
        to ``self.biases`` and ``self.weights``."""
        nabla_b = [np.zeros(b.shape) for b in self.sklonosti]
        nabla_w = [np.zeros(w.shape) for w in self.tezine]
        # feedforward
        activation = x
        activations = [x] # list to store all the activations, layer by layer
        zs = [] # list to store all the z vectors, layer by layer
        for b, w in zip(self.sklonosti, self.tezine):
            z = np.dot(w, activation)+b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)
        # backward pass
        delta = self.cost_derivative(activations[-1], y) * \
            sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].transpose())
        # Note that the variable l in the loop below is used a little
        # differently to the notation in Chapter 2 of the book.  Here,
        # l = 1 means the last layer of neurons, l = 2 is the
        # second-last layer, and so on.  It's a renumbering of the
        # scheme in the book, used here to take advantage of the fact
        # that Python can use negative indices in lists.
        for l in range(2, self.broj_slojeva):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.tezine[-l + 1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l-1].transpose())
        return (nabla_b, nabla_w)

    def evaluate(self, test_data):
        """Return the number of test inputs for which the neural
        network outputs the correct result. Note that the neural
        network's output is assumed to be the index of whichever
        neuron in the final layer has the highest activation."""
        test_results = [(np.argmax(self.feedforward(x)), y)
                        for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)

    def cost_derivative(self, output_activations, y):
        """Return the vector of partial derivatives \partial C_x /
        \partial a for the output activations."""
        return (output_activations-y)

#### Miscellaneous functions
def sigmoid(z):
    """The sigmoid function."""
    return 1.0/(1.0+np.exp(-z))

def sigmoid_prime(z):
    """Derivative of the sigmoid function."""
    return sigmoid(z)*(1-sigmoid(z))
