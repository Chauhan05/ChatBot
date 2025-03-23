package com.example.chatbot

data class MessageModel(
    val message:String,
//    either user or computer
    val role:String,
    val timestamp: Long = System.currentTimeMillis()
)