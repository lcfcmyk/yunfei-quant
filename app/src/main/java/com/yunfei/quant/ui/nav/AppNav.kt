package com.yunfei.quant.ui.nav

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunfei.quant.ui.home.HomeScreen
import com.yunfei.quant.ui.login.LoginScreen
import com.yunfei.quant.ui.login.RegisterScreen
import com.yunfei.quant.ui.market.MarketScreen
import com.yunfei.quant.ui.onboarding.RomKeepAlivePage
import com.yunfei.quant.ui.profile.ApiKeyScreen
import com.yunfei.quant.ui.profile.ProfileScreen
import com.yunfei.quant.ui.splash.SplashScreen
import com.yunfei.quant.ui.strategy.StrategyScreen
import com.yunfei.quant.ui.vm.AppViewModel

data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem("home", "总览", Icons.Default.Dashboard),
    TabItem("strategy", "策略", Icons.Default.Tune),
    TabItem("market", "行情", Icons.Default.CandlestickChart),
    TabItem("profile", "我的", Icons.Default.Person),
)

@Composable
fun AppRoot(windowSizeClass: WindowSizeClass) {
    val vm: AppViewModel = viewModel()
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val isWide = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val inTabs = tabs.any { it.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (!isWide && inTabs) {
                NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                    tabs.forEach { tab ->
                        val selected = backStack?.destination?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { inner ->
        Row(modifier = Modifier.padding(inner).fillMaxSize()) {
            if (isWide && inTabs) {
                NavigationRail(modifier = Modifier.statusBarsPadding()) {
                    Spacer(Modifier.height(24.dp))
                    tabs.forEach { tab ->
                        val selected = backStack?.destination?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }

            NavHost(
                navController = nav,
                startDestination = "splash",
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                composable("splash") {
                    SplashScreen(onFinished = {
                        val target = when {
                            !vm.loggedIn -> "login"
                            !vm.apiBound -> "guide_apikey"
                            !vm.tutorialSeen -> "home_first"
                            else -> "home"
                        }
                        nav.navigate(target) { popUpTo("splash") { inclusive = true } }
                    })
                }
                composable("login") {
                    LoginScreen(
                        vm = vm,
                        onSuccess = {
                            nav.navigate("home") { popUpTo("login") { inclusive = true } }
                        },
                        onGoRegister = { nav.navigate("register") },
                    )
                }
                composable("register") {
                    RegisterScreen(
                        vm = vm,
                        onBack = { nav.popBackStack() },
                        onRegistered = {
                            nav.navigate("home") { popUpTo("login") { inclusive = true } }
                        },
                    )
                }
                composable("guide_apikey") {
                    ApiKeyScreen(vm, forceGuide = true) {
                        nav.navigate("home") { popUpTo("guide_apikey") { inclusive = true } }
                    }
                }
                composable("rom_guide") {
                    RomKeepAlivePage { nav.popBackStack() }
                }
                composable("home_first") {
                    HomeScreen(vm, showTutorial = true,
                        onTutorialDone = {
                            nav.navigate("home") { popUpTo("home_first") { inclusive = true } }
                        },
                        onOpenRomGuide = { nav.navigate("rom_guide") })
                }
                composable("home") {
                    HomeScreen(vm, showTutorial = false,
                        onTutorialDone = {},
                        onOpenRomGuide = { nav.navigate("rom_guide") })
                }
                composable("strategy") { StrategyScreen(vm) }
                composable("market") { MarketScreen(vm) }
                composable("profile") {
                    ProfileScreen(vm,
                        onApiKey = { nav.navigate("apikey") },
                        onRomGuide = { nav.navigate("rom_guide") })
                }
                composable("apikey") {
                    ApiKeyScreen(vm, forceGuide = false) { nav.popBackStack() }
                }
            }
        }
    }
}
