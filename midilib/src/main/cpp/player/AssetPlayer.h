#ifndef ASSET_PLAYER_H
#define ASSET_PLAYER_H

#ifndef SOUND_PLAYER_H
#include "SoundPlayer.h"
#endif

class AssetPlayer : public SoundPlayer {
public:
    void fillBuffer(float* buffer, int32_t numFrames) override;

private:
    vector<unique_ptr<int>> a;
};


#endif //ASSET_PLAYER_H
