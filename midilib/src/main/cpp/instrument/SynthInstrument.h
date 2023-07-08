#ifndef WAVE_SYNTH_H
#define WAVE_SYNTH_H

#include "Instrument.h"
#include "../oscillators/Oscillator.h"

using namespace std;

class SynthInstrument : public Instrument {
public:
    using Instrument::Instrument;

    void addOscillator(unique_ptr<Oscillator> oscillator);
    Oscillator& getOscillatorByIndex(uint8_t index);
    void enableOscillator(uint8_t index);
    void disableOscillator(uint8_t index);
    void removeOscillator(uint8_t index);

    template<typename Shape>
    requires(is_base_of<Oscillator, Shape>()) void setOscillatorShape(uint8_t index) {
        mOscillators[index] = make_unique<Shape>(*mOscillators[index].release());
    }

    float sample(double time, int8_t note) override;

private:
    vector<unique_ptr<Oscillator>> mOscillators = vector<unique_ptr<Oscillator>>();
};

#endif //WAVE_SYNTH_H
