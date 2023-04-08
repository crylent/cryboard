#ifndef WAVE_PIANO_H
#define WAVE_PIANO_H

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

class WavePiano: public WaveInstrument {
public:
    WavePiano(uint8_t overtones, float saturation, float damping);
    WavePiano(uint8_t overtones, float saturation);
    WavePiano();

    void setOvertones(uint8_t overtones);
    void setSaturation(float saturation);

    float wave(float phase) override;

protected:
    uint8_t mOvertones = 0;
    float mSaturation = 0;
};


#endif //WAVE_PIANO_H
