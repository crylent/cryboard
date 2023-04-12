#include "WaveSynth.h"

float WaveSynth::wave(float phase) {
    float value = 0;
    for (auto & oscillator : mOscillators) {
        value += oscillator->eval(phase);
    }
    return value;
}

void WaveSynth::addOscillator(const shared_ptr<Oscillator> &oscillator) {
    mOscillators.push_back(oscillator);
}