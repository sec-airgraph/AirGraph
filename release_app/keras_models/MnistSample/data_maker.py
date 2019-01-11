# -*- coding: utf-8 -*-
'''
Keras Learning script for Keras-IDE
'''
__author__  = 'Ryuichiro Kodama<kodama@sec.co.jp>'
__version__ = '0.1'
__date__    = '10 Aug 2018'

from logging import getLogger
import keras

class DataMaker(object):
    """
    学習用データ作成・学習実行クラス
    """
    def __init__(self, model_json_file_path, result_path, log_dir_path, dataset_dir_path, log_level=20):
        """
        @param model_json_file_path Keras-IDEで作成したモデルのJSONファイルへのパス
        @param log_dir_path         TensorBoard用のログ吐き出し先のディレクトリパス
        @param result_path          学習結果のhdf5ファイル保存先ファイルパス
        @param log_level            ログレベル設定
        """
        # self.logger = getLogger(__name__)
        self.logger = getLogger()
        self.logger.setLevel(log_level)
        self.model_json_file_path = model_json_file_path
        self.result_path = result_path
        self.log_dir_path = log_dir_path
        self.dataset_dir_path = dataset_dir_path

    def make_input_data(self):
        """
        入力データを整形・加工する
        @return 訓練データと検証データの入出力ペア(x_train, y_train), (x_test, y_test)
        """
        from keras.datasets import mnist
        from keras.utils.np_utils import to_categorical
        ###############################################
        # Load data for learning
        # Split the sample data to train data and validation data
        ###############################################
        self.logger.info('loading mnist data...')
        (x_train, y_train), (x_test, y_test) = mnist.load_data()
        self.logger.info('mnist data loaded')

        ################################################
        # Reshape input data
        ################################################
        self.logger.info('reshape data')
        x_train = x_train.reshape(60000, 784)
        x_test = x_test.reshape(10000, 784)
        x_train = x_train.astype('float32')
        x_test = x_test.astype('float32')
        x_train /= 255
        x_test /= 255

        ################################################
        # Reshape validation data
        ################################################
        num_classes = 10
        y_train = y_train.astype('int32')
        y_test = y_test.astype('int32')
        y_train = to_categorical(y_train, num_classes)
        y_test = to_categorical(y_test, num_classes)

        self.logger.info('all data reshaped')

        return (x_train, y_train), (x_test, y_test)

    def load_model_from_ide(self, json_file_path):
        """
        Keras-IDEで作成したモデルの読み込み
        """
        ################################################
        # Load model
        ################################################
        self.logger.info('load model from json file')
        self.logger.debug('json file:' + json_file_path)
        import json

        # 学習プロパティ類取得のため生JSONも取得
        json_file = open(json_file_path)
        model_json = json_file.read()
        model_json_object = json.loads(model_json)
        json_file.close()
        # モデル読み込み
        keras_model = keras.models.model_from_json(model_json)
        self.logger.info('model loaded')
        keras_model.summary()
        return keras_model, model_json_object

    def learn(self):
        """
        学習実行
        """
        (x_train, y_train), (x_test, y_test) = self.make_input_data()
        # IDEで作成したモデルのJSON読み込み
        keras_model, model_json_object = self.load_model_from_ide(self.model_json_file_path)
        ################################################
        # Set callbacks for TensorBoard
        ################################################
        callbacks = []
        # for callback in model_json_object['fit.callback']:
        # 	callbacks.append(keras.callbacks[callback])
        # callbacks.append(keras.callbacks.EarlyStopping(monitor='val_loss', patience=0, verbose=0, mode='auto'))
        callbacks.append(keras.callbacks.TensorBoard(log_dir=self.log_dir_path, histogram_freq=1))

        ################################################
        # Set learning properties
        ################################################
        optimizer = model_json_object['optimizer']
        loss = model_json_object['loss']
        metrics = model_json_object['metrics']

        keras_model.compile(loss=loss, optimizer=optimizer, metrics=metrics)

        ################################################
        # Execute leaning
        ################################################
        # バッチサイズ、エポック数
        batch_size = model_json_object['fit.batch_size']
        epochs = model_json_object['fit.epochs']

        _ = keras_model.fit(x_train, y_train,
                            batch_size=batch_size,
                            epochs=epochs,
                            verbose=1,
                            validation_data=(x_test, y_test),
                            callbacks=callbacks)

        # 学習結果をhdf5に保存
        keras_model.save(self.result_path)

if __name__ == '__main__':
    # コマンドライン引数を取得
    import sys
    print('argc: {}'.format(len(sys.argv)))
    print('argv: {}'.format(sys.argv))
    # コマンドライン引数から、モデルファイル・結果ファイル・ログファイルの出力先・データセットディレクトリを取得
    if len(sys.argv) < 5:
        print('too few arguments. this program needs 4 arguments:')
        print('    model_json_file_path, result_path, log_dir_path, dataset_dir_path.')
        sys.exit(-1)
    MODEL_JSON_FILE_PATH, RESULT_PATH, LOG_DIR_PATH, DATASET_DIR_PATH = sys.argv[1:]
    print('model_json_file_path: {}'.format(MODEL_JSON_FILE_PATH))
    print('result_path         : {}'.format(RESULT_PATH))
    print('log_dir_path        : {}'.format(LOG_DIR_PATH))
    print('dataset_dir_path    : {}'.format(DATASET_DIR_PATH))
    data_maker = DataMaker(MODEL_JSON_FILE_PATH, RESULT_PATH, LOG_DIR_PATH, DATASET_DIR_PATH)
    data_maker.learn()
    sys.exit(0)
