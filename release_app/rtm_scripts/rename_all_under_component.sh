#!/bin/sh

if [ $# -ne 3 ]; then
  exit 1
fi

DIR_PATH=$1
OLD_NAME=$2
NEW_NAME=$3

# RTCのディレクトリに移動する
cd ${DIR_PATH}/${OLD_NAME}

# ファイルの中身をすべて変更する
find . -type f | xargs grep -l ${OLD_NAME} | xargs sed -i -e s/${OLD_NAME}/${NEW_NAME}/g

# 1階層目のファイルを変更する
rename s/${OLD_NAME}/${NEW_NAME}/g *

# 2階層目のファイルを変更する
rename s/${OLD_NAME}/${NEW_NAME}/g */*

# 3階層目のファイルを変更する
rename s/${OLD_NAME}/${NEW_NAME}/g */*/*

# 4階層目のファイルを変更する
rename s/${OLD_NAME}/${NEW_NAME}/g */*/*/*

exit 0
