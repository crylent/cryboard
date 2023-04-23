#ifndef SOUNDFX_H
#define SOUNDFX_H

#ifndef _LIBCPP_STDEXCEPT
#include <stdexcept>
#endif

class SoundFX {
public:
    SoundFX();
    virtual ~SoundFX() = default;

    virtual float process(float sample) = 0;

protected:
    double mTimeIncrement;
};


#endif //SOUNDFX_H
