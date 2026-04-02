cmake_minimum_required(VERSION 3.22.1)

project(signal_processor_native)

add_library(
    signal_processor_native
    SHARED
    signal_processor_jni.cpp
)

target_compile_features(signal_processor_native PRIVATE cxx_std_17)
target_compile_options(
    signal_processor_native
    PRIVATE
    -O3
)

find_library(log-lib log)

target_link_libraries(
    signal_processor_native
    PRIVATE
    ${log-lib}
)
