package com.development.randomgame.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SpecSelection : Screen("spec_selection")
    object Roulette : Screen("roulette/{spec}") {
        fun createRoute(spec: String) = "roulette/$spec"
    }
    object History : Screen("history")
}
