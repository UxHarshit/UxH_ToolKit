cmake_minimum_required(VERSION 3.18.1)


project("uxh_toolkit")

add_library(uxh_toolkit
        SHARED
        main.cpp)

find_library(log-lib
        log)

target_link_libraries(
        uxh_toolkit
        ${log-lib})

add_library(
        root
        SHARED
        rootMain.cpp
)
find_library(log-lib
        log)
target_link_libraries(
        root
        ${log-lib})