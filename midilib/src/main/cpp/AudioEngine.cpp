#include "AudioEngine.h"
#include "log.h"
#include "instrument/SynthInstrument.h"
#include "instrument/AssetInstrument.h"
#include "oscillators/SawtoothOscillator.h"

shared_ptr<oboe::AudioStream> AudioEngine::mStream;
mutex AudioEngine::mLock;
SharingMode AudioEngine::mSharingMode = SharingMode::Exclusive;
int32_t AudioEngine::mSampleRate = AUTO_DEFINITION;
int32_t AudioEngine::mBufferSize = AUTO_DEFINITION;
double AudioEngine::mTimeIncrement;
shared_ptr<FXList> AudioEngine::mMasterEffects;
vector<unique_ptr<Channel>> AudioEngine::mChannels = vector<unique_ptr<Channel>>();

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

/**
 * Starts audio engine in <i>exclusive</i> <b>sharing mode</b>. <b>Sample rate</b> and <b>buffer size</b> are auto-defined.
 * @return <code>Result::OK</code> if started successfully, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::start() {
    lock_guard<mutex> lockGuard(mLock);
    auto defaultSynth = make_shared<SynthInstrument>();
    defaultSynth->setEnvelope(0.25, 5, 0.1, 0.25);
    defaultSynth->addOscillator(make_unique<SawtoothOscillator>(1, 0, 1));
    defaultSynth->getOscillatorByIndex(0).setDetune();

    Channel::setDefaultInstrument(move(defaultSynth));
    initChannels();
    auto player = make_unique<WavePlayer>();
    mMasterEffects = player->getEffects();
    auto* callback = new AudioCallback(move(player));

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

    result = mStream->requestStart();
    if (result != Result::OK) {
        LOGE("Error starting audio stream: %s", convertToText(result));
        return result;
    }
    LOGI("Audio stream: started");

    return result;
}

/**
 * Pauses audio stream.
 * @return <code>Result::OK</code> if successful, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::pause() {
    Result result = mStream->requestPause();
    if (result == Result::OK) {
        LOGI("Audio stream: paused");
    } else {
        LOGI("Error pausing audio stream: %s", convertToText(result));
    }
    return result;
}

/**
 * Resumes audio stream.
 * @return <code>Result::OK</code> if successful, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::resume() {
    Result result = mStream->requestStart();
    if (result == Result::OK) {
        LOGI("Audio stream: resumed");
    } else {
        LOGI("Error resuming audio stream: %s", convertToText(result));
    }
    return result;
}

/**
 * Closes audio stream. Does nothing if stream is not open.
 * @return <code>Result::OK</code> if successful, <code>Result::{some_error}</code> otherwise.
 */
Result AudioEngine::stop() {
    lock_guard<mutex> lockGuard(mLock);
    Result result = Result::OK;
    if (mStream) {
        result = mStream->stop();
        mStream->close();
        mStream.reset();
        if (result == Result::OK) {
            LOGI("Audio stream: closed");
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
vector<unique_ptr<Channel>>& AudioEngine::getChannels() {
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
        throw invalid_argument("Note must be non-negative number. For example, 0 is C0, 57 is A4, 127 is G10.");
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
        throw invalid_argument("Note must be non-negative number. For example, 0 is C0, 57 is A4, 127 is G10.");
    }
    mChannels[channel]->noteOff(note);
}

/** Initializes all channels with default instrument. */
void AudioEngine::initChannels() {
    mChannels.clear();
    mChannels.reserve(mNumChannels);
    for (int8_t i = 0; i < mNumChannels; i++) {
        mChannels.push_back(make_unique<Channel>());
    }
}

/**
 * @return Number of channels. It's always 16.
 */
uint8_t AudioEngine::getNumChannels() {
    return mNumChannels;
}

/**
 * @return Time increment (in seconds) corresponding to current sample rate.
 */
double AudioEngine::getTimeIncrement() {
    return mTimeIncrement;
}

FXList &AudioEngine::getMasterFX() {
    return *mMasterEffects;
}

void AudioEngine::allNotesOff(int8_t channel) {
    mChannels[channel]->allNotesOff();
}
