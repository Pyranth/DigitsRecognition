import mnist_loader as mnist_loader
import mreza as nw


training_data, validation_data, test_data = mnist_loader.load_data_wrapper()


def get_network(input, hidden, output):
    network = nw.NeuronskaMreza([input, hidden, output])
    return network


def train_network(network):
    network.SGD(training_data, 30, 10, 3.0, test_data) # Change epochs 5 to 30 for live
    return network


def get_validation_data():
    vd = list(validation_data)
    return vd
