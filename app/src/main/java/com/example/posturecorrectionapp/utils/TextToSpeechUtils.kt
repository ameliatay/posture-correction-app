package com.example.posturecorrectionapp.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.*

class TextToSpeechUtils : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    fun init(context: Context) {
        tts = TextToSpeech(context, this)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            @Deprecated("TTS, added annotation for suppression")
            // Function run when TTS finishes speaking an item in the queue
            override fun onDone(utteranceId: String?) {
                tts?.stop()
            }

            @Deprecated("TTS, added annotation for suppression")
            override fun onError(utteranceId: String?) {
            }

            @Deprecated("TTS, added annotation for suppression")
            override fun onStart(utteranceId: String?) {
            }
        })
    }

    // Logs message if TTS fails to initialise as the UK Locale language is not supported
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.UK)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","Language not supported!")
            }
        }
    }

    fun speak(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_ADD, null,"")
    }

    fun destroy() {
        // Shutdown TTS Engine to release resources
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

}