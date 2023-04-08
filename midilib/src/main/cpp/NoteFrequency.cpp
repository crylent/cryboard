#include "NoteFrequency.h"

float NoteFrequency::get(int8_t note) {
    return base_frequency * powf(2, float(note - base_note) / 12);
}
