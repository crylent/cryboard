#pragma clang diagnostic push
#pragma ide diagnostic ignored "bugprone-reserved-identifier"

#include "jni.h"

#ifndef _LIBCPP_MAP
#include <map>
#endif

#ifndef WAVE_SYNTH_H
#include "instrument/WaveSynth.h"
#endif

#ifndef SINE_OSCILLATOR_H
#include "oscillators/SineOscillator.h"
#endif

#ifndef TRIANGLE_OSCILLATOR_H
#include "oscillators/TriangleOscillator.h"
#endif

#ifndef SQUARE_OSCILLATOR_H
#include "oscillators/SquareOscillator.h"
#endif

#ifndef SAWTOOTH_OSCILLATOR_H
#include "oscillators/SawtoothOscillator.h"
#endif

#ifndef REVERSE_SAWTOOTH_OSCILLATOR_H
#include "oscillators/ReverseSawtoothOscillator.h"
#endif

#ifndef LIMITER_H
#include "soundfx/Limiter.h"
#endif

#define JSTR(env, string) env->NewStringUTF(string)

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

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_setInstrument(JNIEnv *env, jobject thiz, jbyte channel,
                                               jobject instrument) {
    jclass instCls = env->GetObjectClass(instrument);
    jfieldID idOscillators = env->GetFieldID(
            instCls, "oscillators", "Ljava/util/List;"
    );
    jobject oscillators = env->GetObjectField(instrument, idOscillators);

    jclass listCls = env->FindClass("java/util/List");
    jmethodID listGetId = env->GetMethodID(listCls, "get", "(I)Ljava/lang/Object;");
    jmethodID listSizeId = env->GetMethodID(listCls, "size", "()I");

    jclass oscCls = env->FindClass("com/example/midilib/Oscillator");
    jfieldID idShape = env->GetFieldID(oscCls, "shape", "Lcom/example/midilib/Oscillator$Shape;");
    jfieldID idAmplitude = env->GetFieldID(oscCls, "amplitude", "F");
    jfieldID idPhase = env->GetFieldID(oscCls, "phase", "F");
    jfieldID idFreqFactor = env->GetFieldID(oscCls, "frequencyFactor", "F");
    jfieldID idDetuneObj = env->GetFieldID(oscCls, "detune", "Lcom/example/midilib/Oscillator$Detune;");

    jclass shapeCls = env->FindClass("com/example/midilib/Oscillator$Shape");
    jmethodID idShapeOrdinal = env->GetMethodID(shapeCls, "ordinal", "()I");

    jclass detuneCls = env->FindClass("com/example/midilib/Oscillator$Detune");
    jfieldID idUnisonVoices = env->GetFieldID(detuneCls, "unisonVoices", "I");
    jfieldID idDetuneValue = env->GetFieldID(detuneCls, "detune", "F");
    jmethodID idGetPhase = env->GetMethodID(detuneCls, "getPhaseShift", "(I)F");

    jfieldID idAttack = env->GetFieldID(instCls, "attack", "F");
    jfloat attack = env->GetFloatField(instrument, idAttack);

    jfieldID idDecay = env->GetFieldID(instCls, "decay", "F");
    jfloat decay = env->GetFloatField(instrument, idDecay);

    jfieldID idSustain = env->GetFieldID(instCls, "sustain", "F");
    jfloat sustain = env->GetFloatField(instrument, idSustain);

    jfieldID idRelease = env->GetFieldID(instCls, "release", "F");
    jfloat release = env->GetFloatField(instrument, idRelease);

    auto synth = make_shared<WaveSynth>(attack, decay, sustain, release);

    auto oscillatorsCount = static_cast<int>(env->CallIntMethod(oscillators, listSizeId));
    for (int i = 0; i < oscillatorsCount; i++) {
        jobject oscillator = env->CallObjectMethod(oscillators, listGetId, i);
        jobject shape = env->GetObjectField(oscillator, idShape);
        auto shapeOrdinal = static_cast<int>(env->CallIntMethod(shape, idShapeOrdinal));
        auto amplitude = static_cast<float>(env->GetFloatField(oscillator, idAmplitude));
        auto phase = static_cast<float>(env->GetFloatField(oscillator, idPhase));
        auto freqFactor = static_cast<float>(env->GetFloatField(oscillator, idFreqFactor));

        unique_ptr<Oscillator> osc;
        switch (shapeOrdinal) {
            case 0: // SINE
                osc = make_unique<SineOscillator>(amplitude, phase, freqFactor);
                break;
            case 1: // TRIANGLE
                osc = make_unique<TriangleOscillator>(amplitude, phase, freqFactor);
                break;
            case 2: // SQUARE
                osc = make_unique<SquareOscillator>(amplitude, phase, freqFactor);
                break;
            case 3: // SAW
                osc = make_unique<SawtoothOscillator>(amplitude, phase, freqFactor);
                break;
            case 4: // REVERSE SAW
                osc = make_unique<ReverseSawtoothOscillator>(amplitude, phase, freqFactor);
                break;
            default:
                break;
        }

        jobject detuneObj = env->GetObjectField(oscillator, idDetuneObj);
        if (detuneObj) {
            auto unisonVoices = static_cast<int>(env->GetIntField(detuneObj, idUnisonVoices));
            auto detuneValue = static_cast<float>(env->GetFloatField(detuneObj, idDetuneValue));
            auto detune = osc->setDetune(unisonVoices, detuneValue);
            for (int voice = 0; voice < unisonVoices; voice++) {
                auto phaseShift = env->CallFloatMethod(detuneObj, idGetPhase, voice);
                detune.setPhaseShift(voice, phaseShift);
            }
        }

        synth->addOscillator(move(osc));
    }
    AudioEngine::getChannels()[channel]->setInstrument(synth);
}

