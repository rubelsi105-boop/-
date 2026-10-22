package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false
    private var onSpeechFinishedListener: (() -> Unit)? = null

    init {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onSpeechFinishedListener?.invoke()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onSpeechFinishedListener?.invoke()
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.ENGLISH
            }
            isReady = true
        }
    }

    fun speak(text: String, onComplete: () -> Unit = {}) {
        if (isReady && tts != null) {
            onSpeechFinishedListener = onComplete
            tts?.stop()
            // Clean text for speech synthesis (remove code blocks or markdown stars)
            val speechCleaned = text
                .replace(Regex("```[\\s\\S]*?```"), "কোড স্ন্যাপশট")
                .replace("*", "")
                .replace("#", "")
            val utteranceId = "rubel_tts_${System.currentTimeMillis()}"
            tts?.speak(speechCleaned, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
        onSpeechFinishedListener?.invoke()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
