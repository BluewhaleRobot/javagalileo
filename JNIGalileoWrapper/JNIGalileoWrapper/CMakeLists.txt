cmake_minimum_required(VERSION 3.1.0)
project(
   JNIGalileoWrapper
   VERSION 1.0.0
   LANGUAGES CXX
)

if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release)
endif()

# Check C++11 or C++0x support
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(EXECUTABLE_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/bin)

include(GNUInstallDirs)

find_package(GalileoSDK REQUIRED)

if(NOT JAVA_HOME)
   set(JAVA_HOME "/usr/lib/jvm/java-8-oracle")   
endif()

if(NOT ANDROID)
   find_package(JNI REQUIRED)
endif()


include_directories(
    ${PROJECT_SOURCE_DIR}
    ${PROJECT_SOURCE_DIR}/../../javagalileo/include
    ${GalileoSDK_INCLUDE_DIR}
    ${JAVA_INCLUDE_PATH}
    ${JAVA_INCLUDE_PATH2}
)

set(BUILD_SHARED_LIBS ON)

add_library(JNIGalileoWrapper
   ${PROJECT_SOURCE_DIR}/JNIGalileoWrapper.cpp
)

if(ANDROID)
target_link_libraries(JNIGalileoWrapper
   ${JNI_LIBRARIES}
   ${GalileoSDK_LIBRARIES}
   log
)
else()
target_link_libraries(JNIGalileoWrapper
   ${JNI_LIBRARIES}
   ${GalileoSDK_LIBRARIES}
)
endif()