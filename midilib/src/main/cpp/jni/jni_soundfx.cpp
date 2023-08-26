#include "jni.h"
#include "jni_soundfx_functions.cpp"
#include "../soundfx/Limiter.h"
#include <map>

#define MASTER_CHANNEL -1

#define LIMITER 1

#define GET_PARAM(env, name) auto (name) = static_cast<float>((env)->CallFloatMethod(thiz, idGetParameter, JSTR((env), #name)))

extern "C"
JNIEXPORT jbyte JNICALL
Java_com_crylent_midilib_soundfx_SoundFX_externalAssignToChannel(JNIEnv *env, jobject thiz,
                                                                 jbyte channel) {
    jclass fxCls = env->GetObjectClass(thiz);
    jmethodID idGetId = env->GetMethodID(fxCls, "getId", "()I");
    auto fxId = static_cast<int>(env->CallIntMethod(thiz, idGetId));
    jmethodID idGetParameter = env->GetMethodID(fxCls, "getParameter",
                                                "(Ljava/lang/String;)F");

    unique_ptr<SoundFX> effect;
    switch (fxId) { // NOLINT(hicpp-multiway-paths-covered)
        case LIMITER: {
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
    if (channel == MASTER_CHANNEL) {
        FXList& masterFx = AudioEngine::getMasterFX();
        i = masterFx.addEffect(move(effect));
    } else {
        throw exception(); // Not implemented
    }
    return static_cast<jbyte>(i);
}

#undef GET_PARAM

#define THRESHOLD 1
#define LIMIT 2
#define ATTACK 3
#define RELEASE 4

extern "C"
JNIEXPORT void JNICALL
Java_com_crylent_midilib_soundfx_SoundFX_externalEditEffect(JNIEnv *env, jobject thiz,
                                                            jstring param, jfloat value) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID idChannel = env->GetFieldID(cls, "linkedChannel", "B");
    jfieldID idIndex = env->GetFieldID(cls, "fxIndex", "B");
    jbyte channel = env->GetByteField(thiz, idChannel);
    jbyte i = env->GetByteField(thiz, idIndex);

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
Java_com_crylent_midilib_AudioEngine_clearEffects([[maybe_unused]] JNIEnv *env, [[maybe_unused]] jobject thiz, jbyte channel) {
    FXList& fxList = getFXList(channel);
    fxList.clearEffects();
}

#undef LIMITER