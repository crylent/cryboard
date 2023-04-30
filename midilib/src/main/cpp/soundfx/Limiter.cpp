#include "Limiter.h"
#include <cmath>

float Limiter::process(float sample) {
    float sampleAbs = abs(sample);
    if (sampleAbs > mThreshold) { // attack phase
        mReduction = fmin(mReduction + float(mTimeIncrement / mAttack) * (sampleAbs / mThreshold - 1), sampleAbs / mThreshold);
        mPeak = sampleAbs / mReduction;
    } else if (mPeak > mThreshold) { // release phase
        mReduction = fmax(mReduction - float(mTimeIncrement / mRelease) * (mPeak - mThreshold), 1.0f);
    }
    return sample / mReduction * mGain;
}

/**
 * Constructs new Limiter FX with
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Threshold">threshold</a> = 0.7 (-3 dB),
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Ceiling">limit (ceiling)</a> = 0.99 (-1 dB),
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Attack">attack</a> = 0 ms
 * and <a href="https://www.meldaproduction.com/tutorials/text/limiters#Release">release</a> = 50 ms.
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
Limiter::Limiter() = default;

/**
 * Constructs new Limiter FX with provided
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Threshold">threshold</a>,
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Ceiling">limit (ceiling)</a>
 * and <a href="https://www.meldaproduction.com/tutorials/text/limiters#Release">release</a> values
 * and <a href="https://www.meldaproduction.com/tutorials/text/limiters#Attack">attack</a> = 0 ms.
 * @param threshold the level (from 0.0 <i>non-inclusive</i> to 1.0) at which the limiter applies brick wall compression
 * @param limit the level (from 0.0 <i>non-inclusive</i> to 1.0) at which the limiter applies brick wall compression
 * @param release the amount of time (in seconds) it takes for the limiter to stop after the signal falls below the threshold, default is 50 ms
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
Limiter::Limiter(float threshold, float limit, double release) {
    setThreshold(threshold);
    setLimit(limit);
    setRelease(release);
}

/**
 * Constructs new Limiter FX with provided
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Threshold">threshold</a>,
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Ceiling">limit (ceiling)</a>,
 * <a href="https://www.meldaproduction.com/tutorials/text/limiters#Attack">attack</a>
 * and <a href="https://www.meldaproduction.com/tutorials/text/limiters#Release">release</a>.
 * @param threshold the level (from 0.0 <i>non-inclusive</i> to 1.0) at which the limiter applies brick wall compression
 * @param limit the level (from 0.0 <i>non-inclusive</i> to 1.0) at which the limiter applies brick wall compression
 * @param attack the amount of time (in seconds) it takes for the limiter to return below the threshold
 * @param release the amount of time (in seconds) it takes for the limiter to stop after the signal falls below the threshold
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
Limiter::Limiter(float threshold, float limit, double attack, double release) {
    setThreshold(threshold);
    setLimit(limit);
    setAttack(attack);
    setRelease(release);
}

/**
 * Sets <a href="https://www.meldaproduction.com/tutorials/text/limiters#Threshold">threshold</a> value for limiter.
 * @param threshold the level (from 0.0 <i>non-inclusive</i> to 1.0) at which the limiter applies brick wall compression
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
void Limiter::setThreshold(float threshold) {
    if (threshold <= 0 || threshold > 1) {
        throw invalid_argument("Threshold level must be in the range of 0.0 (non-inclusive) to 1.0");
    }
    mThreshold = threshold;
    mGain = mLimit / threshold;
}

/**
 * Sets <a href="https://www.meldaproduction.com/tutorials/text/limiters#Ceiling">limit (ceiling)</a> value for limiter.
 * @param limit the maximum level (from 0.0 <i>non-inclusive</i> to 1.0) the limiter will output
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
void Limiter::setLimit(float limit) {
    if (limit <= 0 || limit > 1) {
        throw invalid_argument("Limit level must be in the range of 0.0 (non-inclusive) to 1.0");
    }
    mLimit = limit;
    mGain = limit / mThreshold;
}

/**
 * Sets <a href="https://www.meldaproduction.com/tutorials/text/limiters#Attack">attack</a> time for limiter.
 * @param attack the amount of time (in seconds) it takes for the limiter to return below the threshold
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
void Limiter::setAttack(double attack) {
    if (attack < 0) {
        throw invalid_argument("Attack time must be positive number");
    }
    mAttack = attack;
}

/**
 * Sets <a href="https://www.meldaproduction.com/tutorials/text/limiters#Release">release</a> time for limiter.
 * @param release the amount of time (in seconds) it takes for the limiter to stop after the signal falls below the threshold
 * @see <a href="https://www.masteringbox.com/audio-limiter/">Audio Limiter</a>
 * @see <a href="https://www.meldaproduction.com/tutorials/text/limiters">Limiters Tutorial</a>
 * @see <a href="https://www.izotope.com/en/learn/an-introduction-to-limiters-and-how-to-use-them.html">How to Use Limiters</a>
 */
void Limiter::setRelease(double release) {
    if (release < 0) {
        throw invalid_argument("Release time must be positive number");
    }
    mRelease = release;
}
