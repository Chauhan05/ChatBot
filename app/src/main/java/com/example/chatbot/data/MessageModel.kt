package com.example.chatbot.data

data class MessageModel(
    val message:String,
//    either user or computer
    val role:String,
    val timestamp: Long = System.currentTimeMillis()
)