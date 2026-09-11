#include <jni.h>
#include <android/log.h>
#include "oboe/Oboe.h"
#include <cmath>
#include <cstring>

#define LOG_TAG "Martodosko"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// ==================================================
// ✅ GLOBAL NA SETTINGS — KONEKTADO SA KOTLIN
// ==================================================
struct AudioSettings {
    float inputVolume = 0.8f;
    float outputVolume = 0.8f;
    bool reverbEnabled = false;
    bool distortionEnabled = false;
    float reverbLevel = 0.5f;
    float distortionLevel = 0.5f;
    int sampleRate = 48000;
    int channelCount = 2;
};

static AudioSettings gSettings;
static std::shared_ptr<oboe::AudioStream> gStream;
static bool gIsRunning = false;

// ==================================================
// ✅ AUDIO CALLBACK — TUMATAKBO SA TUNOG (REAL-TIME!)
// ==================================================
class AudioCallback : public oboe::AudioStreamCallback {
public:
    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override {

        float *buffer = static_cast<float *>(audioData);
        int numSamples = numFrames * audioStream->getChannelCount();

        // ==================================================
        // ✅ DITO NANGYAYARI ANG LAHAT — 0ms LATENCY!
        // ==================================================
        for (int i = 0; i < numSamples; i++) {
            float sample = buffer[i];

            // 🔊 Volume Control
            sample *= gSettings.inputVolume;

            // 🎸 Distortion Effect
            if (gSettings.distortionEnabled) {
                float drive = 1.0f + gSettings.distortionLevel * 4.0f;
                sample = std::tanh(sample * drive) / std::tanh(drive);
            }

            // 🔄 Reverb / Echo Effect
            if (gSettings.reverbEnabled) {
                static float delayBuffer[96000] = {0};
                static int delayIndex = 0;
                float feedback = gSettings.reverbLevel * 0.4f;
                float delayed = delayBuffer[delayIndex];
                delayBuffer[delayIndex] = sample + delayed * feedback;
                delayIndex = (delayIndex + 1) % 96000;
                sample = sample * 0.6f + delayed * 0.4f;
            }

            // 🔊 Output Volume
            sample *= gSettings.outputVolume;

            // ✅ Iwasan ang sobrang lakas na tunog
            if (sample > 1.0f) sample = 1.0f;
            if (sample < -1.0f) sample = -1.0f;

            buffer[i] = sample;
        }

        return oboe::DataCallbackResult::Continue;
    }
};

static AudioCallback gCallback;

// ==================================================
// ✅ SIMULAN ANG AUDIO — TAWAGIN MULA SA KOTLIN
// ==================================================
extern "C" JNIEXPORT jboolean JNICALL
Java_com_martodosko_studio_MainActivity_startAudioEngine(JNIEnv* env, jobject) {
    if (gIsRunning) {
        LOGI("✅ Audio Engine — TUMATAKBO NA");
        return JNI_TRUE;
    }

    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::InputOutput)
            ->setSampleRate(gSettings.sampleRate)
            ->setChannelCount(gSettings.channelCount)
            ->setFormat(oboe::AudioFormat::Float)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setDataCallback(&gCallback);

    oboe::Result result = builder.openStream(gStream);
    if (result != oboe::Result::OK) {
        LOGE("❌ Hindi mabuksan ang Audio Stream: %s", oboe::convertToText(result));
        return JNI_FALSE;
    }

    gStream->requestStart();
    gIsRunning = true;
    LOGI("✅ Audio Engine — NAGSIMULA! Low Latency Mode ON!");
    return JNI_TRUE;
}

// ==================================================
// ✅ ITIGIL ANG AUDIO
// ==================================================
extern "C" JNIEXPORT void JNICALL
Java_com_martodosko_studio_MainActivity_stopAudioEngine(JNIEnv* env, jobject) {
    if (gStream) {
        gStream->requestStop();
        gStream->close();
        gStream.reset();
    }
    gIsRunning = false;
    LOGI("✅ Audio Engine — NAHINTO");
}

// ==================================================
// ✅ I-UPDATE ANG SETTINGS — TAWAGIN MULA SA KOTLIN
// ==================================================
extern "C" JNIEXPORT void JNICALL
Java_com_martodosko_studio_MainActivity_setVolume(JNIEnv* env, jobject, jfloat inputVol, jfloat outputVol) {
    gSettings.inputVolume = std::max(0.0f, std::min(1.0f, inputVol));
    gSettings.outputVolume = std::max(0.0f, std::min(1.0f, outputVol));
    LOGI("✅ Volume — Input: %.2f  Output: %.2f", gSettings.inputVolume, gSettings.outputVolume);
}

extern "C" JNIEXPORT void JNICALL
Java_com_martodosko_studio_MainActivity_setDistortion(JNIEnv* env, jobject, jboolean enabled, jfloat level) {
    gSettings.distortionEnabled = enabled;
    gSettings.distortionLevel = std::max(0.0f, std::min(1.0f, level));
    LOGI("✅ Distortion — %s  Level: %.2f", enabled ? "ON" : "OFF", gSettings.distortionLevel);
}

extern "C" JNIEXPORT void JNICALL
Java_com_martodosko_studio_MainActivity_setReverb(JNIEnv* env, jobject, jboolean enabled, jfloat level) {
    gSettings.reverbEnabled = enabled;
    gSettings.reverbLevel = std::max(0.0f, std::min(1.0f, level));
    LOGI("✅ Reverb — %s  Level: %.2f", enabled ? "ON" : "OFF", gSettings.reverbLevel);
}

// ==================================================
// ✅ TEST FUNCTION — NANDITO PA RIN!
// ==================================================
extern "C" JNIEXPORT jstring JNICALL
Java_com_martodosko_studio_MainActivity_getNativeInfo(JNIEnv* env, jobject) {
    LOGI("✅ Martodosko Native Library NAKAKASOK!");
    return env->NewStringUTF("Martodosko Audio Engine — HANDA NA! Low Latency — ON!");
}
