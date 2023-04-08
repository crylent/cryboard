#ifndef MULTIWAVE_GENERATOR_H
#define MULTIWAVE_GENERATOR_H

#ifndef OBOE_H

#include <vector>
#include "oboe/Oboe.h"
#endif

#ifndef SOUND_GENERATOR_H
#include "SoundGenerator.h"
#endif

#ifndef WAVE_INSTRUMENT_H
#include "WaveInstrument.h"
#endif

#ifndef WAVE_H
#include "Wave.h"
#endif

class MultiwaveGenerator : public SoundGenerator {
public:
    void fillBuffer(float* buffer) override;
};


#endif //MULTIWAVE_GENERATOR_H
