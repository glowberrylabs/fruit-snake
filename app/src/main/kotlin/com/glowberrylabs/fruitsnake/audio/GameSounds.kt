package com.glowberrylabs.fruitsnake.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.glowberrylabs.fruitsnake.R

class GameSounds(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val eatSound = soundPool.load(context, R.raw.eat, 1)
    private val gameOverSound = soundPool.load(context, R.raw.game_over, 1)

    fun playEat() {
        soundPool.play(eatSound, VOLUME, VOLUME, PRIORITY, 0, 1f)
    }

    fun playGameOver() {
        soundPool.play(gameOverSound, VOLUME, VOLUME, PRIORITY, 0, 1f)
    }

    fun stopAll() {
        soundPool.autoPause()
    }

    fun release() {
        soundPool.release()
    }

    private companion object {
        const val MAX_STREAMS = 2
        const val VOLUME = 0.7f
        const val PRIORITY = 1
    }
}
