package com.example.chatbot

enum class Screen{
    HOME,   //-> ALl the tabs
    CHATBOT,
    OCRSCREEN,
}

sealed class NavigationItem(val  route:String,val title:String){
    data object HomeScreen:NavigationItem(Screen.HOME.name,"Home")
    data object ChatBotScreen:NavigationItem(Screen.CHATBOT.name,"ChatBot")
    data object OcrScreen:NavigationItem(Screen.OCRSCREEN.name,"OcrScreen")
}