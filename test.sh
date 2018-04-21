#!/bin/sh

JAVA_HOME=/usr/local/java/jdk1.6.0_45
CLASSPATH=./bin:./lib/*
MAIN_CLASS=me.q9029.discord.bot.DiscordBotMain

cd `dirname ${0}`

${JAVA_HOME}/bin/java ${JAVA_OPTS} -classpath ${CLASSPATH} ${MAIN_CLASS}

exit 0
