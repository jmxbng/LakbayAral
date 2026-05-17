package com.example.lakbayaral.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lakbayaral.ui.armode.ARModeScreen
import com.example.lakbayaral.ui.leaderboard.LeaderboardScreen
import com.example.lakbayaral.ui.login.LoginScreen
import com.example.lakbayaral.ui.mainmenu.MainMenuScreen
import com.example.lakbayaral.ui.map.MapScreen
import com.example.lakbayaral.ui.municipality.MunicipalityScreen
import com.example.lakbayaral.ui.quiz.QuizScreen
import com.example.lakbayaral.ui.splash.SplashScreen

object Routes {
    const val SPLASH        = "splash"
    const val LOGIN         = "login"
    const val MAIN_MENU     = "main_menu"
    const val MAP           = "map"
    const val MUNICIPALITY  = "municipality/{municipalityId}"
    const val AR_MODE       = "ar_mode/{municipalityId}/{mode}"
    const val QUIZ          = "quiz/{municipalityId}"
    const val LEADERBOARD   = "leaderboard"

    fun municipality(id: String)           = "municipality/$id"
    fun arMode(id: String, mode: String)   = "ar_mode/$id/$mode"
    fun quiz(id: String)                   = "quiz/$id"
}

@Composable
fun LakbayAralNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Routes.MAIN_MENU) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                onStartExploration = { navController.navigate(Routes.MAP) },
                onLeaderboard      = { navController.navigate(Routes.LEADERBOARD) }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                onMunicipalitySelected = { id ->
                    navController.navigate(Routes.municipality(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.MUNICIPALITY,
            arguments = listOf(navArgument("municipalityId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("municipalityId") ?: ""
            MunicipalityScreen(
                municipalityId = id,
                onARPlaces = { navController.navigate(Routes.arMode(id, "places")) },
                onARPeople = { navController.navigate(Routes.arMode(id, "people")) },
                onQuiz     = { navController.navigate(Routes.quiz(id)) },
                onBack     = { navController.popBackStack() }
            )
        }

        composable(
            Routes.AR_MODE,
            arguments = listOf(
                navArgument("municipalityId") { type = NavType.StringType },
                navArgument("mode")           { type = NavType.StringType }
            )
        ) { backStack ->
            val id   = backStack.arguments?.getString("municipalityId") ?: ""
            val mode = backStack.arguments?.getString("mode") ?: "places"
            ARModeScreen(
                municipalityId = id,
                mode           = mode,
                onBack         = { navController.popBackStack() },
                onGoToQuiz     = { navController.navigate(Routes.quiz(id)) }
            )
        }

        composable(
            Routes.QUIZ,
            arguments = listOf(navArgument("municipalityId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("municipalityId") ?: ""
            QuizScreen(
                municipalityId = id,
                onFinished     = { navController.navigate(Routes.MAP) {
                    popUpTo(Routes.MAP) { inclusive = false }
                }},
                onBack         = { navController.popBackStack() }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
