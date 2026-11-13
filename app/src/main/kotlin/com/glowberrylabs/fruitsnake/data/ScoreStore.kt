package com.glowberrylabs.fruitsnake.data

import android.content.Context

class ScoreStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadHighScore(): Int = preferences.getInt(KEY_HIGH_SCORE, 0)

    fun saveHighScore(score: Int): Boolean {
        if (score <= loadHighScore()) return false
        preferences.edit().putInt(KEY_HIGH_SCORE, score).apply()
        return true
    }

    fun isSoundEnabled(): Boolean = preferences.getBoolean(KEY_SOUND_ENABLED, true)

    fun setSoundEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isSlowMode(): Boolean = preferences.getBoolean(KEY_SLOW_MODE, false)

    fun setSlowMode(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_SLOW_MODE, enabled).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "fruit_snake"
        const val KEY_HIGH_SCORE = "high_score"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_SLOW_MODE = "slow_mode"
    }
}
