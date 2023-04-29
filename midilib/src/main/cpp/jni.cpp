#pragma clang diagnostic push
#pragma ide diagnostic ignored "bugprone-reserved-identifier"

#include "jni.h"
#include <map>
#include "instrument/SynthInstrument.h"
//#include "instrument/SynthInstrument.cpp"
#include "oscillators/SineOscillator.h"
#include "oscillators/TriangleOscillator.h"
#include "oscillators/SquareOscillator.h"
#include "oscillators/SawtoothOscillator.h"
#include "oscillators/ReverseSawtoothOscillator.h"
#include "soundfx/Limiter.h"
//#include <android/asset_manager_jni.h>
#include "instrument/AssetInstrument.h"
#include "instrument/InstrumentLib.h"

#define JSTR(env, string) env->NewStringUTF(string)

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

#define SYNTH_INSTRUMENT 0
#define ASSET_INSTRUMENT 1

#define SHAPE_SINE 0
#define SHAPE_TRIANGLE 1
#define SHAPE_SQUARE 2
#define SHAPE_SAW 3
#define SHAPE_REVERSE_SAW 4

unique_ptr<Oscillator> makeOscillator(int shapeOrdinal, float amplitude, float phase, float freqFactor) {
    switch (shapeOrdinal) {
        case SHAPE_SINE:
            return make_unique<SineOscillator>(amplitude, phase, freqFactor);
        case SHAPE_TRIANGLE:
            return make_unique<TriangleOscillator>(amplitude, phase, freqFactor);
        case SHAPE_SQUARE:
            return make_unique<SquareOscillator>(amplitude, phase, freqFactor);
        case SHAPE_SAW:
            return make_unique<SawtoothOscillator>(amplitude, phase, freqFactor);
        case SHAPE_REVERSE_SAW:
            return make_unique<ReverseSawtoothOscillator>(amplitude, phase, freqFactor);
        default:
            return nullptr;
    }
}

