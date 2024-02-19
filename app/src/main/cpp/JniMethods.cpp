//
// Created by maksi on 12/26/2023.
//
#include <jni.h>
#include <stdint.h>
#include "NetworkImageStream.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_maxsimus_RemoteCam_NativeImageProcessor_nativeInit(JNIEnv *env, jclass clazz, jint address) {
    return (long) new NetworkImageStream(address);
}
extern "C"
JNIEXPORT void JNICALL
Java_maxsimus_RemoteCam_NativeImageProcessor_nativeProcess(JNIEnv *env, jclass clazz, jlong ptr,
                                                           jobject buffer, jint frame_number) {
    void *data = env->GetDirectBufferAddress(buffer);
    long size = env->GetDirectBufferCapacity(buffer);

    ((NetworkImageStream *) ptr)->sendData(frame_number, (char *) data, size);
}
extern "C"
JNIEXPORT void JNICALL
Java_maxsimus_RemoteCam_NativeImageProcessor_nativeClose(JNIEnv *env, jclass clazz, jlong ptr) {
    delete ((NetworkImageStream *) ptr);
}