#include "jni.h"

#pragma ide diagnostic ignored "bugprone-reserved-identifier"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_start__([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz) {
    AudioEngine::start();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_stop([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz) {
    AudioEngine::stop();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_start__ZI([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jboolean shared_mode,
                                               jint sample_rate) {
    if (shared_mode) {
        AudioEngine::start(oboe::SharingMode::Shared, sample_rate);
    } else {
        AudioEngine::start(oboe::SharingMode::Exclusive, sample_rate);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_noteOn([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel, jbyte note, jfloat amplitude) {
    AudioEngine::noteOn(channel, note, amplitude);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_noteOff([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel, jbyte note) {
    AudioEngine::noteOff(channel, note);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_allNotesOff([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel) {
    AudioEngine::allNotesOff(channel);
}