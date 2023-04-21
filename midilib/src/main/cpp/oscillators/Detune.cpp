#include "Detune.h"

#ifndef OSCILLATOR_H
#include "Oscillator.h"
#endif

/**
 * Constructs new Detune object.
 * @param oscillator shared pointer to oscillator object
 * @param unisonVoices number of unison voices, 2 or more
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 */
Detune::Detune(const shared_ptr<class Oscillator>& owner, uint8_t unisonVoices, float detune) {
    mOwner = weak_ptr<Oscillator>(owner);
    mPhases = vector<float>(mUnisonVoices);
    setUnisonVoices(unisonVoices);
    setDetune(detune);
}

float Detune::process(double time, float frequency) {
    float minFrequency = frequency * (1 - mDetune);
    float delta = frequency * (2 * mDetune) / float(mUnisonVoices - 1);
    float sum = 0;
    for (uint8_t i = 0; i < mUnisonVoices; i++) {
        sum += mOwner.lock()->evalVoice(time, minFrequency + float(i) * delta);
    }
    return sum / float(mUnisonVoices);
}

/**
 * Sets number of unison voices for detune.
 * @param unisonVoices number of unison voices, 2 or more
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
void Detune::setUnisonVoices(uint8_t unisonVoices) {
    if (unisonVoices < 2) {
        throw invalid_argument("You should set 2 or more unison voices");
    }
    mUnisonVoices = unisonVoices;
    while (mPhases.size() < mUnisonVoices) {
        mPhases.push_back(0);
    }
}

/**
 * Sets detune level.
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
void Detune::setDetune(float detune) {
    if (detune <= 0 || detune >= 1) {
        throw invalid_argument("Detune must be in the range from 0.0 non-inclusive to 1.0 non-inclusive");
    }
    mDetune = detune;
}

/**
 * Sets phase shift for voice with the given number.
 * @param voice number of voice, from 0 to (unisonVoices - 1)
 * @param shift radians, either in the range from 0 to 2π, or from -π to π
 */
void Detune::setPhaseShift(uint8_t voice, float shift) {
    mPhases[voice] = shift;
}

float Detune::getPhaseShift(uint8_t voice) {
    return mPhases[voice];
}

bool Detune::checkOwnership(const shared_ptr<class Oscillator>& oscillator) {
    return mOwner.lock() == oscillator;
}
