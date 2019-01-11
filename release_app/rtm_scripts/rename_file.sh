#!/bin/sh

if [ $# -ne 3 ]; then
  exit 1
fi

DIR_PATH=$1
OLD_NAME=$2
NEW_NAME=$3

# ディレクトリに移動する
cd ${DIR_PATH}

# 1階層目のファイルを変更する
rename s/${OLD_NAME}/${NEW_NAME}/g *

exit 0
