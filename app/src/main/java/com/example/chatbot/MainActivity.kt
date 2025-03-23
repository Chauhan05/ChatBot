//package com.example.chatbot
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.animation.ExperimentalAnimationApi
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.ScaffoldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.chatbot.ui.theme.ChatBotTheme
//import com.google.accompanist.navigation.animation.rememberAnimatedNavController
//
//class MainActivity : ComponentActivity() {
//    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            val AppViewModel: AppViewModel = viewModel()
//            ChatBotTheme {
//                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
//                val navController = rememberAnimatedNavController()
//                Scaffold(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .nestedScroll(scrollBehavior.nestedScrollConnection),
//                    topBar = {
//                        TopAppBar(
//                            navigationIcon = {
//                                IconButton({}) {
//                                    Icon(Icons.Default.Menu, "menu Button")
//                                }
//                            },
//                            title = { Text("Chat Bot") }, // Title color
////                            colors = TopAppBarDefaults.topAppBarColors(
////                                containerColor = Color(0xFF6200EA), // Background color
////                                titleContentColor = Color.White // Title color
////                            ),
//                            actions = {
//                                Icon(Icons.Default.MoreVert, "more")
//                            },
//                            scrollBehavior = scrollBehavior
//
//                        )
//                    },
//                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
//                ) { innerPadding ->
//                    AppNavHost(
//                        navController,
//                        Modifier.padding(innerPadding),
//                        AppViewModel
//                    )
//                }
//            }
//        }
//    }
//}
//

package com.example.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatbot.ui.theme.ChatBotTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val AppViewModel: AppViewModel = viewModel()
            val systemUiController = rememberSystemUiController()
            val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            // Set status bar color to match app theme
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !isDarkTheme
                )
            }

            ChatBotTheme {
                // Create a connection that doesn't affect scrolling when keyboard appears
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                val navController = rememberAnimatedNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .systemBarsPadding(), // Respect system bars
                    topBar = {
                        TopAppBar(
                            title = { Text("AI Assistant") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            navigationIcon = {
                                IconButton({}) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menu Button",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            },
                            actions = {
                                IconButton({}) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More Options",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
                ) { innerPadding ->
                    AppNavHost(
                        navHostController = navController,
                        modifier = Modifier
                            .padding(innerPadding)
                            .imePadding(), // Handle keyboard properly
                        AppViewModel = AppViewModel
                    )
                }
            }
        }
    }
}
