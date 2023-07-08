#ifndef DETUNE_H
#define DETUNE_H

#include <vector>

using namespace std;

class Oscillator;

class Detune {
public:
    Detune(Oscillator& owner) : mOwner(owner) {};
    Detune(Oscillator& owner, uint8_t unisonVoices, float detune);
    Detune(Oscillator& owner, Detune& other);

    float process(double time, float frequency);

    Detune& setUnisonVoices(uint8_t unisonVoices);
    Detune& setDetune(float detune);
    Detune& setPhaseShift(uint8_t voice, float shift);
    float getPhaseShift(uint8_t voice);

private:
    Oscillator& mOwner;
    uint8_t mUnisonVoices = 2;
    float mDetune = 0.005;
    vector<float> mPhases;
};


#endif //DETUNE_H
