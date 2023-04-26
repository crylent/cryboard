#ifndef LIMITER_H
#define LIMITER_H

#include "SoundFX.h"

class Limiter: public SoundFX {
public:
    Limiter();
    Limiter(float threshold, float limit, double release = 0.05);
    Limiter(float threshold, float limit, double attack, double release);

    void setThreshold(float threshold);
    void setLimit(float limit);
    void setAttack(double attack);
    void setRelease(double release);

    float process(float sample) override;

private:
    float mThreshold = 0.7; // from 0.0 to 1.0, 0.7 ~= -3 dB
    float mLimit = 0.99; // from 0.0 to 1.0, 0.99 ~= -1 dB
    double mAttack = 0; // seconds
    double mRelease = 0.05; // seconds

    float mGain = mLimit / mThreshold;

    float mPeak = 0;
    float mReduction = 1;
};


#endif //LIMITER_H
