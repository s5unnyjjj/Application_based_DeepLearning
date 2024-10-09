# file name : index.py
# pwd : /project_name/app/main/index.py

from flask import Blueprint, request, render_template, flash, redirect, url_for, jsonify
from flask import current_app as app
from OS_engine.venv import Engine_for_train
import numpy
# 추가할 모듈이 있다면 추가

main = Blueprint('main', __name__, url_prefix='/')

testData = {
    'batch': 0,
    'train_epch': 0,
    'param_conv': [],
    'conv_active': 'relu',
    'pooling': 'Max',
    'param_dense': [],
    'loss': "tf.keras.losses.mean_squared_error"
}

@main.route('/', methods=['GET'])
def index():
    # /main/index.html은 사실 /project_name/app/templates/main/index.html을 가리킵니다.


    return render_template('/main/index.html')

@main.route('/post', methods=['POST'])
def post():
    # testData['conv_active'] = request.form['conv_active']
    # print(testData)


    return render_template('/main/index.html')

@main.route('/test', methods=['POST'])
def test():

    req_data = request.get_json(silent=True, force=True)
    print(testData)
    print(req_data)
    testData['batch'] = int(req_data['batch'])
    testData['train_epch'] = int(req_data['train_epch'])
    testData['param_conv'] = list(map(int, req_data['param_conv'].split(',')))
    testData['conv_active'] = req_data['conv_active']
    testData['pooling'] = req_data['pooling']
    for node, act in zip(req_data['param_dense'].split(","), req_data['param_dense_act'].split(",")):
        testData['param_dense'].append((node, act))

    instance = Engine_for_train.cg(testData)

    print(testData)
    acc, train_time  = instance.train()
    test_acc = instance.pred()
    print('학습률 :', acc, '학습 소요 시간 :', train_time)
    print('예측률 :', test_acc )
    print(str(acc)+","+str(train_time)+","+str(test_acc))

    return "학습률 : "+str(round(acc,2))+", 소요시간 : "+str(round(train_time,2))+"초, 예측률 : "+str(round(test_acc,2))
