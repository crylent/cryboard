#include "SawtoothOscillator.h"

float SawtoothOscillator::eval(double time, float frequency) {
    float p = calcPhase(time, frequency);
    return mAmplitude * float(p / M_PI);
}
