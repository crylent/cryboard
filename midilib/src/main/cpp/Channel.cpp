#include "Channel.h"

#ifndef NOTE_FREQUENCY_H
#include "NoteFrequency.h"
#endif

WaveInstrument* Channel::mDefaultInstrument = nullptr;

Channel::Channel() {
    mInstrument = mDefaultInstrument;
    if (!mInstrument) {
        throw std::logic_error("You must call setDefaultInstrument first");
    }
}

void Channel::setInstrument(WaveInstrument *instrument) {
    mInstrument = instrument;
}

Wave *Channel::newWave(float frequency, float amplitude) {
    Wave* wave = new Wave(mInstrument, frequency, amplitude);
    return wave;
}

float Channel::nextSample() {
    float sampleValue = 0;
    for (auto & wave : mWaves) {
        sampleValue += wave.second->nextSample();
    }
    return sampleValue;
}

void Channel::noteOn(int8_t note, float amplitude) {
    Wave* wave = newWave(NoteFrequency::get(note), amplitude);
    mWaves[note] = wave;
}

void Channel::noteOff(int8_t note) {
    mWaves.erase(note);
}

void Channel::setDefaultInstrument(WaveInstrument *instrument) {
    mDefaultInstrument = instrument;
}