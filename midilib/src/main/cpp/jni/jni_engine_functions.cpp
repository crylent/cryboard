#include "../player/WavePlayer.h"

static void renderNSamples(WavePlayer& player, size_t bufferSize, vector<float>& samples) {
    auto* buffer = new float[bufferSize];
    player.fillBuffer(buffer, bufferSize);
    samples.insert(samples.end(), buffer, buffer + bufferSize - 1);
}