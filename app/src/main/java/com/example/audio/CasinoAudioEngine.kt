package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural Real-time Audio Synthesizer for Casino Slot Machine.
 * Generates genuine mechanical click sounds, reel hum, stop thuds, win chimes,
 * and jackpot fanfare using direct PCM synthesis via AudioTrack.
 */
class CasinoAudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    var isMuted: Boolean = false

    private val sampleRate = 44100
    private var spinTickJob: Job? = null

    private fun playTone(
        frequency: Double,
        durationMs: Int,
        amplitude: Float = 0.6f,
        decay: Boolean = true,
        type: WaveType = WaveType.SINE
    ) {
        if (isMuted) return
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val envelope = if (decay) {
                        1.0 - (i.toDouble() / numSamples)
                    } else {
                        1.0
                    }
                    val rawSample = when (type) {
                        WaveType.SINE -> sin(2.0 * PI * frequency * t)
                        WaveType.SQUARE -> if (sin(2.0 * PI * frequency * t) >= 0) 0.8 else -0.8
                        WaveType.TRIANGLE -> {
                            val period = 1.0 / frequency
                            val phase = (t % period) / period
                            if (phase < 0.5) 4.0 * phase - 1.0 else 3.0 - 4.0 * phase
                        }
                        WaveType.NOISE -> (Math.random() * 2.0 - 1.0)
                    }
                    val sample = (rawSample * Short.MAX_VALUE * amplitude * envelope).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore audio playback errors if device audio is occupied
            }
        }
    }

    /** Sound when user taps SPIN button or pulls lever */
    fun playSpinStart() {
        if (isMuted) return
        scope.launch {
            playTone(280.0, 60, amplitude = 0.5f, type = WaveType.TRIANGLE)
            delay(50)
            playTone(420.0, 90, amplitude = 0.6f, type = WaveType.TRIANGLE)
        }
    }

    /** Rapid ticking while reels are rolling */
    fun startReelSpinningLoop() {
        if (isMuted) return
        spinTickJob?.cancel()
        spinTickJob = scope.launch {
            var delayMs = 60L
            while (isActive) {
                playTone(550.0 + (Math.random() * 80), 25, amplitude = 0.25f, type = WaveType.TRIANGLE)
                delay(delayMs)
            }
        }
    }

    fun stopReelSpinningLoop() {
        spinTickJob?.cancel()
        spinTickJob = null
    }

    /** Satisfying mechanical thud / clunk when an individual reel stops */
    fun playReelStop(reelIndex: Int) {
        if (isMuted) return
        val baseFreq = 160.0 + (reelIndex * 40.0)
        playTone(baseFreq, 120, amplitude = 0.8f, type = WaveType.TRIANGLE)
        playTone(baseFreq * 0.5, 90, amplitude = 0.6f, type = WaveType.SINE)
    }

    /** Melodic chime on winning payline */
    fun playWinSound(tier: WinSoundTier) {
        if (isMuted) return
        scope.launch {
            when (tier) {
                WinSoundTier.SMALL -> {
                    val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                    for (note in notes) {
                        playTone(note, 140, amplitude = 0.6f)
                        delay(90)
                    }
                }
                WinSoundTier.MEDIUM -> {
                    val notes = listOf(440.0, 554.37, 659.25, 880.0, 1108.73) // A4, C#5, E5, A5, C#6
                    for (note in notes) {
                        playTone(note, 180, amplitude = 0.7f, type = WaveType.TRIANGLE)
                        delay(110)
                    }
                }
                WinSoundTier.BIG -> {
                    // Dramatic triumphant fanfare
                    val notes = listOf(523.25, 659.25, 783.99, 1046.50, 783.99, 1046.50, 1318.51)
                    for (note in notes) {
                        playTone(note, 220, amplitude = 0.8f, type = WaveType.TRIANGLE)
                        delay(120)
                    }
                }
                WinSoundTier.JACKPOT_777 -> {
                    // Legendary 777 Jackpot theme
                    val chords = listOf(
                        523.25, 659.25, 783.99, 1046.50,
                        1174.66, 1318.51, 1567.98, 2093.00
                    )
                    for (note in chords) {
                        playTone(note, 250, amplitude = 0.85f, type = WaveType.TRIANGLE)
                        delay(100)
                    }
                    delay(100)
                    // High fanfare flourishes
                    for (i in 0..4) {
                        playTone(1567.98, 120, amplitude = 0.7f)
                        delay(80)
                        playTone(2093.00, 150, amplitude = 0.8f)
                        delay(100)
                    }
                }
            }
        }
    }

    /** Coin counting sound effect */
    fun playCoinClink() {
        if (isMuted) return
        val pitch = 1600.0 + Math.random() * 400.0
        playTone(pitch, 60, amplitude = 0.35f, type = WaveType.SINE)
    }

    /** Roulette ball bouncing sound */
    fun playBallBounce() {
        if (isMuted) return
        val pitch = 900.0 + Math.random() * 300.0
        playTone(pitch, 35, amplitude = 0.4f, type = WaveType.TRIANGLE)
    }

    /** Card / Number reveal flip sound */
    fun playRevealFlip() {
        if (isMuted) return
        playTone(450.0, 50, amplitude = 0.5f, type = WaveType.SINE)
    }

    /** Disappointment / Loss thud */
    fun playLossSound() {
        if (isMuted) return
        scope.launch {
            playTone(320.0, 120, amplitude = 0.4f, type = WaveType.SINE)
            delay(100)
            playTone(240.0, 180, amplitude = 0.35f, type = WaveType.SINE)
        }
    }

    /** UI Button click */
    fun playButtonClick() {
        if (isMuted) return
        playTone(700.0, 40, amplitude = 0.3f, type = WaveType.SINE)
    }

    enum class WaveType {
        SINE, SQUARE, TRIANGLE, NOISE
    }

    enum class WinSoundTier {
        SMALL, MEDIUM, BIG, JACKPOT_777
    }
}
