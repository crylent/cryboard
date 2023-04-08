#include "AudioEngine.h"

#ifndef LOG_H
#include "log.h"
#endif

#include "WavePiano.h"

std::shared_ptr<oboe::AudioStream> AudioEngine::mStream;
std::mutex AudioEngine::mLock;
SharingMode AudioEngine::mSharingMode = SharingMode::Exclusive;
int32_t AudioEngine::mSampleRate = 48000;
int32_t AudioEngine::mBufferSize = 256;
std::vector<Channel*> AudioEngine::mChannels = std::vector<Channel*>();

/**
 * Starts audio engine with specified configuration.
 * @param sharingMode <a href="https://bit.ly/3KIM1fB"><i>Exclusive</i> or <i>Shared</i></a>
 * @param sampleRate The most common <a href="https://bit.ly/3nVpoM6">sample rates</a> are 44100 and 48000.
 * @return <code>Result::OK</code> if started successfully, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::start(SharingMode sharingMode, int32_t sampleRate) {
    mSharingMode = sharingMode;
    mSampleRate = sampleRate;
    return start();
}

/**
 * Starts audio engine in <b>exclusive</b> sharing mode and with <b>48000</b> sample rate.
 * @return <code>Result::OK</code> if started successfully, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::start() {
    std::lock_guard<std::mutex> lockGuard(mLock);
    auto* piano = new WavePiano(5, 0.5, 0.0004);
    auto* piano2 = new WavePiano(0, 0, 0);
    Channel::setDefaultInstrument(piano);
    initChannels();
    MultiwaveGenerator::init(mBufferSize, mSampleRate);
    auto* generator = new MultiwaveGenerator();
    auto* callback = new AudioCallback(generator);
    AudioStreamBuilder builder;
    Result result = builder.setPerformanceMode(PerformanceMode::LowLatency)
            ->setSharingMode(mSharingMode)
            ->setDirection(Direction::Output)
            ->setFormat(AudioFormat::Float)
            ->setChannelCount(ChannelCount::Mono)
            ->setDataCallback(callback)
            ->setSampleRate(mSampleRate)
            ->openStream(mStream);
    if (result != Result::OK) {
        LOGE("Error creating audio stream: %s", convertToText(result));
        return result;
    }
    LOGI("Audio stream started successfully");
    result = mStream->requestStart();
    mBufferSize = mStream->getFramesPerBurst();
    if (result != Result::OK) {
        LOGE("Error starting audio stream: %s", convertToText(result));
        return result;
    }
    return result;
}

/**
 * Closes audio stream. Does nothing if stream is not open.
 * @return <code>Result::OK</code> if successful, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::stop() {
    std::lock_guard<std::mutex> lockGuard(mLock);
    Result result = Result::OK;
    if (mStream) {
        result = mStream->stop();
        mStream->close();
        mStream.reset();
        if (result == Result::OK) {
            LOGI("Audio stream closed successfully");
        } else {
            LOGE("Error closing audio stream: %s", convertToText(result));
        }
    }
    return result;
}

/** @return Current <a href="https://bit.ly/3nVpoM6">sample rate</a> */
int32_t AudioEngine::getSampleRate() {
    return mSampleRate;
}

/** @return All channels as <code>std::vector\<Channel*\></code> */
std::vector<Channel *> AudioEngine::getChannels() {
    return mChannels;
}

/**
 * Plays a note on defined channel.
 * @param channel number from 0 to 15.
 * @param note <a href="https://bit.ly/3MqvY7q">MIDI note</a>, from 0 to 127. For example, 0 is C0, 57 is A4, 127 is G10.
 * @param amplitude float from 0 to 1.0.
 */
void AudioEngine::noteOn(int8_t channel, int8_t note, float amplitude) {
    if (note < 0) {
        throw std::invalid_argument("Note must be non-negative number. For example, 0 is C0, 57 is A4, 127 is G10.");
    }
    mChannels[channel]->noteOn(note, amplitude);
}

/**
 * Silences the note on defined channel. Does nothing if specified note is not playing.
 * @param channel number from 0 to 15.
 * @param note <a href="https://bit.ly/3MqvY7q">MIDI note</a>, from 0 to 127. For example, 0 is C0, 57 is A4, 127 is G10.
 */
void AudioEngine::noteOff(int8_t channel, int8_t note) {
    if (note < 0) {
        throw std::invalid_argument("Note must be non-negative number. For example, 0 is C0, 57 is A4, 127 is G10.");
    }
    mChannels[channel]->noteOff(note);
}

/** Initializes all channels with default instrument. */
void AudioEngine::initChannels() {
    mChannels.clear();
    for (int8_t i = 0; i < mNumChannels; i++) {
        auto * channel = new Channel();
        mChannels.push_back(channel);
    }
}

/** Returns number of channels. But it's always 16. */
int8_t AudioEngine::getNumChannels() {
    return mNumChannels;
}
