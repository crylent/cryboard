#ifndef WAVE_GENERATOR_H
#define WAVE_GENERATOR_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

#ifndef SOUND_GENERATOR_H
#include "SoundGenerator.h"
#endif

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

class WaveGenerator : public SoundGenerator {
public:
    WaveGenerator(WaveInstrument* instrument);

    void fillBuffer(float* buffer) override;

    void setFrequency(float frequency);
    void setAmplitude(float amplitude);

private:
    WaveInstrument* mInstrument;

    float mPhase = 0;
    float mFrequency = 440;
    float mAmplitude = 0.5;
};


#endif //WAVE_GENERATOR_H
