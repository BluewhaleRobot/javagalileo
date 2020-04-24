#!/bin/bash
cd GalileoSDK/GalileoSDK
mkdir build
cd build
cmake ..
make -j
cd ../../..
cd JNIGalileoWrapper/JNIGalileoWrapper
mkdir build
cd build
cmake ..
make -j
cd ../../../javagalileo
mvn clean
mvn compile
cd target/classes
javah -classpath . javagalileo.GalileoSDK
rm ../../include/*.h
mv *.h ../../include
cd ..