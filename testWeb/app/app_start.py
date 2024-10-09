# from flask import Flask, request, redirect, url_for, send_from_directory, render_template
#
# app = Flask(__name__)
# app.debug = True
#
# # Routes
# @app.route('/', methods=['GET'])
# def root():
#     return app.send_static_file('index.html')
#
# @app.route('/')
# def static_prox(path):
#     return app.send_static_file(path)
#
# @app.route('/test', methods=['GET'])
# def test():
#     data = {
#     'batch': 50,
#     'train_epch': 10,
#     'param_conv': [3, 3, 3],
#     'conv_active': 'relu',
#     'pooling': 'Max',
#     'param_dense': [(10, 'relu'), (10, 'softmax')],
#     'loss': 'shoot'
#     }
#     print(type(data['conv_active']))
#     return render_template('index.html', obj = data['conv_active'])
#
#
# if __name__ == "__main__":
#     app.run(host='localhost', port=3000, threaded=True)

from app import app

app.run(host="0.0.0.0", port=80)
