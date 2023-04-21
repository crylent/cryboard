#ifndef DETUNE_H
#define DETUNE_H

#ifndef _LIBCPP_VECTOR
#include <vector>
#endif

using namespace std;

class Oscillator;

class Detune {
public:
    Detune(const shared_ptr<class Oscillator>& owner, uint8_t unisonVoices, float detune);

    float process(double time, float frequency);

    void setUnisonVoices(uint8_t unisonVoices);
    void setDetune(float detune);
    void setPhaseShift(uint8_t voice, float shift);
    float getPhaseShift(uint8_t voice);

    bool checkOwnership(const shared_ptr<class Oscillator>& oscillator);

private:
    weak_ptr<Oscillator> mOwner;
    uint8_t mUnisonVoices = 2;
    float mDetune = 0.005;
    vector<float> mPhases;
};


#endif //DETUNE_H