#define GET_PARAM(env, name) auto (name) = static_cast<float>((env)->CallFloatMethod(fx, idGetParameter, JSTR((env), #name)))

extern "C"
JNIEXPORT jbyte JNICALL
Java_com_example_midilib_MidiLib_addEffectExternal(JNIEnv *env, jobject thiz, jbyte channel,
                                                   jobject fx) {
    jclass fxCls = env->GetObjectClass(fx);
    jmethodID idGetId = env->GetMethodID(fxCls, "getId", "()I");
    auto fxId = static_cast<int>(env->CallIntMethod(fx, idGetId));
    jmethodID idGetParameter = env->GetMethodID(fxCls, "getParameter",
                                                "(Ljava/lang/String;)F");

    unique_ptr<SoundFX> effect;
    switch (fxId) { // NOLINT(hicpp-multiway-paths-covered)
        case 1: { // Limiter
            GET_PARAM(env, threshold);
            GET_PARAM(env, limit);
            GET_PARAM(env, attack);
            GET_PARAM(env, release);
            effect = make_unique<Limiter>(threshold, limit, attack, release);
            break;
        }
        default: // Unexpected effect ID
            throw exception();
    }

    uint8_t i;
    if (channel == -1) { // Master
        FXList& masterFx = AudioEngine::getMasterFX();
        i = masterFx.addEffect(move(effect));
    } else {
        throw exception(); // Not implemented
    }
    return static_cast<jbyte>(i);
}

FXList& getFXList(int8_t channel) {
    if (channel == -1) { // Master
        return AudioEngine::getMasterFX();
    } else {
        throw exception(); // Not implemented
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_clearEffects(JNIEnv *env, jobject thiz, jbyte channel) {
    FXList& fxList = getFXList(channel);
    fxList.clearEffects();
}

#define THRESHOLD 1
#define LIMIT 2
#define ATTACK 3
#define RELEASE 4

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_MidiLib_editEffect(JNIEnv *env, jobject thiz, jbyte channel, jbyte i,
                                            jstring param, jfloat value) {
    FXList& fxList = getFXList(channel);
    auto& effect = fxList.getEffect(i);
    auto _param = env->GetStringUTFChars(param, nullptr);

    map<string, int> mapping;
    mapping["threshold"] = THRESHOLD;
    mapping["limit"] = LIMIT;
    mapping["attack"] = ATTACK;
    mapping["release"] = RELEASE;

    try {
        auto& limiter = dynamic_cast<Limiter&>(effect);
        switch (mapping[_param]) {
            case THRESHOLD: limiter.setThreshold(value); break;
            case LIMIT: limiter.setLimit(value); break;
            case ATTACK: limiter.setAttack(value); break;
            case RELEASE: limiter.setRelease(value); break;
            default: break;
        }
    } catch (bad_cast& e) {}
}

#undef THRESHOLD
#undef LIMIT
#undef ATTACK
#undef RELEASE

#pragma clang diagnostic pop