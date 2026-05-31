package com.development.randomgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.development.randomgame.data.local.AppDatabase
import com.development.randomgame.data.repository.GameRepository
import com.development.randomgame.navigation.Screen
import com.development.randomgame.ui.screens.HistoryScreen
import com.development.randomgame.ui.screens.RouletteScreen
import com.development.randomgame.ui.screens.SpecSelectionScreen
import com.development.randomgame.ui.screens.WelcomeScreen
import com.development.randomgame.ui.theme.RandomGameTheme
import com.development.randomgame.ui.viewmodel.GameViewModel

/**
 * Fábrica para instanciar o GameViewModel injetando o repositório.
 */
class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Define o tema global do app
            RandomGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

/**
 * Gerenciador de navegação principal (NavHost).
 */
@Preview
@Composable
fun AppNavHost() {
    val context = LocalContext.current
    // Inicialização das dependências (Banco de Dados e Repositório)
    val database = AppDatabase.getDatabase(context)
    val repository = GameRepository(database.gameDao())
    val factory = GameViewModelFactory(repository)
    
    val navController = rememberNavController()
    // Obtém o ViewModel compartilhado entre as telas
    val gameViewModel: GameViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Rota para a tela de boas-vindas
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        // Rota para a tela de seleção de setup (especificações)
        composable(Screen.SpecSelection.route) {
            SpecSelectionScreen(navController = navController, viewModel = gameViewModel)
        }

        // Rota para a roleta, recebendo o setup como argumento
        composable(
            route = Screen.Roulette.route,
            arguments = listOf(
                androidx.navigation.navArgument("spec") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val spec = backStackEntry.arguments?.getString("spec")
            RouletteScreen(navController = navController, specName = spec, viewModel = gameViewModel)
        }

        // Rota para a tela de histórico
        composable(Screen.History.route) {
            HistoryScreen(navController = navController, viewModel = gameViewModel)
        }
    }
}
