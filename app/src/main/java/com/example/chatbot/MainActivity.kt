package com.example.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatbot.navigation.AppNavHost
import com.example.chatbot.ui.theme.ChatBotTheme
import com.example.chatbot.viewmodels.AppViewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val AppViewModel: AppViewModel = viewModel()
            ChatBotTheme {
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                val navController = rememberAnimatedNavController()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton({}) {
                                    Icon(Icons.Default.Menu, "menu Button")
                                }
                            },
                            title = { Text("Chat Bot") }, // Title color
//                            colors = TopAppBarDefaults.topAppBarColors(
//                                containerColor = Color(0xFF6200EA), // Background color
//                                titleContentColor = Color.White // Title color
//                            ),
                            actions = {
                                Icon(Icons.Default.MoreVert, "more")
                            },
                            scrollBehavior = scrollBehavior

                        )
                    },
                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
                ) { innerPadding ->
                    AppNavHost(
                        navController,
                        Modifier.padding(innerPadding),
                        AppViewModel
                    )
                }
            }
        }
    }
}

