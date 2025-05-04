#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include "nanodalvik_vm.h"

static NanoDalvik* vm;

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_loadProgramNative(JNIEnv *env, jobject thiz, jstring code)
{
    const char* nativeStr = (*env)->GetStringUTFChars(env, code, 0);
    nanodalvik_load_program(vm, nativeStr);

    (*env)->ReleaseStringUTFChars(env, code, nativeStr);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_startUpNative(JNIEnv *env, jobject thiz)
{
    vm = malloc(sizeof(NanoDalvik));
    nanodalvik_initialize(vm);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_executeNextOpNative(JNIEnv *env, jobject thiz)
{
    nanodalvik_execute_next_op(vm);
}