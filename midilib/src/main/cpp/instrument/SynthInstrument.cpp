#include "SynthInstrument.h"
#include "../NoteFrequency.h"

float SynthInstrument::sample(double time, int8_t note) {
    float value = 0;
    float frequency = NoteFrequency::get(note);
    for (auto & oscillator : mOscillators) {
        value += oscillator->eval(time, frequency);
    }
    return value;
}

void SynthInstrument::addOscillator(unique_ptr<Oscillator> oscillator) {
    mOscillators.push_back(move(oscillator));
}

Oscillator& SynthInstrument::getOscillatorByIndex(uint8_t index) {
    return *mOscillators[index];
}

void SynthInstrument::enableOscillator(uint8_t index) {
    mOscillators[index]->enable();
}

void SynthInstrument::disableOscillator(uint8_t index) {
    mOscillators[index]->disable();
}

void SynthInstrument::removeOscillator(uint8_t index) {
    mOscillators.erase(mOscillators.begin() + index);
}