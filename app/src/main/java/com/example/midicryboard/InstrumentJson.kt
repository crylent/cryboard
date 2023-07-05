package com.example.midicryboard

import android.content.Context
import android.util.Log
import com.example.midilib.Oscillator
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
private const val OSCILLATORS = "oscillators"
private const val SHAPE = "shape"
private const val AMPLITUDE = "amplitude"
private const val PHASE = "phase"
private const val FREQ_FACTOR = "freqFactor"
private const val DETUNE = "detune"
private const val DETUNE_UNISON_VOICES = "unisonVoices"
private const val DETUNE_LEVEL = "detuneLevel"

fun Instrument.Companion.fromJson(context: Context, json: JSONObject): Instrument {
    json.apply {
        val attack = optDouble(ATTACK, 0.0)
        val attackSharpness = optDouble( ATTACK_SHARPNESS, 1.0)
        val decay = optDouble(DECAY, 5.0)
        val decaySharpness = optDouble(DECAY_SHARPNESS, 1.0)
        val sustain = optDouble(SUSTAIN, 0.0)
        val release = optDouble(RELEASE, 0.0)
        val releaseSharpness = optDouble(RELEASE_SHARPNESS, 1.0)
        val instrument = if (optBoolean(IS_SYNTH)) {
            SynthInstrument(
                attack, decay, sustain, release,
                attackSharpness, decaySharpness, releaseSharpness
            ).apply {
                val oscillators = getJSONArray(OSCILLATORS)
                oscillators.forEach {
                    val shape = shapes[it.getString(SHAPE)] ?: return@forEach
                    val amplitude = it.optDouble(AMPLITUDE, 1.0)
                    val phase = it.optDouble(PHASE, 0.0)
                    val freqFactor = it.optDouble(FREQ_FACTOR, 1.0)
                    addOscillator(
                        Oscillator(shape, amplitude, phase, freqFactor).apply Detune@ {
                            val detune = it.optJSONObject(DETUNE) ?: return@Detune
                            val unisonVoices = detune.getInt(DETUNE_UNISON_VOICES)
                            val level = detune.getDouble(DETUNE_LEVEL)
                            enableDetune(unisonVoices, level)
                        }
                    )
                }
            }
        }
        else {
            (if (optBoolean(IS_SINGLE)) AssetInstrument.Single()
            else AssetInstrument(
                attack, decay, sustain, release,
                attackSharpness, decaySharpness, releaseSharpness
            )).apply {
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

private val shapes = mapOf(
    "sine" to Oscillator.Shape.SINE,
    "triangle" to Oscillator.Shape.TRIANGLE,
    "square" to Oscillator.Shape.SQUARE,
    "saw" to Oscillator.Shape.SAW,
    "reserve_saw" to Oscillator.Shape.REVERSE_SAW
)

private val shapesReversed = shapes.entries.associateBy({ it.value }) { it.key }

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
        val instrument = (this@toJson as SynthInstrument)
        put(OSCILLATORS, JSONArray().apply {
            instrument.forEachOscillator {
                put(JSONObject().apply {
                    put(SHAPE, shapesReversed[it.shape])
                    put(AMPLITUDE, it.amplitude)
                    put(PHASE, it.phase)
                    put(FREQ_FACTOR, it.frequencyFactor)
                    it.detune.also { detune ->
                        if (detune != null) {
                            put(DETUNE, JSONObject().apply {
                                put(DETUNE_LEVEL, detune.detune)
                                put(DETUNE_UNISON_VOICES, detune.unisonVoices)
                            })
                        }
                    }
                })
            }
        })
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