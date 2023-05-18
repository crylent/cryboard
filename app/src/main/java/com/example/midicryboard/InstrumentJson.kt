package com.example.midicryboard

import android.content.Context
import com.example.midilib.instrument.AssetInstrument
import com.example.midilib.instrument.Instrument
import com.example.midilib.instrument.SynthInstrument
import org.json.JSONArray
import org.json.JSONObject

private const val ATTACK = "attack"
private const val ATTACK_SHARPNESS = "attackSharpness"
private const val DECAY = "decay"
private const val DECAY_SHARPNESS = "decaySharpness"
private const val SUSTAIN = "sustain"
private const val RELEASE = "release"
private const val RELEASE_SHARPNESS = "releaseSharpness"
private const val IS_SYNTH = "synth"
private const val ASSETS = "assets"
private const val NOTE = "note"
private const val SAMPLE = "sample"
private const val IS_BASE = "base"
private const val NAME = "name"

fun Instrument.Companion.fromJson(context: Context, json: JSONObject): Instrument {
    json.apply {
        val attack = optDouble(ATTACK, 0.0)
        val attackSharpness = optDouble( ATTACK_SHARPNESS, 1.0)
        val decay = optDouble(DECAY, 5.0)
        val decaySharpness = optDouble(DECAY_SHARPNESS, 1.0)
        val sustain = optDouble(SUSTAIN, 0.0)
        val release = optDouble(RELEASE, 0.0)
        val releaseSharpness = optDouble(RELEASE_SHARPNESS, 1.0)
        val instrument = if (optBoolean(IS_SYNTH))
            SynthInstrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness).apply {
                TODO("oscillators")
            }
        else AssetInstrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness).apply {
            getJSONArray(ASSETS).forEach {
                loadAsset(
                    context,
                    it.getInt(NOTE).toByte(),
                    it.getString(SAMPLE),
                    it.optBoolean(IS_BASE)
                )
            }
            repeatAssets = true
        }
        instrument.name = getString(NAME)
        return instrument
    }
}

fun Instrument.toJson() = JSONObject().apply {
    put(NAME, name)
    val isSynth = this@toJson is SynthInstrument
    put(IS_SYNTH, isSynth)
    put(ATTACK, attack)
    put(ATTACK_SHARPNESS, attackSharpness)
    put(DECAY, decay)
    put(DECAY_SHARPNESS, decaySharpness)
    put(SUSTAIN, sustain)
    put(RELEASE, release)
    put(RELEASE_SHARPNESS, releaseSharpness)
    if (isSynth) {
        TODO()
    } else {
        val instrument = (this@toJson as AssetInstrument)
        put(ASSETS, JSONArray().apply {
            instrument.assetsList.forEach {
                put(JSONObject().apply {
                    put(NOTE, it.note)
                    put(SAMPLE, it.filename)
                    put(IS_BASE, it.isBaseAsset)
                })
            }
        })
    }
}

fun JSONArray.forEach(function: (JSONObject) -> Unit) {
    for (i in 0 until length()) {
        function(getJSONObject(i))
    }
}