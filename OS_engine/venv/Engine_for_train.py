import tensorflow as tf
tf.compat.v1.enable_eager_execution()
import timeit
import numpy as np

# config = {
# 'batch': 50,
# 'train_epch': 10,
# 'param_conv': [3, 3, 3],
# 'conv_active': 'relu',
# 'pooling': 'Max',
# 'param_dense': [(10, 'relu'), (10, 'softmax')],
# 'loss': tf.keras.losses.mean_squared_error
# }


class cg():
    def __init__(self, param):
        self.config = param
        (self.x_train, self.y_train), (self.x_test, self.y_test) = tf.keras.datasets.mnist.load_data()

        # self.x_train=self.x_train[:50]
        # self.y_train=self.y_train[:50]
        # self.x_test=self.x_test[:10]
        # self.y_test=self.y_test[:10]
        self.Model = tf.keras.Sequential()
        self.Model.add(tf.keras.layers.Input(shape=(28, 28, 1), batch_size=self.config['batch']))
        # conv
        for param in self.config['param_conv']:
            self.Model.add(tf.keras.layers.Conv2D(10, param, activation=self.config['conv_active']))
        if self.config['pooling'] == 'Max':
            self.Model.add(tf.keras.layers.MaxPooling2D())
        else:
            self.Model.add(tf.keras.layers.AveragePooling2D())

        self.Model.add(tf.keras.layers.Flatten())

        # dense
        for (node, activation) in self.config['param_dense']:
            if activation == 'relu':
                self.Model.add(tf.keras.layers.Dense(node, tf.keras.activations.relu))
            elif activation == 'softmax':
                self.Model.add(tf.keras.layers.Dense(node, tf.keras.activations.softmax))

        self.Model.compile(optimizer='adam', loss=tf.keras.losses.sparse_categorical_crossentropy)
        self.Model.build()
        self.Model.summary()

    def train(self):
        x_train, y_train = self.x_train, self.y_train
        x_train = x_train / 255.0

        x_train = x_train[..., tf.newaxis]

        # calculate train time
        train_start_time = timeit.default_timer()
        # Model train
        self.Model.fit(x_train, y_train, epochs=self.config['train_epch'], batch_size=self.config['batch'])
        # train terminate
        train_end_time = timeit.default_timer()

        # calculate
        train_pred = self.Model.predict(x_train)
        train_pred = tf.math.argmax(train_pred, 1)
        train_acc = tf.reduce_mean(tf.cast(tf.equal(train_pred, y_train), tf.float32)) * 100
        train_acc = train_acc.numpy()

        # Sec
        train_time = train_end_time - train_start_time

        return train_acc, train_time

    def pred(self):
        x_test = self.x_test
        x_test = x_test / 255.0

        x_test = x_test[..., tf.newaxis]

        pred = self.Model.predict(x_test)
        pred = tf.math.argmax(pred, 1)

        acc = tf.reduce_mean(tf.cast(tf.equal(pred, self.y_test), tf.float32)) * 100
        acc = acc.numpy()

        return acc

