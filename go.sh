#!/bin/sh -x
CLASSPATH=../craftbukkit-1.1-R4.jar javac src/com/Evilgeniuses/Hardcore/*.java -Xlint:unchecked -Xlint:deprecation
cd src
jar cf Hardcore.jar com/ *.yml
mv *.jar ..
cd ..
