#pragma clang diagnostic push
#pragma ide diagnostic ignored "bugprone-reserved-identifier"

#include "jni.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_start__(JNIEnv *env, jobject thiz) {
    AudioEngine::start();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_stop(JNIEnv *env, jobject thiz) {
    AudioEngine::stop();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_start__ZI(JNIEnv *env, jobject thiz, jboolean shared_mode,
                                           jint sample_rate) {
    if (shared_mode) {
        AudioEngine::start(oboe::SharingMode::Shared, sample_rate);
    } else {
        AudioEngine::start(oboe::SharingMode::Exclusive, sample_rate);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_noteOn(JNIEnv *env, jobject thiz, jbyte channel, jbyte note, jfloat amplitude) {
    AudioEngine::noteOn(channel, note, amplitude);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_noteOff(JNIEnv *env, jobject thiz, jbyte channel, jbyte note) {
    AudioEngine::noteOff(channel, note);
}

#pragma clang diagnostic pop