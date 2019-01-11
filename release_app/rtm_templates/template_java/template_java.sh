#!/bin/sh
if test "x$RTM_JAVA_ROOT" = "x" ; then
    echo "Environment variable RTM_JAVA_ROOT is not set."
    echo "Please specify the OpenRTM-aist installation directory."
    echo "Abort."
    exit 1
fi
export CLASSPATH=.:${RTM_JAVA_ROOT}/jar/OpenRTM-aist-1.1.0.jar:${RTM_JAVA_ROOT}/jar/commons-cli-1.1.jar
cd `dirname $0`/bin
java template_javaComp -f rtc.conf $*
