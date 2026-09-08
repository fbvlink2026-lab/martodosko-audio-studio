#include <jni.h>
#include <android/log.h>

#define LOG_TAG "Martodosko"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_martodosko_studio_MainActivity_getNativeInfo(JNIEnv* env, jobject /* this */) {
    LOGI("✅ Martodosko Native Library NAKAKASOK!");
    return env->NewStringUTF("Martodosko Audio Engine — Handang Tumatakbo!");
}
