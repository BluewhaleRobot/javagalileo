#!/bin/bash
# build JNIGalileoWrapper
cd JNIGalileoWrapper/JNIGalileoWrapper
mkdir build
cd build
cmake ..
make -j
cd ../../../
mkdir javagalileo/src/main/resources
cp JNIGalileoWrapper/JNIGalileoWrapper/build/libJNIGalileoWrapper.so javagalileo/src/main/resources/
cp /usr/local/lib/libGalileoSDK.so javagalileo/src/main/resources/
cd javagalileo
mvn package
cd ..

