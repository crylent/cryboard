#include "WaveGenerator.h"

WaveGenerator::WaveGenerator(WaveInstrument* instrument) {
    mInstrument = instrument;
}

void WaveGenerator::fillBuffer(float *buffer) {
    auto phaseIncrement = float(mFrequency * 2 * M_PI / (double) mSampleRate);
    for (int i = 0; i < mBufferSize; i++) {
        float sampleValue = mAmplitude * mInstrument->calc(mPhase);
        buffer[i] = sampleValue;
        mPhase += phaseIncrement;
    }
}

void WaveGenerator::setAmplitude(float amplitude) {
    mAmplitude = amplitude;
}

void WaveGenerator::setFrequency(float frequency) {
    if (frequency <= 0) {
        throw std::invalid_argument("Invalid frequency");
    }
    mFrequency = frequency;
}