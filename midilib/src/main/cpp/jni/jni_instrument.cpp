#include "jni.h"
#include "jni_instrument_functions.cpp"
#include "../instrument/InstrumentLib.h"
#include "../instrument/SynthInstrument.h"
#include "../instrument/AssetInstrument.h"
#include "../oscillators/SineOscillator.h"
#include "../oscillators/TriangleOscillator.h"
#include "../oscillators/SquareOscillator.h"
#include "../oscillators/SawtoothOscillator.h"
#include "../oscillators/ReverseSawtoothOscillator.h"

#define GET_SYNTH(index) (dynamic_cast<SynthInstrument&>(*InstrumentLib::getInstrument(index)))

#define SYNTH_INSTRUMENT 0
#define ASSET_INSTRUMENT 1

extern "C"
JNIEXPORT jint JNICALL
Java_com_crylent_midilib_instrument_Instrument_externalCreate(JNIEnv *env, jobject thiz) {
    jclass instCls = env->GetObjectClass(thiz);
    jclass synthInstCls = env->FindClass("com/crylent/midilib/instrument/SynthInstrument");
    jclass assetInstCls = env->FindClass("com/crylent/midilib/instrument/AssetInstrument");
    uint8_t instType;
    if (env->IsInstanceOf(thiz, synthInstCls)) instType = SYNTH_INSTRUMENT;
    else instType = ASSET_INSTRUMENT;

#define GET_FIELD(field) jfieldID id_##field = env->GetFieldID(instCls, #field, "F"); \
jfloat field = env->GetFloatField(thiz, id_##field)

    GET_FIELD(attack);
    GET_FIELD(decay);
    GET_FIELD(sustain);
    GET_FIELD(release);
    GET_FIELD(attackSharpness);
    GET_FIELD(decaySharpness);
    GET_FIELD(releaseSharpness);

#undef GET_FIELD

    uint32_t position;

    if (instType == SYNTH_INSTRUMENT) {
        jmethodID idAsSynthInst = env->GetMethodID(instCls, "asSynthInstrument",
                                                   "()Lcom/crylent/midilib/instrument/SynthInstrument;");
        thiz = env->CallObjectMethod(thiz, idAsSynthInst);
        auto inst = make_shared<SynthInstrument>(
                attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness);
        jmethodID idOscCount = env->GetMethodID(synthInstCls, "getOscCount", "()I");
        jint oscillatorsCount = env->CallIntMethod(thiz, idOscCount);

        jmethodID idGetOscillator = env->GetMethodID(synthInstCls, "getOscillator",
                                                     "(I)Lcom/crylent/midilib/Oscillator;");

        for (int i = 0; i < oscillatorsCount; i++) {
            jobject oscillator = env->CallObjectMethod(thiz, idGetOscillator, i);
            addOscillator(env, *inst, oscillator);
        }
        position = InstrumentLib::addInstrument(inst);
    } else {
        jmethodID idAsAssetInst = env->GetMethodID(instCls, "asAssetInstrument",
                                                   "()Lcom/crylent/midilib/instrument/AssetInstrument;");
        thiz = env->CallObjectMethod(thiz, idAsAssetInst);
        auto inst = make_shared<AssetInstrument>(
                attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness);
        jfieldID idRepeatAssets = env->GetFieldID(assetInstCls, "repeatAssets", "Z");
        bool repeatable = env->GetBooleanField(thiz, idRepeatAssets);
        inst->setRepeatable(repeatable);
        position = InstrumentLib::addInstrument(inst);
    }
    return (jint) position;
}

#define NO_INDEX (-1)

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_SynthInstrument_externalAddOscillator(JNIEnv *env, jobject thiz,
                                                                          jobject oscillator) {
    int32_t index = getLibIndex(env, thiz);
    auto& inst = GET_SYNTH(index);
    addOscillator(env, inst, oscillator);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_SynthInstrument_externalRemoveOscillator(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jint index) {
    int32_t instIndex = getLibIndex(env, thiz);
    auto& inst = GET_SYNTH(instIndex);
    inst.removeOscillator(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_SynthInstrument_externalEnableOscillator(JNIEnv *env, jobject thiz,
                                                                             jint index) {
    int32_t instIndex = getLibIndex(env, thiz);
    auto& inst = GET_SYNTH(instIndex);
    inst.enableOscillator(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_SynthInstrument_externalDisableOscillator(JNIEnv *env, jobject thiz,
                                                                              jint index) {
    int32_t instIndex = getLibIndex(env, thiz);
    auto& inst = GET_SYNTH(instIndex);
    inst.disableOscillator(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_Instrument_externalAssignToChannel(JNIEnv *env, jobject thiz,
                                                                       jbyte channel) {
    AudioEngine::getChannels()[channel]->setInstrument(
            InstrumentLib::getInstrument(getLibIndex(env, thiz))
            );
}

#define PARAM_SETTER(param) extern "C" JNIEXPORT void JNICALL \
Java_com_crylent_midilib_instrument_Instrument_externalSet##param \
(JNIEnv *env, jobject thiz, jfloat value) {\
    int32_t index = getLibIndex(env, thiz);\
    if (index != NO_INDEX) {\
        InstrumentLib::getInstrument(index)->set##param(value);\
    }\
}

PARAM_SETTER(Attack)
PARAM_SETTER(Decay)
PARAM_SETTER(Sustain)
PARAM_SETTER(Release)
PARAM_SETTER(AttackSharpness)
PARAM_SETTER(DecaySharpness)
PARAM_SETTER(ReleaseSharpness)

#undef PARAM_SETTER

#define OSC_FUNCTION_BEGIN(env, obj) \
int32_t instIndex = getOwnerLibIndex(env, obj); \
int32_t oscIndex = getOscIndex(env, obj); \
auto& inst = GET_SYNTH(instIndex)

#define OSC_PARAM_SETTER(param) extern "C" JNIEXPORT void JNICALL \
Java_com_crylent_midilib_Oscillator_externalSet##param \
(JNIEnv *env, jobject thiz, jfloat value) { \
    OSC_FUNCTION_BEGIN(env, thiz); \
    if (instIndex != NO_INDEX && oscIndex != NO_INDEX) inst.getOscillatorByIndex(oscIndex).set##param(value); \
}

OSC_PARAM_SETTER(Amplitude)
OSC_PARAM_SETTER(Phase)
OSC_PARAM_SETTER(FreqFactor)

#undef OSC_PARAM_SETTER

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalSetShape(JNIEnv *env, jobject thiz, jint shape) {
    OSC_FUNCTION_BEGIN(env, thiz);
    switch (shape) {
        case SHAPE_SINE:
            inst.setOscillatorShape<SineOscillator>(oscIndex); break;
        case SHAPE_TRIANGLE:
            inst.setOscillatorShape<TriangleOscillator>(oscIndex); break;
        case SHAPE_SQUARE:
            inst.setOscillatorShape<SquareOscillator>(oscIndex); break;
        case SHAPE_SAW:
            inst.setOscillatorShape<SawtoothOscillator>(oscIndex); break;
        case SHAPE_REVERSE_SAW:
            inst.setOscillatorShape<ReverseSawtoothOscillator>(oscIndex); break;
        default:
            break;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalEnableDetune(JNIEnv *env, jobject thiz,
                                                         jint unison_voices, jfloat detune_level) {
    OSC_FUNCTION_BEGIN(env, thiz);
    inst.getOscillatorByIndex(oscIndex).setDetune(unison_voices, detune_level);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalDisableDetune(JNIEnv *env, jobject thiz) {
    OSC_FUNCTION_BEGIN(env, thiz);
    inst.getOscillatorByIndex(oscIndex).clearDetune();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalSetUnisonVoices(JNIEnv *env, jobject thiz,
                                                            jint value) {
    OSC_FUNCTION_BEGIN(env, thiz);
    inst.getOscillatorByIndex(oscIndex).getDetune().setUnisonVoices(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalSetDetuneLevel(JNIEnv *env, jobject thiz,
                                                           jfloat value) {
    OSC_FUNCTION_BEGIN(env, thiz);
    inst.getOscillatorByIndex(oscIndex).getDetune().setDetune(value);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_Oscillator_externalSetPhaseShift(JNIEnv *env, jobject thiz, jint voice,
                                                          jfloat value) {
    OSC_FUNCTION_BEGIN(env, thiz);
    inst.getOscillatorByIndex(oscIndex).getDetune().setPhaseShift(voice, value);
}

#undef OSC_FUNCTION_BEGIN
#undef GET_SYNTH
#undef SYNTH_INSTRUMENT

#define GET_AINST(index) (dynamic_cast<AssetInstrument&>(*InstrumentLib::getInstrument(index)))

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_AssetInstrument_externalLoadAsset(JNIEnv *env, jobject thiz,
                                                                      jbyte note,
                                                                      jbyteArray wav_data,
                                                                      jint data_size,
                                                                      jboolean is_base_asset) {
    int32_t index = getLibIndex(env, thiz);
    auto array = env->GetByteArrayElements(wav_data, nullptr);
    auto data = make_unique<vector<uint8_t>>(array, array + data_size);
    auto& inst = GET_AINST(index);
    if (is_base_asset) inst.loadBaseAsset(note, move(data));
    else inst.loadAsset(note, move(data));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_AssetInstrument_externalSetRepeatable(JNIEnv *env, jobject thiz,
                                                                          jboolean repeatable) {
    int32_t index = getLibIndex(env, thiz);
    GET_AINST(index).setRepeatable(repeatable);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_instrument_AssetInstrument_00024Companion_externalSetResamplingQuality(
        [[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jint quality) {
    AssetInstrument::setResamplingQuality(quality);
}

#undef GET_AINST
#undef ASSET_INSTRUMENT
#undef NO_INDEX