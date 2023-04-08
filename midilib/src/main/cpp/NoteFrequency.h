#ifndef NOTE_FREQUENCY_H
#define NOTE_FREQUENCY_H

#ifndef OBOE_H
#include "oboe/Oboe.h"
#endif

class NoteFrequency {
public:
    static float get(int8_t note);

private:
    // Base note is A4
    static int8_t const base_note = 57;
    static float constexpr base_frequency = 440;
};


#endif //NOTE_FREQUENCY_H
