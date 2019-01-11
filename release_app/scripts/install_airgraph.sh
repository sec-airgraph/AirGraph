#!/bin/bash

if [ "`whoami`" != "root" ]; then
  echo "usage: sudo ${0}"
  echo "sudoをつけてroot権限で実行してください。"
  exit 1
fi

TARGET_DIR=/opt/AirGraph

set_startup() {
    onoff=${1}
    cp ./scripts/airgraph /etc/init.d/.
    chmod 755 /etc/init.d/airgraph
    sysv-rc-conf airgraph ${onoff}
    systemctl daemon-reload
}

make_dir() {
  echo "make" ${1} "."
  mkdir -p ${1}
  chmod 777 ${1}
}

set -e
cd `dirname ${0}`
cd ..

# ディレクトリ生成
mkdir -p ${TARGET_DIR}
mkdir -p ${TARGET_DIR}/bin
mkdir -p ${TARGET_DIR}/scripts
mkdir -p ${TARGET_DIR}/data
make_dir ${TARGET_DIR}/logs

make_dir ${TARGET_DIR}/data/datasets
make_dir ${TARGET_DIR}/data/keras_models
make_dir ${TARGET_DIR}/data/keras_results
make_dir ${TARGET_DIR}/data/keras_templates
make_dir ${TARGET_DIR}/data/keras_workspace
make_dir ${TARGET_DIR}/data/rtm_components
make_dir ${TARGET_DIR}/data/rtm_packages
make_dir ${TARGET_DIR}/data/rtm_results
make_dir ${TARGET_DIR}/data/rtm_scripts
make_dir ${TARGET_DIR}/data/rtm_templates
make_dir ${TARGET_DIR}/data/rtm_workspace

# 必要なファイルをコピー
cp -r ./airgraph.jar ${TARGET_DIR}/bin/.
cp -r ./scripts/airgraph ${TARGET_DIR}/scripts/.

cp -r ./rtm_scripts/* ${TARGET_DIR}/data/rtm_scripts/.
cp -r ./rtm_templates/* ${TARGET_DIR}/data/rtm_templates/.
cp -r ./keras_templates/* ${TARGET_DIR}/data/keras_templates/.
cp -r ./keras_models/* ${TARGET_DIR}/data/keras_models/.

cp -r ./datasets/* ${TARGET_DIR}/data/datasets/.

# 自動起動設定
set_startup off

exit 0
