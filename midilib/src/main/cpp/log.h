//
// Created by Somnie on 07.04.2023.
//

#ifndef LOG_H
#define LOG_H
#include <android/log.h>

#ifndef MODULE_NAME
#define MODULE_NAME "MidiLib"
#endif

#ifndef __SAMPLE_ANDROID_DEBUG_H__
#define __SAMPLE_ANDROID_DEBUG_H__
#include <android/log.h>
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, MODULE_NAME, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, MODULE_NAME, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, MODULE_NAME, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,MODULE_NAME, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,MODULE_NAME, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,MODULE_NAME, __VA_ARGS__)
#define ASSERT(cond, ...) if (!(cond)) {__android_log_assert(#cond, MODULE_NAME, __VA_ARGS__);}
#endif

#endif //LOG_H
