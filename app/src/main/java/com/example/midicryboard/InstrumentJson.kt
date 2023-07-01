package com.example.midicryboard

import android.content.Context
import android.util.Log
import com.example.midilib.instrument.ApplicationAsset
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
private const val IS_SINGLE = "single"
private const val ASSETS = "assets"
private const val NOTE = "note"
private const val SAMPLE_ASSET = "sample"
private const val SAMPLE_BYTES = "sampleBytes"
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
        else {
            (if (optBoolean(IS_SINGLE)) AssetInstrument.Single()
            else AssetInstrument(attack, decay, sustain, release, attackSharpness, decaySharpness, releaseSharpness)).apply {
                getJSONArray(ASSETS).forEach {
                    val note = it.getInt(NOTE).toByte()
                    val isBase = it.optBoolean(IS_BASE)
                    it.opt(SAMPLE_BYTES)?.let { it1 -> Log.d("json", it1.toString()) }
                    val sampleBytes = it.optJSONArray(SAMPLE_BYTES)
                    if (sampleBytes != null) {
                        val bytes = ByteArray(sampleBytes.length())
                        for (i in 0 until sampleBytes.length()) {
                            bytes[i] = sampleBytes.getInt(i).toByte()
                        }
                        loadAsset(context, note, bytes, isBase)
                    }
                    else {
                        val sampleAsset = it.getString(SAMPLE_ASSET)
                        loadAsset(context, note, sampleAsset, isBase)
                    }
                }
            }
        }
        instrument.name = getString(NAME)
        return instrument
    }
}

fun Instrument.toJson(context: Context) = JSONObject().apply {
    put(NAME, name)
    val isSynth = this@toJson is SynthInstrument
    val isSingle = this@toJson is AssetInstrument.Single
    put(IS_SYNTH, isSynth)
    if (!isSingle) {
        put(ATTACK, attack)
        put(ATTACK_SHARPNESS, attackSharpness)
        put(DECAY, decay)
        put(DECAY_SHARPNESS, decaySharpness)
        put(SUSTAIN, sustain)
        put(RELEASE, release)
        put(RELEASE_SHARPNESS, releaseSharpness)
    } else {
        put(IS_SINGLE, true)
    }
    if (isSynth) {
        TODO()
    } else {
        val instrument = (this@toJson as AssetInstrument)
        put(ASSETS, JSONArray().apply {
            instrument.assetsList.forEach {
                put(JSONObject().apply {
                    put(NOTE, it.note)
                    if (it is ApplicationAsset) put(SAMPLE_ASSET, it.filename)
                    else {
                        put(SAMPLE_BYTES, JSONArray(it.readBytes(context)))
                    }
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