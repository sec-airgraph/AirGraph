# AirGraph

AirGraphはAIとRobot開発のための、グラフィカルなIDEです。
詳細は [ドキュメント](https://sec-airgraph.github.io/AirGraph-doc) をお読みください.

<img src="./docs/img/airgraph.png" width="320px">

*Read this in other languages: [English](README.en.md).*

## システム要件
* Ubuntu 20.04

> 開発環境は `Ubuntu 20.04 64bit on amd64 processor`、ブラウザは `Firefox` です。

## 依存関係
* Java Open JDK 8 (AdoptOpenJDK)
* Apache Maven
* [OpenRTM-aist](http://openrtm.org/) 1.2.2
* [wasanbon](http://wasanbon.org/) 1.2.0
* [Keras](https://keras.io/) 2.7.0
    - [Tensorflow](https://www.tensorflow.org) 2.7.0
    - hdf5, h5pyは必須.
* [wasanbon-webframework](https://github.com/wasanbon/wasanbon_webframework) 1.0.1

## インストール手順
### Step1. 依存パッケージのインストール
* パッケージ情報の最新化
    ```bash
    $ sudo apt update
    $ sudo apt upgrade -y
    $ sudo apt update
    ```
    > apt upgardeは任意

* Java Open JDK 8 (AdoptOpenJDK)
    ```bash
    $ wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -
    $ echo deb https://adoptopenjdk.jfrog.io/adoptopenjdk/deb focal main | sudo tee /etc/apt/sources.list.d/adoptopenjdk.list
    $ sudo apt update
    $ sudo apt install adoptopenjdk-8-hotspot
    $ echo "export JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64" >> ~/.bashrc
    $ echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    ```
    > 上記パスはUbuntu20.04(64bit版、armプロセッサ)の場合の例のため，必要に応じて変更

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
    $ wget http://archive.ubuntu.com/ubuntu/pool/universe/s/sysv-rc-conf/sysv-rc-conf_0.99.orig.tar.gz
    $ tar zxvf sysv-rc-conf_0.99.orig.tar.gz
    $ cd sysv-rc-conf-0.99
    $ sudo apt install make
    $ sudo make
    $ sudo make install
    $ sudo apt install libcurses-ui-perl libterm-readkey-perl libcurses-perl
    ```

* rename
    ```bash
    $ sudo apt install rename
    ```

### Step2. 依存関係のインストール
公式サイトを参照してインスト―ル。 ([OpenRTM-aist](http://openrtm.org/), [wasanbon](http://wasanbon.org/), [Keras](https://keras.io/)), [wasanbon-webframework](https://github.com/wasanbon/wasanbon_webframework)


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
* airgraph/src/main/resources/application.propertiesの17, 20行目を修正
    ```bash
    #workspace Directory Path
    workspace.local.directory.path=/home/sec/.wasanbon-webframework/develop_workspace/

    #wasanbon webframework Directory Path
    wasanbon.webframework.local.directory.path=/home/sec/.wasanbon-webframework/
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

