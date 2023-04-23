#ifndef WAVE_SYNTH_H
#define WAVE_SYNTH_H

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

#ifndef OSCILLATOR_H
#include "../oscillators/Oscillator.h"
#endif

#ifndef _LIBCPP_VECTOR
#include <vector>
#endif

using namespace std;

class WaveSynth: public WaveInstrument {
public:
    using WaveInstrument::WaveInstrument;

    void addOscillator(unique_ptr<Oscillator> oscillator);
    Oscillator& getOscillatorByIndex(uint8_t index);
    void enableOscillator(uint8_t index);
    void disableOscillator(uint8_t index);

    float wave(double time, float frequency) override;

private:
    vector<unique_ptr<Oscillator>> mOscillators = vector<unique_ptr<Oscillator>>();
};



#endif //WAVE_SYNTH_H
