#include "WaveSynth.h"

float WaveSynth::wave(double time, float frequency) {
    float value = 0;
    for (auto & oscillator : mOscillators) {
        value += oscillator->eval(time, frequency);
    }
    return value;
}

void WaveSynth::addOscillator(const shared_ptr<Oscillator> &oscillator) {
    mOscillators.push_back(oscillator);
}