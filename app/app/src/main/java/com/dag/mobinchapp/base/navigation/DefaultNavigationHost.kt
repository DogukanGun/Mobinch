package com.dag.mobinchapp.base.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dag.mobinchapp.base.extensions.ObserveAsEvents
import com.dag.mobinchapp.features.aibot.AiBotScreen
import com.dag.mobinchapp.features.login.presentation.LoginView
import com.dag.mobinchapp.features.splash.SplashView
import com.dag.mobinchapp.features.home.presentation.HomeView

@Composable
fun DefaultNavigationHost(
    modifier: Modifier = Modifier,
    startDestination: Destination = Destination.Splash,
    navigator: DefaultNavigator,
    navBackStackEntryState: (NavBackStackEntry) -> Unit,
) {
    val navController = rememberNavController()
    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ) {
                action.navOptions(this)
            }
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }
    ObserveAsEvents(flow = navController.currentBackStackEntryFlow){
        navBackStackEntryState(it)
    }
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            startDestination = startDestination
        ) {
            splashComposable<Destination.Splash> {
                SplashView(
                    navController = navController
                )
            }

            composableWithAnimations<Destination.LoginScreen> {
                LoginView(
                    navController = navController
                )
            }

            composableWithAnimations<Destination.HomeScreen> {
                HomeView(
                    navController = navController
                )
            }

            composableWithAnimations<Destination.AIView> {
                AiBotScreen()
            }

        }
    }
}