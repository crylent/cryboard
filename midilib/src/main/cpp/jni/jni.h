#ifndef JNI_H
#define JNI_H
#include <jni.h>
#include "../AudioEngine.h"

#define JSTR(env, string) env->NewStringUTF(string)

#endif //JNI_H
