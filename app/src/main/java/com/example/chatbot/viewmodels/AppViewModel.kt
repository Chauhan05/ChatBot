package com.example.chatbot.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.BuildConfig
import com.example.chatbot.data.Constants
import com.example.chatbot.data.MessageModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    // Using mutableStateListOf to make the list observable
    private val _messageList = mutableStateListOf<MessageModel>()
    val messageList: List<MessageModel> = _messageList

    // Typing indicator state
    val isTyping = mutableStateOf(false)

    // Text input state
    private val _input = mutableStateOf("")
    val input: State<String> get() = _input

    // Initialize Gemini model
    val model = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.15f
            topK = 32
            topP = 1f
            maxOutputTokens = 4096
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
        )
    )

    init {
        // Add a welcome message
        _messageList.add(
            MessageModel(
                message = "Hi there! I'm Gemini. How can I help you today?",
                role = "model",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun updateInput(newInput: String) {
        _input.value = newInput
    }

    fun clearInput() {
        _input.value = ""
    }

    fun sendMessage(question: String) {
        // Don't send empty messages
        if (question.isBlank()) return

        // Add user message with current timestamp
        _messageList.add(
            MessageModel(
                message = question,
                role = "user",
                timestamp = System.currentTimeMillis()
            )
        )

        // Set typing indicator
        isTyping.value = true

        viewModelScope.launch {
            try {
                // Start chat with message history
                val chat = model.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }.toList()
                )

                // Send message to Gemini
                val response = chat.sendMessage(question)

                // Add model response with current timestamp
                _messageList.add(
                    MessageModel(
                        message = response.text?.toString()
                            ?: "Sorry, I couldn't generate a response.",
                        role = "model",
                        timestamp = System.currentTimeMillis()
                    )
                )

                Log.i("Response", response.text.toString())
            } catch (e: Exception) {
                // Handle error
                _messageList.add(
                    MessageModel(
                        message = "Sorry, an error occurred: ${e.localizedMessage}",
                        role = "model",
                        timestamp = System.currentTimeMillis()
                    )
                )
                Log.e("ChatError", "Error sending message", e)
            } finally {
                // Turn off typing indicator
                isTyping.value = false
            }
        }
    }
}