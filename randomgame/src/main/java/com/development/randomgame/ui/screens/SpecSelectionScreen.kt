package com.development.randomgame.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.development.randomgame.domain.model.GameSpec
import com.development.randomgame.navigation.Screen
import com.development.randomgame.ui.viewmodel.GameViewModel
import com.development.randomgame.ui.components.SettingsOption
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.development.randomgame.R
import kotlinx.coroutines.delay

/**
 * Prévia da tela de seleção de setup para o Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun SpecSelectionScreenPreview() {
    val navController = rememberNavController()
    // Repositório falso para a prévia
    val fakeRepository = object : com.development.randomgame.domain.repository.IGameRepository {
        override val history = kotlinx.coroutines.flow.MutableStateFlow<List<com.development.randomgame.domain.model.Game>>(emptyList())
        override suspend fun saveGameToHistory(game: com.development.randomgame.domain.model.Game) {}
        override suspend fun clearHistory() {}
    }
    val viewModel = GameViewModel(fakeRepository)
    SpecSelectionScreen(navController = navController, viewModel = viewModel)
}

@Composable
fun SpecSelectionScreen(navController: NavController, viewModel: GameViewModel) {
    val haptic = LocalHapticFeedback.current
    var showSettings by remember { mutableStateOf(false) }
    
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val visualEffectsEnabled by viewModel.visualEffectsEnabled.collectAsState()

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
        if (visualEffectsEnabled) {
            val particles = remember {
                List(12) {
                    Triple(kotlin.random.Random.nextFloat(), kotlin.random.Random.nextFloat(), kotlin.random.Random.nextFloat())
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                particles.forEach { (x, y, size) ->
                    val particleOffset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = (7000 + (size * 7000)).toInt(), easing = LinearEasing),
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
                            .size((15 + size * 25).dp)
                            .background(Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(50))
                    )
                }
            }
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Configurações", color = Color.White) },
                containerColor = Color(0xFF001529),
                text = {
                    Column {
                        SettingsOption(
                            title = "Sons",
                            checked = soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )
                        SettingsOption(
                            title = "Vibração",
                            checked = hapticEnabled,
                            onCheckedChange = { viewModel.toggleHaptic(it) }
                        )
                        SettingsOption(
                            title = "Efeitos Visuais",
                            checked = visualEffectsEnabled,
                            onCheckedChange = { viewModel.toggleVisualEffects(it) }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) { Text("OK", color = Color.Cyan) }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) + expandVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Qual o seu PC?",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Selecione o seu setup para começarmos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            val cardNames = listOf("HIGH_END", "MEDIUM_END", "LOW_END")
            val cardTitles = listOf("Forte (High-End)", "Mediano (Medium)", "Fraco (Low-End)")
            val cardDescs = listOf("Roda tudo no talo!", "Equilíbrio é tudo.", "O importante é jogar!")
            val cardImages = listOf(R.drawable.pc_high_end, R.drawable.pc_half_end, R.drawable.pc_low_end)
            val cardColors = listOf(Color(0xFF2E7D32), Color(0xFF1565C0), Color(0xFFB71C1C))

            cardTitles.forEachIndexed { index, title ->
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(200L * index)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally { it } + fadeIn() + expandHorizontally()
                ) {
                    Column {
                        SpecCard(
                            title = title,
                            description = cardDescs[index],
                            imageRes = cardImages[index],
                            color = cardColors[index],
                            onClick = { 
                                if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                navController.navigate(Screen.Roulette.createRoute(cardNames[index])) 
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navController.navigate(Screen.History.route) }) {
                Text("Ver Histórico de Jogos", color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun SpecCard(
    title: String,
    description: String,
    imageRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
