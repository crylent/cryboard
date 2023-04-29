#include "jni.h"
#include "../instrument/SynthInstrument.h"
#include "../oscillators/SineOscillator.h"
#include "../oscillators/TriangleOscillator.h"
#include "../oscillators/SquareOscillator.h"
#include "../oscillators/SawtoothOscillator.h"
#include "../oscillators/ReverseSawtoothOscillator.h"

#define SHAPE_SINE 0
#define SHAPE_TRIANGLE 1
#define SHAPE_SQUARE 2
#define SHAPE_SAW 3
#define SHAPE_REVERSE_SAW 4

static unique_ptr<Oscillator> makeOscillator(int shapeOrdinal, float amplitude, float phase, float freqFactor) {
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

static void addOscillator(JNIEnv *env, SynthInstrument& inst, jobject oscillator) {
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

static int32_t getLibIndex(JNIEnv *env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID idLibIndex = env->GetFieldID(cls, "libIndex", "I");
    return env->GetIntField(thiz, idLibIndex);
}

static int32_t getOscIndex(JNIEnv *env, jobject osc, jclass cls) {
    jmethodID idOscIndex = env->GetMethodID(cls, "getOscIndex", "()I");
    return env->CallIntMethod(osc, idOscIndex);
}