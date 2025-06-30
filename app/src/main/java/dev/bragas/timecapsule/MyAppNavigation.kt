package dev.bragas.timecapsule

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.bragas.timecapsule.ui.screen.auth.SignInScreen
import dev.bragas.timecapsule.ui.screen.auth.SignUpScreen
import dev.bragas.timecapsule.ui.screen.capsule.CapsuleDetailScreen
import dev.bragas.timecapsule.ui.screen.capsule.CapsuleListScreen
import dev.bragas.timecapsule.ui.screen.capsule.CreateTimeCapsuleScreen

@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "signIn") {
        composable("signIn") { SignInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("timeCapsuleList") {
            CapsuleListScreen(
                navController,
                onCreateClick = { navController.navigate("createCapsule") }
            )
        }
        composable(
            "capsuleDetail/{capsuleId}",
            arguments = listOf(navArgument("capsuleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val capsuleId = backStackEntry.arguments?.getString("capsuleId") ?: ""
            CapsuleDetailScreen(navController, capsuleId)
        }
        composable("createCapsule") {
            CreateTimeCapsuleScreen(
            navController,
            onCapsuleCreated = { navController.navigate("timeCapsuleList") })
        }
    }
}