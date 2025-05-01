cmake_minimum_required(VERSION 3.10)

project(nanodalvik_vm)

add_library(
        nanodalvik_vm
        SHARED
        nanodalvik_vm.c
        nanodalvik_vm_jni.c
        util/stack.c
)

target_link_libraries(
        nanodalvik_vm
        log
)
