#include "Detune.h"
#include "Oscillator.h"

/**
 * Constructs new Detune object.
 * @param owner reference to oscillator object
 * @param unisonVoices number of unison voices, 2 or more
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 */
Detune::Detune(Oscillator& owner, uint8_t unisonVoices, float detune) : mOwner(owner) {
    mPhases = vector<float>(mUnisonVoices);
    setUnisonVoices(unisonVoices);
    setDetune(detune);
}

/**
 * Constructs new Detune object from other
 * @param owner reference to oscillator object
 * @param other other detune object
 */
Detune::Detune(Oscillator &owner, Detune &other) : mOwner(owner) {
    mUnisonVoices = other.mUnisonVoices;
    mDetune = other.mDetune;
    mPhases = other.mPhases;
}

float Detune::process(double time, float frequency) {
    float minFrequency = frequency * (1 - mDetune);
    float delta = frequency * (2 * mDetune) / float(mUnisonVoices - 1);
    float sum = 0;
    for (uint8_t i = 0; i < mUnisonVoices; i++) {
        sum += mOwner.evalVoice(time, minFrequency + float(i) * delta);
    }
    return sum / float(mUnisonVoices);
}

/**
 * Sets number of unison voices for detune.
 * @param unisonVoices number of unison voices, 2 or more
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
Detune& Detune::setUnisonVoices(uint8_t unisonVoices) {
    if (unisonVoices < 2) {
        throw invalid_argument("You should set 2 or more unison voices");
    }
    mUnisonVoices = unisonVoices;
    mPhases.reserve(unisonVoices);
    while (mPhases.size() < mUnisonVoices) {
        mPhases.push_back(0);
    }
    return *this;
}

/**
 * Sets detune level.
 * @param detune maximum divergence from the original frequency (from 0.0 <i>non-inclusive</i> to 1.0 <i>non-inclusive</i>)
 * @see <a href="https://samplechilli.com/what-is-synth-detune-and-why-use-it/">Detune</a>
 */
Detune& Detune::setDetune(float detune) {
    if (detune <= 0 || detune >= 1) {
        throw invalid_argument("Detune must be in the range from 0.0 non-inclusive to 1.0 non-inclusive");
    }
    mDetune = detune;
    return *this;
}

/**
 * Sets phase shift for voice with the given number. Phase shift does not affect sound much, but can make it smoother or shift peaks.
 * @param voice number of voice, from 0 to (unisonVoices - 1)
 * @param shift radians, either in the range from 0 to 2π, or from -π to π
 */
Detune& Detune::setPhaseShift(uint8_t voice, float shift) {
    mPhases[voice] = shift;
    return *this;
}

/**
 * @param voice number of voice, from 0 to (unisonVoices - 1)
 * @return phase shift in radians
 */
float Detune::getPhaseShift(uint8_t voice) {
    return mPhases[voice];
}