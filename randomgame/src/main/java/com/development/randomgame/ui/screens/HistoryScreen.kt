package com.development.randomgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.development.randomgame.domain.model.Game
import com.development.randomgame.ui.viewmodel.GameViewModel

/**
 * Tela que exibe o histórico de jogos que foram sorteados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: GameViewModel) {
    // Observa o histórico do ViewModel
    val history by viewModel.history.collectAsState()

    // Transição infinita para o fundo animado
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundTransition")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BackgroundOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF123C45), Color(0xFF266E5E), Color(0xFF123C45)),
                    start = Offset(backgroundOffset - 1000f, 0f),
                    end = Offset(backgroundOffset, 1000f)
                )
            )
    ) {
        // Partículas flutuantes para efeito visual
        val particles = remember {
            List(8) {
                Triple(kotlin.random.Random.nextFloat(), kotlin.random.Random.nextFloat(), kotlin.random.Random.nextFloat())
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            particles.forEach { (x, y, size) ->
                val particleOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = (8000 + (size * 8000)).toInt(), easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "ParticleOffset"
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = (x * 400).dp,
                            y = ((y + particleOffset) % 1f * 800).dp
                        )
                        .size((10 + size * 20).dp)
                        .background(Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(50))
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Histórico de Jogos", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    },
                    actions = {
                        // Botão para limpar o histórico, visível apenas se houver itens
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearHistory() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Limpar", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            if (history.isEmpty()) {
                // Mensagem quando o histórico está vazio
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum jogo sorteado ainda",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Lista de jogos sorteados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(history) { game ->
                        HistoryCard(game = game)
                    }
                }
            }
        }
    }
}

/**
 * Prévia da tela de histórico para o Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    val navController = rememberNavController()
    // Repositório falso para a prévia
    val fakeRepository = object : com.development.randomgame.domain.repository.IGameRepository {
        override val history = kotlinx.coroutines.flow.MutableStateFlow<List<Game>>(emptyList())
        override suspend fun saveGameToHistory(game: Game) {}
        override suspend fun clearHistory() {}
    }
    val viewModel = GameViewModel(fakeRepository)
    HistoryScreen(navController = navController, viewModel = viewModel)
}

/**
 * Cartão individual que exibe os detalhes de um jogo no histórico.
 */
@Composable
fun HistoryCard(game: Game) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = game.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = game.spec.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Yellow.copy(alpha = 0.8f)
                )
            }
        }
    }
}
