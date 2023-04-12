#include "WaveInstrument.h"

#ifndef LOG_H
#include "log.h"
#endif

float WaveInstrument::envelope(float time, float timeReleased) const {
    if (time > timeReleased + mRelease) { // final phase
        return NAN;
    }
    if (time > timeReleased) { // release phase
        float t = mAttack + mDecay - timeReleased;
        float ampWhenReleased = (t > 0 ? t : 0) / mDecay * (1 - mSustain) + mSustain;
        return (timeReleased + mRelease - time) / mRelease * ampWhenReleased;
    }
    if (time < mAttack) { // attack phase
        return 1.0f - (mAttack - time) / mAttack;
    }
    if (time < mAttack + mDecay) { // decay phase
        return (mAttack + mDecay - time) / mDecay * (1 - mSustain) + mSustain;
    }
    return mSustain; // sustain phase
}

float WaveInstrument::eval(float time, float frequency, float timeReleased) {
    //float env = envelope(time, timeReleased);
    return wave(time, frequency);
}

/**
 * Constructs new WaveInstrument with provided parameters for ADSR envelope generator.
 * @param attack Time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @param decay Time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @param sustain Level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @param release Time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
WaveInstrument::WaveInstrument(float attack, float decay, float sustain, float release) {
    setEnvelope(attack, decay, sustain, release);
}

/**
 * Sets all parameters for ADSR envelope generator.
 * @param attack Time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @param decay Time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @param sustain Level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @param release Time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void WaveInstrument::setEnvelope(float attack, float decay, float sustain, float release) {
    setAttack(attack);
    setDecay(decay);
    setSustain(sustain);
    setRelease(release);
}

/**
 * Sets <b>attack</b> value for ADSR envelope generator.
 * @param attack Time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void WaveInstrument::setAttack(float attack) {
    if (attack < 0) {
        throw std::invalid_argument("Attack value must not be negative");
    }
    mAttack = attack;
}
/**
 * Sets <b>decay</b> value for ADSR envelope generator.
 * @param decay Time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void WaveInstrument::setDecay(float decay) {
    if (decay < 0) {
        throw std::invalid_argument("Decay value must not be negative");
    }
    mDecay = decay;
}

/**
 * Sets <b>sustain</b> value for ADSR envelope generator.
 * @param sustain Level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void WaveInstrument::setSustain(float sustain) {
    if (sustain < 0 || sustain > 1) {
        throw std::invalid_argument("Sustain value must be in the range of 0 to 1");
    }
    mSustain = sustain;
}

/**
 * Sets <b>release</b> value for ADSR envelope generator.
 * @param release Time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void WaveInstrument::setRelease(float release) {
    if (release < 0) {
        throw std::invalid_argument("Release value must not be negative");
    }
    mRelease = release;
}
