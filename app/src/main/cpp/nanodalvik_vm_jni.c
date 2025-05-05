#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include "nanodalvik_vm.h"

static NanoDalvik* vm;

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_loadProgramNative(JNIEnv* env, jobject thiz,
                                                                      jstring code)
{
    const char* nativeStr = (*env)->GetStringUTFChars(env, code, 0);
    nanodalvik_load_program(vm, nativeStr);

    (*env)->ReleaseStringUTFChars(env, code, nativeStr);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_startUpNative(JNIEnv* env, jobject thiz)
{
    vm = malloc(sizeof(NanoDalvik));
    nanodalvik_initialize(vm);
}

void notify_java_layer(JNIEnv* env, jobject* thiz, OpResult* res)
{
    jclass cls = (*env)->FindClass(env, "com/nanodalvik/data/cpp/NativeOPExecutionResult");
    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>",
                                         "(ILjava/lang/String;Ljava/lang/String;)V");
    jstring outputStr = (*env)->NewStringUTF(env, res->output);
    jstring errorStr = (*env)->NewStringUTF(env, res->error);

    jobject result = (*env)->NewObject(env, cls, ctor, 0, outputStr, errorStr);
    jclass clazz = (*env)->GetObjectClass(env, *thiz);

    jmethodID method = (*env)->GetMethodID(
            env,
            clazz,
            "executionResult",
            "(Lcom/nanodalvik/data/cpp/NativeOPExecutionResult;)V"
    );
    (*env)->CallVoidMethod(env, *thiz, method, result);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_executeNextOpNative(JNIEnv* env, jobject thiz)
{
    OpResult* res = nanodalvik_execute_next_op(vm);
    notify_java_layer(env, &thiz, res);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_clearNative(JNIEnv* env, jobject thiz)
{
    nanodalvik_clear(vm);
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_executeProgramNative(JNIEnv* env, jobject thiz)
{
    OpResult* res;
    while (nanodalvik_has_next_op(vm))
    {
        res = nanodalvik_execute_next_op(vm);
        notify_java_layer(env, &thiz, res);
    }
}

