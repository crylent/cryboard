#ifndef INSTRUMENT_LIB_H
#define INSTRUMENT_LIB_H

#include <memory>
#include <vector>
#include "Instrument.h"

using namespace std;

class InstrumentLib {
public:
    static uint32_t addInstrument(shared_ptr<Instrument> instrument);
    static shared_ptr<Instrument> getInstrument(uint32_t id);

private:
    static vector<shared_ptr<Instrument>> mLib;
};


#endif //INSTRUMENT_LIB_H
