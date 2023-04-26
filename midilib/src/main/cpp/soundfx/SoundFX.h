#ifndef SOUNDFX_H
#define SOUNDFX_H

#include <stdexcept>

using namespace std;

class SoundFX {
public:
    SoundFX();
    virtual ~SoundFX() = default;

    virtual float process(float sample) = 0;

protected:
    double mTimeIncrement;
};


#endif //SOUNDFX_H
