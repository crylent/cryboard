package com.example.midicryboard

import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOff
import com.leff.midi.event.NoteOn
import com.leff.midi.util.MidiEventListener

class MidiReader: MidiEventListener {
    override fun onStart(fromBeginning: Boolean) {

    }

    override fun onEvent(event: MidiEvent?, ms: Long) {
        event.apply {
            if (this is NoteOn) Midi.noteOn(noteValue.toByte(), channel.toByte(), velocity.toByte())
            else if (this is NoteOff) Midi.noteOff(noteValue.toByte(), channel.toByte())
        }
    }

    override fun onStop(finished: Boolean) {

    }
}