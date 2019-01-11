# AirGraph

AirGraphはAIとRobot開発のための、グラフィカルなIDEです。
詳細は [ドキュメント](https://sec-airgraph.github.io/airgraph) をお読みください.

<img src="https://github.com/sec-airgraph/airgraph/blob/master/docs_src/img/airgraph.png" width="320px">

*Read this in other languages: [English](README.en.md).*

## システム要件
* Ubuntu 16.04

> 開発環境は `Ubuntu 16.04 64bit on arm64 processor` です。

## 依存関係
* Java SE Development Kit 8
* Apache Maven
* [OpenRTM-aist](http://openrtm.org/) 1.1.2
* [wasanbon](http://wasanbon.org/) 1.1.0.post5
* [Keras](https://keras.io/) 2.2.4
    - [Tensorflow](https://www.tensorflow.org) r1.12
    - hdf5, h5pyは必須.

## インストール手順
### Step1. 依存パッケージのインストール
* パッケージ情報の最新化
    ```bash
    $ sudo apt update
    $ sudo apt upgrade -y
    $ sudo apt update
    ```
    > apt upgardeは任意

* Java SE Development Kit 8
    ```bash
    $ sudo apt install openjdk-8-jdk -y
    $ echo "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" >> ~/.bashrc
    $ echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    $ source ~/.bashrc
    ```
    > 上記パスはUbuntu16.04(64bit版、armプロセッサ)の場合の例のため，必要に応じて変更

* Apache Maven
    ```bash
    $ sudo apt install maven -y
    ```

* Git
    ```bash
    $ sudo apt install git -y
    ```

* sysv-rc-conf
    ```bash
    $ sudo apt install sysv-rc-conf -y
    ```

### Step2. 依存関係のインストール
公式サイトを参照してインスト―ル。 ([OpenRTM-aist](http://openrtm.org/), [wasanbon](http://wasanbon.org/), [Keras](https://keras.io/))


### Step3. AirGraphのビルド
* Clone、ビルド
    ```bash
    $ cd ~
    $ git clone --recursive https://github.com/sec-airgraph/airgraph.git
    $ cd airgraph/release_app/scripts
    $ ./build_airgraph.sh
    ```

> mavenのプロキシ設定は以下を参考
> ```bash
> $ vi ~/.m2/settings.xml
> ```
> ```xml
> <settings>
>   <proxies>
>     <proxy>
>       <active>true</active>
>       <protocol>プロトコル</protocol>
>       <host>ホスト名</host>
>       <port>ポート番号</port>
>       <username>ユーザー名</username>
>       <password>パスワード</password>
>       <nonProxyHosts>ある時は設定</nonProxyHosts>
>     </proxy>
>   </proxies>
> </settings>
> ```

### Step4. AirGraphのインストール設定
#### 実行ユーザの変更
* release_app/scripts/airgraphの17行目を修正
    ```bash
    user="sec"
    ```

#### [optional]自動起動設定をオフにする
* release_app/scripts/install_airgraph.shの60行目を修正
    ```bash
    # 自動起動設定
    set_startup off
    ```

### Step5. AirGraphのインストール
```bash
$ sudo ./install_airgraph.sh
```

### Step6. AirGraphの手動起動・停止（インストール直後など）
* 起動
    ```bash
    $ sudo service airgraph start
    ```

    > ブラウザにて以下のURLが起動できれば成功.
    > http://localhost:8080/main

* 停止
    ```bash
    $ sudo service airgraph stop
    ```

## Author
- AirGraph開発チーム - 株式会社セック
    [mail](airgraph@sec.co.jp)

## 注意事項

### 1. AirGraphの初回起動時は時間がかかる可能性があります
AirGraphは、起動時にWasanbon Binderに登録されているRTコンポーネントを全てCloneします。
そのため、初回起動時は、時間がかかる可能性があります。

### 2. rtc-template関連のエラー
スケルトンコード生成のためのツールとして、IDLCompilerを利用しています。
IDLCompilerを動作させるためには以下の対応が必要です。（暫定）

1. `~/.bashrc` のPYTHONPATHを修正（必要があれば）
    ```bash
    export PYTHONPATH=/usr/lib/python2.7/dist-packages:$PYTHONPATH
    ```

2. `/usr/lib/python2.7/dist-packages` にシンボリックリンクを追加
    ```bash
    cd /usr/lib/python2.7/dist-packages
    sudo ln -s /usr/lib/omniidl/omniidl ./omniidl
    sudo ln -s /usr/lib/omniidl/omniidl_be ./omniidl_be
    ```

3. omniidlの呼び出しの引数を修正
    ```bash
    sudo vim /usr/lib/x86_64-linux-gnu/openrtm-1.1/py_helper/cxx_svc_impl.py
    ```
    ```diff
    - tree = _omniidl.compile(file)
    + tree = _omniidl.compile(file, "")
    ```
