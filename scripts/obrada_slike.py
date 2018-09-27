import obucavanje as tn
import numpy as np
from PIL import Image

class Evaluator:

    def __init__(self, input, hidden, output):
        self.network = tn.get_network(input, hidden, output)

    def train(self):
        self.network = tn.train_network(self.network)
        self.network.zapamti_model()

    def load(self):
        self.network.ucitaj_model()

    def evaluate(self, image):
        data = self.image_to_array(self.resize(image))
        return self.recognize(data)

    def image_to_array(self, image):
        image.show()
        array = np.array(image)
        array = array.flatten()
        array = np.reshape(array, (784, 1))
        array = 1 - array / 255
        return array

    def resize(self, image):
        image = image.convert('L')
        return image.resize((28, 28), Image.ANTIALIAS)

    def recognize(self, data):
        return np.argmax(self.network.feedforward(data))
