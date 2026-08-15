package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminAudioState(
    val isPlaying: Boolean = false,
    val currentTrackTitle: String = "",
    val trackDurationMs: Int = 0,
    val currentPositionMs: Int = 0,
    val selectedUri: Uri? = null,
    val isDiscoStrobeActive: Boolean = false
)

class AdminAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val _audioState = MutableStateFlow(AdminAudioState())
    val audioState: StateFlow<AdminAudioState> = _audioState.asStateFlow()

    fun loadAndPlayCustomSong(uri: Uri) {
        try {
            stop()
            val fileName = getFileName(uri) ?: "Пользовательский трек.mp3"

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                prepare()
                start()
                setOnCompletionListener {
                    _audioState.value = _audioState.value.copy(isPlaying = false, currentPositionMs = 0)
                }
            }

            val duration = mediaPlayer?.duration ?: 0
            _audioState.value = _audioState.value.copy(
                isPlaying = true,
                currentTrackTitle = fileName,
                trackDurationMs = duration,
                selectedUri = uri
            )
        } catch (e: Exception) {
            Log.e("AdminAudioPlayer", "Error playing custom track", e)
            _audioState.value = _audioState.value.copy(
                isPlaying = false,
                currentTrackTitle = "Ошибка воспроизведения файла"
            )
        }
    }

    fun play() {
        try {
            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    _audioState.value = _audioState.value.copy(isPlaying = true)
                }
            }
        } catch (e: Exception) {
            Log.e("AdminAudioPlayer", "Error resuming playback", e)
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    _audioState.value = _audioState.value.copy(isPlaying = false)
                }
            }
        } catch (e: Exception) {
            Log.e("AdminAudioPlayer", "Error pausing playback", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
            mediaPlayer = null
            _audioState.value = _audioState.value.copy(
                isPlaying = false,
                currentPositionMs = 0
            )
        } catch (e: Exception) {
            Log.e("AdminAudioPlayer", "Error stopping playback", e)
        }
    }

    fun toggleDiscoStrobe(active: Boolean) {
        _audioState.value = _audioState.value.copy(isDiscoStrobeActive = active)
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            // fallback
        }
        return name ?: uri.lastPathSegment
    }
}
