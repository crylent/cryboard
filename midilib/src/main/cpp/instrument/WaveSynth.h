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
    WaveSynth() {};

    void addOscillator(const shared_ptr<Oscillator>& oscillator);

    float wave(double time, float frequency) override;

private:
    vector<shared_ptr<Oscillator>> mOscillators = vector<shared_ptr<Oscillator>>();
};



#endif //WAVE_SYNTH_H
