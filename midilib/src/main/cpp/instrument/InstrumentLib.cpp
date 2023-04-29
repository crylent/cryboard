#include "InstrumentLib.h"

vector<shared_ptr<Instrument>> InstrumentLib::mLib;

uint32_t InstrumentLib::addInstrument(shared_ptr<Instrument> instrument) {
    mLib.push_back(move(instrument));
    return mLib.size() - 1;
}

shared_ptr<Instrument> InstrumentLib::getInstrument(uint32_t id) {
    return mLib.at(id);
}
