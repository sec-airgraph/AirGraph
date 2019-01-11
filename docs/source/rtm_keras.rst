.. AirGraph documentation master file, created by
   sphinx-quickstart on Wed Aug  1 22:17:25 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

ロボット開発とDNN開発の連携について
=======================================
ロボット開発からDNN開発への連携機能
----------------------------------------
1. AirGraph(RTM Editor)側でコンポーネントのプロパティ設定にてDatasetを定義すると，サーバ上にDataset領域が作成されます．

 - コンポーネントのコンフィギュレーションにも自動反映され，学習のためのデータセット保存先の指定が簡単になります．

.. image:: ../img/rtm_add_dataset.png
  :width: 70%
  :align: center

- C++の場合

.. code-block:: csharp

  // <rtc-template block="config_declare">
  /*!
   *
   * - Name:  dataset_dir_path
   * - DefaultValue: /opt/AirGraph/data/datasets/SampleDataset/
   */
  std::string m_dataset_dir_path;
  // </rtc-template>

.. code-block:: csharp

  RTC::ReturnCode_t CppTest::onInitialize()
  {
    // Bind variables and configuration variable
    bindParameter("dataset_dir_path", m_dataset_dir_path, "/opt/AirGraph/data/datasets/SampleDataset/");
  
    return RTC::RTC_OK;
  }

- Pythonの場合

.. code-block:: python

	def __init__(self, manager):
		OpenRTM_aist.DataFlowComponentBase.__init__(self, manager)
		# initialize of configuration-data.
		# <rtc-template block="init_conf_param">
		"""
		 - Name:  dataset_dir_path
		 - DefaultValue: /opt/AirGraph/data/datasets/SampleDataset/
		"""
		self._dataset_dir_path = ['/opt/AirGraph/data/datasets/SampleDataset/']
		# </rtc-template>

2. AirGraph(Keras Editor)のプロパティ設定では，AirGraph(RTM Editor)で定義したDatasetを選択することが可能です．

 - この機能により，RTシステム側で取得したデータセットをそのままKeras側で学習に用いることが可能となります．

.. image:: ../img/keras_select_dataset.png
  :width: 50%
  :align: center

DNN開発からロボット開発への連携機能
----------------------------------------
1. AirGraph(Keras Editor)側でモデルを作成して保存すると，AirGraph(RTM Editor)側のコンポーネントのプロパティ画面でモデルの選択が可能となります．

 - モデルを選択すると，コンポーネントのコンフィギュレーションにも自動反映され，推論のためのモデルの指定が簡単になります．

.. image:: ../img/rtm_select_dnn.png
  :width: 70%
  :align: center

- C++の場合

.. code-block:: csharp

  // <rtc-template block="config_declare">
  /*!
   *
   * - Name:  dnn_model_path
   * - DefaultValue: /opt/AirGraph/data/keras_models/MnistSample/MnistSample.json
   */
  std::string m_dnn_model_path;
  // </rtc-template>

.. code-block:: csharp

  RTC::ReturnCode_t CppTest::onInitialize()
  {
    // Bind variables and configuration variable
    bindParameter("dnn_model_path", m_dnn_model_path, "/opt/AirGraph/data/keras_models/MnistSample/MnistSample.json");
  
    return RTC::RTC_OK;
  }

- Pythonの場合

.. code-block:: python

	def __init__(self, manager):
		OpenRTM_aist.DataFlowComponentBase.__init__(self, manager)
		# initialize of configuration-data.
		# <rtc-template block="init_conf_param">
		"""
		 - Name:  dnn_model_path
		 - DefaultValue: /opt/AirGraph/data/keras_models/MnistSample/MnistSample.json
		"""
		self._dnn_model_path = ['/opt/AirGraph/data/keras_models/MnistSample/MnistSample.json']
		# </rtc-template>
