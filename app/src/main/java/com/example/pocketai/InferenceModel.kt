package com.example.pocketai

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class InferenceModel private constructor(private val context: Context) {

    // Add a mutex to prevent concurrent access
    private val mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var llmInference: LlmInference

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var maxTokens: Int
        get() = sharedPreferences.getInt(KEY_MAX_TOKENS, DEFAULT_MAX_TOKENS)
        set(value) {
            sharedPreferences.edit().putInt(KEY_MAX_TOKENS, value).apply()
//            recreateLlmInference(context) // Reinitialize model with new value
        }
    private var topK: Int
        get() = sharedPreferences.getInt(KEY_TOP_K, DEFAULT_MAX_TOP_K)
        set(value) {
            sharedPreferences.edit().putInt(KEY_TOP_K, value).apply()
//            recreateLlmInference(context) // Reinitialize model with new value
        }

    init {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }
        llmInference = createLlmInference(context)
    }

    private fun createLlmInference(context: Context,maxtokens: Int = DEFAULT_MAX_TOKENS, maxtopK: Int = DEFAULT_MAX_TOP_K  ): LlmInference {
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(maxtokens)
            .setMaxTopK(maxtopK)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        return LlmInference.createFromOptions(context, options)
    }

    private fun recreateLlmInference(context: Context) {
        try {
            llmInference.close()
        } catch (e: Exception) {
            Log.e("InferenceModel", "Error closing LLM instance", e)
        }

        try {
            llmInference = createLlmInference(context, maxTokens, topK)
//            sharedPreferences.edit().putInt(KEY_MAX_TOKENS, maxTokens).apply()
//            sharedPreferences.edit().putInt(KEY_MAX_TOP_K, maxTopK).apply()
        } catch (e: Exception) {
            Log.e("InferenceModel", "Error recreating LLM instance", e)
//            sharedPreferences.edit().putInt(KEY_MAX_TOKENS, maxTokens).apply()
//            sharedPreferences.edit().putInt(KEY_MAX_TOP_K, maxTopK).apply()
            throw e
        }
    }

    fun updateMaxTokensAndMaxTopK(newMaxTokens: Int, newMaxTopK: Int) {
        val previousValue = maxTokens
        val previousTopK = topK
        try {
            sharedPreferences.edit().putInt(KEY_MAX_TOKENS, newMaxTokens).apply()
            sharedPreferences.edit().putInt(KEY_TOP_K, newMaxTopK).apply()
            coroutineScope.launch {  // Run recreation in background
                mutex.withLock {
                    recreateLlmInference(context)
                }
            }
        } catch (e: Exception) {
            sharedPreferences.edit().putInt(KEY_MAX_TOKENS, previousValue).apply()
            sharedPreferences.edit().putInt(KEY_TOP_K, previousTopK).apply()
            throw e
        }
    }

    suspend fun generateResponseAsync(prompt: String) {
        mutex.withLock {
            val gemmaPrompt = "$prompt<start_of_turn>model\n"
            llmInference.generateResponseAsync(gemmaPrompt)
        }
    }

    companion object {
        private const val MODEL_PATH = "/data/local/tmp/gemma-2b-it-cpu-int4.bin"
        private const val PREFS_NAME = "llm_prefs"
        private const val KEY_MAX_TOKENS = "max_tokens"
        private const val KEY_TOP_K = "max_top_k"
        private const val DEFAULT_MAX_TOKENS = 1024
        private const val DEFAULT_MAX_TOP_K = 40

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: InferenceModel? = null

        fun getInstance(context: Context): InferenceModel {
            return instance ?: synchronized(this) {
                instance ?: InferenceModel(context.applicationContext).also { instance = it }
            }
        }
    }
}