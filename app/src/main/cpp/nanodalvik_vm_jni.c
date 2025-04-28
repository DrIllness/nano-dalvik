#include <jni.h>
#include <android/log.h>

#define LOG_TAG "kekus"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_loadProgramNative(JNIEnv *env, jobject thiz, jstring code) {
    // load program here
}

JNIEXPORT void JNICALL
Java_com_nanodalvik_data_cpp_NativeNanoDalvikVMImpl_nativeTestHello(JNIEnv *env, jobject thiz) {
    LOGI("hello from nanodalvik native layer");
}