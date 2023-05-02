#include "Instrument.h"

float Instrument::envelope(double time, double timeReleased) const {
    if (time > timeReleased + mRelease) { // final phase
        return NAN;
    }
    if (time > timeReleased) { // release phase
        float ampWhenReleased;
        if (timeReleased < mAttack) { // released in attack phase
            ampWhenReleased = pow(float(timeReleased) / mAttack, 1.0f / mAttackSharpness);
        } else if (time < mAttack + mDecay) { // released in decay phase
            ampWhenReleased = pow((mAttack + mDecay - (float) timeReleased) / mDecay, mDecaySharpness) * (1 - mSustain) + mSustain;
        } else { // released in sustain phase
            ampWhenReleased = mSustain;
        }
        return pow(((float) timeReleased + mRelease - (float) time) / mRelease, mReleaseSharpness) * ampWhenReleased;
    }
    if (time < mAttack) { // attack phase
        return pow(float(time) / mAttack, 1.0f / mAttackSharpness);
    }
    if (time < mAttack + mDecay) { // decay phase
        return pow((mAttack + mDecay - (float) time) / mDecay, mDecaySharpness) * (1 - mSustain) + mSustain;
    }
    return mSustain; // sustain phase
}

float Instrument::eval(double time, int8_t note, double timeReleased) {
    float env = envelope(time, timeReleased);
    return sample(time, note) * env;
}

/**
 * Constructs new Instrument with provided parameters for ADSR envelope generator.
 * @param attack time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @param decay time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @param sustain level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @param release time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
Instrument::Instrument(float attack, float decay, float sustain, float release) {
    setEnvelope(attack, decay, sustain, release);
}

/**
 * Constructs new Instrument with provided parameters for ADSR envelope generator.
 * @param attack time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @param decay time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @param sustain level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @param release time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @param attackSharpness attack sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 * @param decaySharpness decay sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 * @param releaseSharpness release sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
Instrument::Instrument(float attack, float decay, float sustain, float release,
                       float attackSharpness, float decaySharpness, float releaseSharpness) {
    setEnvelope(attack, decay, sustain, release);
    setEnvelopeSharpness(attackSharpness, decaySharpness, releaseSharpness);
}


/**
 * Sets all parameters for ADSR envelope generator.
 * @param attack time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @param decay time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @param sustain level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @param release time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void Instrument::setEnvelope(float attack, float decay, float sustain, float release) {
    setAttack(attack);
    setDecay(decay);
    setSustain(sustain);
    setRelease(release);
}

/**
 * Sets <b>attack</b> value for ADSR envelope generator.
 * @param attack time (in seconds) taken for initial run-up of level from nil to peak, beginning when the key is pressed
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void Instrument::setAttack(float attack) {
    if (attack < 0) {
        throw std::invalid_argument("Attack value must not be negative");
    }
    mAttack = attack;
}
/**
 * Sets <b>decay</b> value for ADSR envelope generator.
 * @param decay time (in seconds) taken for the subsequent run down from the attack level to the designated sustain level
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void Instrument::setDecay(float decay) {
    if (decay < 0) {
        throw std::invalid_argument("Decay value must not be negative");
    }
    mDecay = decay;
}

/**
 * Sets <b>sustain</b> value for ADSR envelope generator.
 * @param sustain level (from 0.0 to 1.0) during the main sequence of the sound's duration, until the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void Instrument::setSustain(float sustain) {
    if (sustain < 0 || sustain > 1) {
        throw std::invalid_argument("Sustain value must be in the range of 0.0 to 1.0");
    }
    mSustain = sustain;
}

/**
 * Sets <b>release</b> value for ADSR envelope generator.
 * @param release time (in seconds) taken for the level to decay from the sustain level to zero after the key is released
 * @see <a href="https://en.wikipedia.org/wiki/Envelope_(music)">Envelope</a>
 */
void Instrument::setRelease(float release) {
    if (release < 0) {
        throw std::invalid_argument("Release value must not be negative");
    }
    mRelease = release;
}

/**
 * Sets sharpness parameters for ADSR envelope generator.
 * @param attack attack sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 * @param decay decay sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 * @param release release sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 */
void Instrument::setEnvelopeSharpness(float attack, float decay, float release) {
    setAttackSharpness(attack);
    setDecaySharpness(decay);
    setReleaseSharpness(release);
}

/**
 * Sets attack sharpness for ADSR envelope generator.
 * @param sharpness attack sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 */
void Instrument::setAttackSharpness(float sharpness) {
    sharpnessCheck(sharpness);
    mAttackSharpness = sharpness;
}

/**
 * Sets decay sharpness for ADSR envelope generator.
 * @param sharpness decay sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 */
void Instrument::setDecaySharpness(float sharpness) {
    sharpnessCheck(sharpness);
    mDecaySharpness = sharpness;
}

/**
 * Sets release sharpness for ADSR envelope generator.
 * @param sharpness release sharpness, 1 is linear, >1 for faster start, \<1 for slower start
 */
void Instrument::setReleaseSharpness(float sharpness) {
    sharpnessCheck(sharpness);
    mReleaseSharpness = sharpness;
}

void Instrument::sharpnessCheck(float sharpness) {
    if (sharpness <= 0) {
        throw std::invalid_argument("Sharpness value must be positive");
    }
}