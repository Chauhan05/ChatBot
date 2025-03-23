package com.example.chatbot.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.chatbot.viewmodels.AppViewModel
import com.example.chatbot.viewmodels.OcrViewModel
import com.example.chatbot.screens.ChatPage
import com.example.chatbot.screens.HomeScreenPage
import com.example.chatbot.screens.OcrScreenPage
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    AppViewModel: AppViewModel
) {

    val OcrViewModel: OcrViewModel = viewModel()

    AnimatedNavHost(
        navController = navHostController,
        startDestination = NavigationItem.HomeScreen.route
    ) {
        composable(
            route = NavigationItem.HomeScreen.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            HomeScreenPage(modifier, AppViewModel, navHostController)
        }

        composable(
            route = NavigationItem.ChatBotScreen.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            ChatPage(modifier = modifier, viewModel = AppViewModel)
        }

        composable(
            route = NavigationItem.OcrScreen.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            OcrScreenPage(modifier = modifier, viewModel = OcrViewModel)
        }
    }
}
