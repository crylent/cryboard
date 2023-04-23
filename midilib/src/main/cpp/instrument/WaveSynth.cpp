#include "WaveSynth.h"

float WaveSynth::wave(double time, float frequency) {
    float value = 0;
    for (auto & oscillator : mOscillators) {
        value += oscillator->eval(time, frequency);
    }
    return value;
}

void WaveSynth::addOscillator(unique_ptr<Oscillator> oscillator) {
    mOscillators.push_back(move(oscillator));
}

Oscillator& WaveSynth::getOscillatorByIndex(uint8_t index) {
    return *mOscillators[index];
}

void WaveSynth::enableOscillator(uint8_t index) {
    mOscillators[index]->enable();
}

void WaveSynth::disableOscillator(uint8_t index) {
    mOscillators[index]->disable();
}