void addOscillator(JNIEnv *env, SynthInstrument& inst, jobject oscillator) {
    jclass oscCls = env->GetObjectClass(oscillator);

    jfieldID idShape = env->GetFieldID(oscCls, "shape",
                                       "Lcom/example/midilib/Oscillator$Shape;");
    jfieldID idAmplitude = env->GetFieldID(oscCls, "amplitude", "F");
    jfieldID idPhase = env->GetFieldID(oscCls, "phase", "F");
    jfieldID idFreqFactor = env->GetFieldID(oscCls, "frequencyFactor", "F");
    jfieldID idDetuneObj = env->GetFieldID(oscCls, "detune",
                                           "Lcom/example/midilib/Oscillator$Detune;");

    jclass shapeCls = env->FindClass("com/example/midilib/Oscillator$Shape");
    jmethodID idShapeOrdinal = env->GetMethodID(shapeCls, "ordinal", "()I");

    jclass detuneCls = env->FindClass("com/example/midilib/Oscillator$Detune");
    jfieldID idUnisonVoices = env->GetFieldID(detuneCls, "unisonVoices", "I");
    jfieldID idDetuneValue = env->GetFieldID(detuneCls, "detune", "F");
    jmethodID idGetPhase = env->GetMethodID(detuneCls, "getPhaseShift", "(I)F");

    jobject shape = env->GetObjectField(oscillator, idShape);
    auto shapeOrdinal = static_cast<int>(env->CallIntMethod(shape, idShapeOrdinal));
    auto amplitude = static_cast<float>(env->GetFloatField(oscillator, idAmplitude));
    auto phase = static_cast<float>(env->GetFloatField(oscillator, idPhase));
    auto freqFactor = static_cast<float>(env->GetFloatField(oscillator, idFreqFactor));

    unique_ptr<Oscillator> osc = makeOscillator(shapeOrdinal, amplitude, phase, freqFactor);

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

    inst.addOscillator(move(osc));
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_midilib_AudioEngine_createInstrumentExternal__Lcom_example_midilib_instrument_Instrument_2(JNIEnv *env, [[maybe_unused]] jobject thiz, jobject instrument) {
    jclass instCls = env->GetObjectClass(instrument);
    jclass synthInstCls = env->FindClass("com/example/midilib/instrument/SynthInstrument");
    //jclass assetInstCls = env->FindClass("com/example/midilib/instrument/AssetInstrument");
    uint8_t instType;
    if (env->IsInstanceOf(instrument, synthInstCls)) instType = SYNTH_INSTRUMENT;
    else instType = ASSET_INSTRUMENT;

    jfieldID idAttack = env->GetFieldID(instCls, "attack", "F");
    jfloat attack = env->GetFloatField(instrument, idAttack);
    jfieldID idDecay = env->GetFieldID(instCls, "decay", "F");
    jfloat decay = env->GetFloatField(instrument, idDecay);
    jfieldID idSustain = env->GetFieldID(instCls, "sustain", "F");
    jfloat sustain = env->GetFloatField(instrument, idSustain);
    jfieldID idRelease = env->GetFieldID(instCls, "release", "F");
    jfloat release = env->GetFloatField(instrument, idRelease);

    uint32_t position;

    if (instType == SYNTH_INSTRUMENT) {
        jmethodID idAsSynthInst = env->GetMethodID(instCls, "asSynthInstrument","()Lcom/example/midilib/instrument/SynthInstrument;");
        instrument = env->CallObjectMethod(instrument, idAsSynthInst);
        auto inst = make_shared<SynthInstrument>(attack, decay, sustain, release);
        jmethodID idOscillators = env->GetMethodID(
                synthInstCls, "getOscillators", "()Ljava/util/List;"
        );
        jobject oscillators = env->CallObjectMethod(instrument, idOscillators);

        jclass listCls = env->FindClass("java/util/List");
        jmethodID listGetId = env->GetMethodID(listCls, "get", "(I)Ljava/lang/Object;");
        jmethodID listSizeId = env->GetMethodID(listCls, "size", "()I");

        auto oscillatorsCount = static_cast<int>(env->CallIntMethod(oscillators, listSizeId));
        for (int i = 0; i < oscillatorsCount; i++) {
            jobject oscillator = env->CallObjectMethod(oscillators, listGetId, i);
            addOscillator(env, *inst, oscillator);
        }
        position = InstrumentLib::addInstrument(inst);
    } else {
        auto inst = make_shared<AssetInstrument>(attack, decay, sustain, release);
        position = InstrumentLib::addInstrument(inst);
    }
    return (jint) position;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setInstrumentExternal([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel,
                                                           jint instrument) {
    AudioEngine::getChannels()[channel]->setInstrument(
            InstrumentLib::getInstrument(instrument)
            );
}

/*#define ATTACK 1
#define DECAY 2
#define SUSTAIN 3
#define RELEASE 4

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_editInstrumentExternal(JNIEnv *env, [[maybe_unused]] jobject thiz,
                                                            jint instrument, jstring param,
                                                            jfloat value) {
    const auto& inst = InstrumentLib::getInstrument(instrument);
    auto _param = env->GetStringUTFChars(param, nullptr);

    map<string, int> mapping;
    mapping["attack"] = ATTACK;
    mapping["decay"] = DECAY;
    mapping["sustain"] = SUSTAIN;
    mapping["release"] = RELEASE;

    switch (mapping[_param]) {
        case ATTACK: inst->setAttack(value); break;
        case DECAY: inst->setDecay(value); break;
        case SUSTAIN: inst->setSustain(value); break;
        case RELEASE: inst->setRelease(value); break;
    }
}*/

#define AS_SYNTH(instrument) (dynamic_cast<SynthInstrument&>(*InstrumentLib::getInstrument(instrument)))

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_addOscillatorExternal(JNIEnv *env, [[maybe_unused]] jobject thiz,
                                                           jint instrument, jobject oscillator) {
    auto& inst = AS_SYNTH(instrument);
    addOscillator(env, inst, oscillator);
}

/*#define AMPLITUDE 1
#define PHASE 2
#define FREQ_FACTOR 3

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_editOscillatorExternal(JNIEnv *env, [[maybe_unused]] jobject thiz,
                                                            jint instrument, jint osc_index,
                                                            jstring param, jfloat value) {
    auto& inst = AS_SYNTH(instrument);
    auto& osc = inst.getOscillatorByIndex(osc_index);

    auto _param = env->GetStringUTFChars(param, nullptr);

    map<string, int> mapping;
    mapping["amplitude"] = AMPLITUDE;
    mapping["phase"] = PHASE;
    mapping["frequencyFactor"] = FREQ_FACTOR;

    switch (mapping[_param]) {
        case AMPLITUDE: osc.setAmplitude(value); break;
        case PHASE: osc.setPhase(value); break;
        case FREQ_FACTOR: osc.setFreqFactor(value); break;
    }
}

#undef AMPLITUDE
#undef PHASE
#undef FREQ_FACTOR*/

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setInstrumentAttackExternal(JNIEnv *env, jobject thiz,
                                                                 jint instrument, jfloat attack) {
    AS_SYNTH(instrument).setAttack(attack);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setInstrumentDecayExternal(JNIEnv *env, jobject thiz,
                                                                jint instrument, jfloat decay) {
    AS_SYNTH(instrument).setDecay(decay);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setInstrumentSustainExternal(JNIEnv *env, jobject thiz,
                                                                  jint instrument, jfloat sustain) {
    AS_SYNTH(instrument).setSustain(sustain);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setInstrumentReleaseExternal(JNIEnv *env, jobject thiz,
                                                                  jint instrument, jfloat release) {
    AS_SYNTH(instrument).setRelease(release);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setOscillatorAmplitudeExternal(JNIEnv *env, jobject thiz,
                                                                    jint instrument, jint osc_index,
                                                                    jfloat amplitude) {
    AS_SYNTH(instrument).getOscillatorByIndex(osc_index).setAmplitude(amplitude);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setOscillatorPhaseExternal(JNIEnv *env, jobject thiz,
                                                                jint instrument, jint osc_index,
                                                                jfloat phase) {
    AS_SYNTH(instrument).getOscillatorByIndex(osc_index).setPhase(phase);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setOscillatorFrequencyFactorExternal(JNIEnv *env, jobject thiz,
                                                                          jint instrument,
                                                                          jint osc_index,
                                                                          jfloat freq_factor) {
    AS_SYNTH(instrument).getOscillatorByIndex(osc_index).setFreqFactor(freq_factor);
}

//template<typename Shape> void SynthInstrument::setOscillatorShape(uint8_t index);

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setOscillatorShapeExternal([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jint instrument,
                                                                jint osc_index, jint shape) {
    //auto& inst = AS_SYNTH(instrument);
    auto& inst = dynamic_cast<SynthInstrument&>(*InstrumentLib::getInstrument(instrument));
    switch (shape) {
        case SHAPE_SINE:
            inst.setOscillatorShape<SineOscillator>(osc_index); break;
        case SHAPE_TRIANGLE:
            inst.setOscillatorShape<TriangleOscillator>(osc_index); break;
        case SHAPE_SQUARE:
            inst.setOscillatorShape<SquareOscillator>(osc_index); break;
        case SHAPE_SAW:
            inst.setOscillatorShape<SawtoothOscillator>(osc_index); break;
        case SHAPE_REVERSE_SAW:
            inst.setOscillatorShape<ReverseSawtoothOscillator>(osc_index); break;
        default:
            break;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_enableDetuneExternal(JNIEnv *env, jobject thiz,
                                                          jint instrument, jint osc_index,
                                                          jint unison_voices, jfloat detune) {
    AS_SYNTH(instrument)
        .getOscillatorByIndex(osc_index)
        .setDetune(unison_voices, detune);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_disableDetuneExternal(JNIEnv *env, jobject thiz,
                                                           jint instrument, jint osc_index) {
    AS_SYNTH(instrument)
        .getOscillatorByIndex(osc_index)
        .clearDetune();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setUnisonVoicesExternal(JNIEnv *env, jobject thiz,
                                                             jint instrument, jint osc_index,
                                                             jint unison_voices) {
    AS_SYNTH(instrument)
        .getOscillatorByIndex(osc_index)
        .getDetune()
        .setUnisonVoices(unison_voices);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setDetuneLevelExternal(JNIEnv *env, jobject thiz,
                                                            jint instrument, jint osc_index,
                                                            jfloat detune) {
    AS_SYNTH(instrument)
        .getOscillatorByIndex(osc_index)
        .getDetune()
        .setDetune(detune);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setPhaseShiftExternal(JNIEnv *env, jobject thiz,
                                                           jint instrument, jint osc_index,
                                                           jint voice, jfloat phase) {
    AS_SYNTH(instrument)
        .getOscillatorByIndex(osc_index)
        .getDetune()
        .setPhaseShift(voice, phase);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_removeOscillatorExternal([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz,
                                                              jint instrument, jint osc_index) {
    dynamic_cast<SynthInstrument&>(*InstrumentLib::getInstrument(instrument)).removeOscillator(osc_index);
}

#define GET_PARAM(env, name) auto (name) = static_cast<float>((env)->CallFloatMethod(fx, idGetParameter, JSTR((env), #name)))

extern "C"
JNIEXPORT jbyte JNICALL
Java_com_example_midilib_AudioEngine_addEffectExternal(JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel,
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
Java_com_example_midilib_AudioEngine_clearEffects([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel) {
    FXList& fxList = getFXList(channel);
    fxList.clearEffects();
}

#undef ATTACK
#undef DECAY
#undef SUSTAIN
#undef RELEASE

#define THRESHOLD 1
#define LIMIT 2
#define ATTACK 3
#define RELEASE 4

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_editEffectExternal(JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel, jbyte i,
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

extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_allNotesOff([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel) {
    AudioEngine::allNotesOff(channel);
}

/*extern "C"
JNIEXPORT void JNICALL
Java_com_example_midilib_AudioEngine_setAssetManager(JNIEnv *env, jobject thiz, jobject mgr) {
    AssetInstrument::setAssetManager(AAssetManager_fromJava(env, mgr));
}*/

#pragma clang diagnostic pop