#include "AudioEngine.h"

#ifndef LOG_H
#include "log.h"
#endif

#ifndef WAVE_SYNTH_H
#include "instrument/WaveSynth.h"
#endif

#ifndef SAWTOOTH_OSCILLATOR_H
#include "oscillators/SawtoothOscillator.h"
#endif

#include "soundfx/Limiter.h"

std::shared_ptr<oboe::AudioStream> AudioEngine::mStream;
std::mutex AudioEngine::mLock;
SharingMode AudioEngine::mSharingMode = SharingMode::Exclusive;
int32_t AudioEngine::mSampleRate = AUTO_DEFINITION;
int32_t AudioEngine::mBufferSize = AUTO_DEFINITION;
double AudioEngine::mTimeIncrement;
std::vector<std::shared_ptr<Channel>> AudioEngine::mChannels = std::vector<std::shared_ptr<Channel>>();

/**
 * Starts audio engine with specified configuration.
 * @param sharingMode <a href="https://bit.ly/3KIM1fB"><i>exclusive</i> or <i>shared</i></a>
 * @param sampleRate the most common <a href="https://bit.ly/3nVpoM6">sample rates</a> are 44100 and 48000
 * @param bufferSize Default is 256. Using larger buffers might guard against such glitches, but a large buffer also introduces longer audio latency.
 * @return <code>Result::OK</code> if started successfully, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::start(SharingMode sharingMode, int32_t sampleRate, int32_t bufferSize) {
    mSharingMode = sharingMode;
    mSampleRate = sampleRate;
    mBufferSize = bufferSize;
    return start();
}

#define ADJUST_DETUNE(osc, func) osc->adjustDetune([] (const shared_ptr<Detune>& detune) { detune->func; })
#define ADJUST_DEFINE(synth, osc, func) ADJUST_DETUNE((synth)->getOscillatorByIndex(osc), func)

/**
 * Starts audio engine in <i>exclusive</i> <b>sharing mode</b>. <b>Sample rate</b> and <b>buffer size</b> are auto-defined.
 * @return <code>Result::OK</code> if started successfully, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::start() {
    std::lock_guard<std::mutex> lockGuard(mLock);
    auto defaultSynth = make_shared<WaveSynth>();
    defaultSynth->setEnvelope(0.25, 5, 0.1, 0.25);
    defaultSynth->addOscillator(make_shared<SawtoothOscillator>(1, 0, 1));
    //defaultSynth->getOscillatorByIndex(0)->setDetune(2, 0.005);

    Channel::setDefaultInstrument(defaultSynth);
    initChannels();
    auto generator = make_shared<MultiwaveGenerator>();
    auto* callback = new AudioCallback(generator);

    AudioStreamBuilder builder;
    builder.setPerformanceMode(PerformanceMode::LowLatency)
            ->setSharingMode(mSharingMode)
            ->setDirection(Direction::Output)
            ->setFormat(AudioFormat::Float)
            ->setChannelCount(ChannelCount::Mono)
            ->setDataCallback(callback);
    if (mSampleRate != AUTO_DEFINITION)
        builder.setSampleRate(mSampleRate);
    if (mBufferSize != AUTO_DEFINITION)
        builder.setFramesPerDataCallback(mBufferSize);

    Result result = builder.openStream(mStream);
    if (result != Result::OK) {
        LOGE("Error creating audio stream: %s", convertToText(result));
        return result;
    }
    LOGI("Audio stream: created");

    if (mSampleRate == AUTO_DEFINITION)
        mSampleRate = mStream->getSampleRate();
    if (mBufferSize == AUTO_DEFINITION)
        mBufferSize = mStream->getFramesPerBurst();
    LOGI("Sample rate: %d", mSampleRate);
    LOGI("Buffer size: %d", mBufferSize);

    mTimeIncrement = 1.0 / mSampleRate;

    generator->addEffect(make_shared<Limiter>());

    result = mStream->requestStart();
    if (result != Result::OK) {
        LOGE("Error starting audio stream: %s", convertToText(result));
        return result;
    }
    LOGI("Audio stream: started");
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

/**
 * @return Current <a href="https://bit.ly/3nVpoM6">sample rate</a>
 */
int32_t AudioEngine::getSampleRate() {
    return mSampleRate;
}

/**
 * @return Current buffer size
 */
int32_t AudioEngine::getBufferSize() {
    return mBufferSize;
}

/**
 * @return All channels as <code>std::vector\<Channel*\></code>
 */
std::vector<std::shared_ptr<Channel>> AudioEngine::getChannels() {
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
        auto channel = std::make_shared<Channel>();
        mChannels.push_back(channel);
    }
}

/**
 * @return Number of channels. It's always 16.
 */
int8_t AudioEngine::getNumChannels() {
    return mNumChannels;
}

/**
 * @return Time increment (in seconds) corresponding to current sample rate.
 */
double AudioEngine::getTimeIncrement() {
    return mTimeIncrement;
}
