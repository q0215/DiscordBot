#!/bin/sh

# [�K�{]�����Ώۂ̃��C���N���X
MAIN_CLASS=test.Test

# java
JAVA_HOME=/usr/local/java/jdk1.6.0_45

# �N���X�p�X�ϐ�
CLASSPATH=./bin:./lib/*

# �J�����g�f�B���N�g���Ɉړ�����B
cd `dirname ${0}`

# java���N������B
${JAVA_HOME}/bin/java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS} &

# ���^�[���R�[�h��ԋp���ďI������B
exit 0
