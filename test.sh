#!/bin/sh

# [必須]処理対象のメインクラス
MAIN_CLASS=test.Test

# java
JAVA_HOME=/usr/local/java/jdk1.6.0_45

# クラスパス変数
CLASSPATH=./bin:./lib/*

# カレントディレクトリに移動する。
cd `dirname ${0}`

# javaを起動する。
${JAVA_HOME}/bin/java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} &

# リターンコードを返却して終了する。
exit 0
