package com.esamept12026.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

import com.esamept12026.R

/*
 *  Classe che gestisce i suoni emessi durante la pressione di un pulsante o
 *  durante la visualizzazione della sequenza che l'utente dovrà riprodurre.
 */
class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<String, Int>()

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()

        //Associa ogni colore al suo suono.
        soundMap["R"] = soundPool.load(context, R.raw.red, 1)
        soundMap["B"] = soundPool.load(context, R.raw.blue, 1)
        soundMap["G"] = soundPool.load(context, R.raw.green, 1)
        soundMap["Y"] = soundPool.load(context, R.raw.yellow, 1)
        soundMap["M"] = soundPool.load(context, R.raw.magenta, 1)
        soundMap["C"] = soundPool.load(context, R.raw.cyan, 1)
    }

    fun play(colorCode: String) {
        val soundId = soundMap[colorCode] ?: return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}