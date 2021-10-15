# AirGraph

AirGraph is AI and Robot Graphical IDE.
Please read the [Document](https://sec-airgraph.github.io/AirGraph-doc) for details.

<img src="https://github.com/sec-airgraph/airgraph/blob/master/docs/img/airgraph.png" width="320px">

## System Requirements
* Ubuntu 20.04

> Our developing environment is `Ubuntu 20.04 64bit on arm64 processor`

## Dependencies
* Java Open JDK 8 (AdoptOpenJDK)
* Apache Maven
* [OpenRTM-aist](http://openrtm.org/) 1.2.2
* [wasanbon](http://wasanbon.org/) 1.1.0.post5
* [Keras](https://keras.io/) 2.2.4 with [Tensorflow](https://www.tensorflow.org) r1.12
    - hdf5, h5py must be installed.

## Installation
### Step1. Install dependent packages
* Update packages
    ```bash
    $ sudo apt update
    $ sudo apt upgrade -y
    $ sudo apt update
    ```
    > apt upgarde is optional.

* Java Open JDK 8 (AdoptOpenJDK)
    ```bash
    # wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add -
    # echo deb https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/ xenial main > /etc/apt/sources.list.d/adoptopenjdk.list
    $ sudo apt update
    $ sudo apt install adoptopenjdk-8-hotspot
    $ echo "export JAVA_HOME=/usr/lib/jvm/adoptopenjdk-8-hotspot-amd64" >> ~/.bashrc
    $ echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
    ```
    > This path is for Ubuntu 16.04 64 bit version on arm64 CPU.

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

### Step2. Install dependent tools
Visit Official Pages. ([OpenRTM-aist](http://openrtm.org/), [wasanbon](http://wasanbon.org/), [Keras](https://keras.io/))

### Step3. Build AirGraph
* Clone, Build
    ```bash
    $ cd ~
    $ git clone --recursive https://github.com/sec-airgraph/airgraph.git
    $ cd airgraph/release_app/scripts
    $ ./build_airgraph.sh
    ```

> Setting maven's proxy.
> ```bash
> $ vi ~/.m2/settings.xml
> ```
> ```xml
> <settings>
>   <proxies>
>     <proxy>
>       <active>true</active>
>       <protocol>Protocol</protocol>
>       <host>Host Name</host>
>       <port>Port Number</port>
>       <username>User Name</username>
>       <password>Password</password>
>       <nonProxyHosts>Non Proxy Host Name</nonProxyHosts>
>     </proxy>
>   </proxies>
> </settings>
> ```

### Step4. Install Settings
#### Change Execution User
* release_app/scripts/airgraph (Line.17)
    ```bash
    user="sec"
    ```

#### [Optional]Disable Auto StartUp
* release_app/scripts/build_airgraph.sh (Line.60)
    ```bash
    # Auto Startup Setting
    set_startup off
    ```

### Step5. Install AirGraph
```bash
$ sudo ./install_airgraph.sh
```

### Step6. Start AirGraph Manually
* Start
    ```bash
    $ sudo service airgraph start
    ```

    > Access URL. 
    > http://localhost:8080/main

* Stop
    ```bash
    $ sudo service airgraph stop
    ```

## Author
- Team AirGraph - System Engineering Consultants Co., LTD.
    [mail](airgraph@sec.co.jp)
