package com.example.grammardaily.ai

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import com.example.grammardaily.data.AiJudge
import com.google.mediapipe.tasks.genai.llminference.LlmInference

class AiJudgeService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        @Suppress("DEPRECATION")
        val receiver = intent?.getParcelableExtra<ResultReceiver>(AiJudge.KEY_RECEIVER)
        val prompt = intent?.getStringExtra(AiJudge.KEY_PROMPT)
        Thread {
            val bundle = Bundle()
            try {
                val response = if (prompt.isNullOrBlank()) {
                    Engine.warmLoad(applicationContext)
                    "warmed"
                } else {
                    Engine.run(applicationContext, prompt)
                }
                bundle.putString(AiJudge.KEY_RESPONSE, response)
            } catch (t: Throwable) {
                bundle.putString(AiJudge.KEY_ERROR, t.javaClass.simpleName)
            }
            try {
                receiver?.send(0, bundle)
            } catch (t: Throwable) {
            }
            try {
                stopSelfResult(startId)
            } catch (t: Throwable) {
            }
        }.start()
        return START_NOT_STICKY
    }
}

private object Engine {

    @Volatile
    private var llm: LlmInference? = null

    @Volatile
    private var failed = false

    @Synchronized
    fun warmLoad(context: Context) {
        if (llm == null && !failed && AiJudge.isModelReady(context)) {
            try {
                llm = LlmInference.createFromOptions(
                    context.applicationContext,
                    LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(AiJudge.modelFile(context).absolutePath)
                        .setMaxTokens(256)
                        .setTopK(1)
                        .setTemperature(0.0f)
                        .build()
                )
            } catch (t: Throwable) {
                failed = true
                llm = null
            }
        }
    }

    @Synchronized
    fun run(context: Context, prompt: String): String {
        if (llm == null) {
            failed = false
            warmLoad(context)
        }
        val model = llm ?: throw IllegalStateException("The AI model did not load")
        return model.generateResponse(prompt)
    }
}
