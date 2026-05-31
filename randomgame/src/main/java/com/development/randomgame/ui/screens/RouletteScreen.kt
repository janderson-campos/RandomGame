package com.development.randomgame.ui.screens

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.development.randomgame.R
import com.development.randomgame.domain.model.Game
import com.development.randomgame.domain.model.GameSpec
import com.development.randomgame.ui.viewmodel.GameViewModel
import com.development.randomgame.navigation.Screen
import com.development.randomgame.ui.components.SettingsOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Prévia da tela da roleta para o Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun RouletteScreenPreview() {
    val navController = rememberNavController()
    // Repositório falso para a prévia
    val fakeRepository = object : com.development.randomgame.domain.repository.IGameRepository {
        override val history = kotlinx.coroutines.flow.MutableStateFlow<List<Game>>(emptyList())
        override suspend fun saveGameToHistory(game: Game) {}
        override suspend fun clearHistory() {}
    }
    val viewModel = GameViewModel(fakeRepository)
    RouletteScreen(navController = navController, specName = "MEDIUM_END", viewModel = viewModel)
}

/**
 * Tela da Roleta onde o usuário sorteia um jogo com base no setup selecionado.
 */
@Composable
fun RouletteScreen(navController: NavController, specName: String?, viewModel: GameViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showGamesDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Sons de efeito
    val spinSound = remember { MediaPlayer.create(context, R.raw.roleta_normal) }
    val winSound = remember { MediaPlayer.create(context, R.raw.selecionado) }

    // Libera os recursos de áudio quando a tela é fechada
    DisposableEffect(Unit) {
        onDispose {
            try {
                spinSound?.release()
                winSound?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Determina o setup selecionado
    val spec = try {
        GameSpec.valueOf(specName ?: GameSpec.MEDIUM_END.name)
    } catch (e: Exception) {
        GameSpec.MEDIUM_END
    }

    // Observa os estados do ViewModel
    val onlineGamesFlow by viewModel.onlineGames.collectAsState()
    val onlineGames = onlineGamesFlow.filter { it.spec == spec }
    
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val visualEffectsEnabled by viewModel.visualEffectsEnabled.collectAsState()

    // Carrega os jogos ao iniciar a tela
    LaunchedEffect(spec) {
        viewModel.fetchGamesFromInternet(spec)
    }

    var rotation by remember { mutableStateOf(0f) }
    var spinning by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var rollingGames by remember { mutableStateOf<Game?>(null) }
    val scope = rememberCoroutineScope()

    // Fundo animado
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
        // Efeito de partículas
        if (visualEffectsEnabled) {
            val particles = remember {
                List(15) {
                    Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                particles.forEach { (x, y, size) ->
                    val particleOffset by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = (5000 + (size * 5000)).toInt(), easing = LinearEasing),
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
                            .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(50))
                    )
                }
            }
        }

        // Animação de rotação da roleta
        val animatedRotation by animateFloatAsState(
            targetValue = rotation,
            animationSpec = tween(
                durationMillis = 3000,
                easing = CubicBezierEasing(0.1f, 0f, 0.2f, 1f)
            ),
            label = "RouletteRotation"
        )

        // Animação de escala durante o giro
        val wheelScale by animateFloatAsState(
            targetValue = if (spinning) 1.05f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "WheelScale"
        )

        // Diálogos (Configurações, Detalhes, Lista)
        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Configurações", color = Color.White) },
                containerColor = Color(0xFF001529),
                text = {
                    Column {
                        SettingsOption(title = "Sons", checked = soundEnabled, onCheckedChange = { viewModel.toggleSound(it) })
                        SettingsOption(title = "Vibração", checked = hapticEnabled, onCheckedChange = { viewModel.toggleHaptic(it) })
                        SettingsOption(title = "Efeitos Visuais", checked = visualEffectsEnabled, onCheckedChange = { viewModel.toggleVisualEffects(it) })
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettings = false }) { Text("OK", color = Color.Cyan) }
                }
            )
        }

        if (showDetailsDialog && selectedGame != null) {
            Dialog(onDismissRequest = { showDetailsDialog = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001529))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = selectedGame!!.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = selectedGame!!.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${selectedGame!!.name}+game"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00509d))
                        ) {
                            Text("Ver no Google")
                        }
                    }
                }
            }
        }

        if (showGamesDialog) {
            Dialog(onDismissRequest = { showGamesDialog = false }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001529))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxHeight(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Lista de Jogos", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(onlineGames.size) { index ->
                                val game = onlineGames[index]
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(painter = painterResource(id = game.imageRes), contentDescription = null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(game.name, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        Button(onClick = { showGamesDialog = false }, modifier = Modifier.padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00509d))) {
                            Text("Fechar")
                        }
                    }
                }
            }
        }

        // Conteúdo Principal da UI
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra de ferramentas superior
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                }
                
                Row {
                    IconButton(onClick = { navController.navigate(Screen.History.route) }) {
                        Icon(Icons.Default.History, contentDescription = "Histórico", tint = Color.White)
                    }
                    IconButton(onClick = { showGamesDialog = true }) {
                        Icon(Icons.Default.List, contentDescription = "Lista", tint = Color.White)
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Roleta de Jogos", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "Setup: ${spec.name.replace("_", " ")}", style = MaterialTheme.typography.bodyMedium, color = Color.Cyan.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(20.dp))

            // A Roleta
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .graphicsLayer {
                        rotationZ = animatedRotation
                        scaleX = wheelScale
                        scaleY = wheelScale
                    }
                    .clip(RoundedCornerShape(150.dp))
                    .background(Color.Black)
                    .border(4.dp, Color.Black, RoundedCornerShape(150.dp))
                    .clickable { if (!spinning) showGamesDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (spinning) {
                    rollingGames?.let { game ->
                        Image(painter = painterResource(id = game.imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                } else if (selectedGame != null) {
                    Image(painter = painterResource(id = selectedGame!!.imageRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.White.copy(alpha = 0.3f))
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.sweepGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Transparent)))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Ver Detalhes
            if (selectedGame != null && !spinning) {
                Button(
                    onClick = { showDetailsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver Detalhes", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Girar
            Button(
                onClick = {
                    if (!spinning && onlineGames.isNotEmpty()) {
                        scope.launch {
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            spinning = true
                            selectedGame = null
                            
                            val targetRotation = rotation + 1080f + Random.nextInt(360)
                            rotation = targetRotation

                            val animationJob = launch {
                                if (soundEnabled) {
                                    try {
                                        spinSound?.let {
                                            it.isLooping = true
                                            it.start()
                                        }
                                    } catch (e: Exception) { }
                                }
                                while (spinning) {
                                    rollingGames = onlineGames.random()
                                    if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    delay(100)
                                }
                            }
                            
                            delay(3000)
                            spinning = false
                            animationJob.cancel()
                            
                            try {
                                spinSound?.let { if (it.isPlaying) it.pause(); it.seekTo(0) }
                            } catch (e: Exception) { }
                            
                            selectedGame = onlineGames.random()
                            if (soundEnabled) { try { winSound?.start() } catch (e: Exception) { } }
                            if (visualEffectsEnabled) showConfetti = true
                            if (hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addToHistory(selectedGame!!)
                            delay(4000)
                            showConfetti = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (spinning) Color.Gray else Color(0xFF2196F3)),
                enabled = !spinning
            ) {
                Text(text = if (spinning) "SORTEANDO..." else "GIRAR ROLETA", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Trocar Setup", color = Color.White.copy(alpha = 0.6f))
            }
        }

        if (showConfetti) {
            ConfettiEffect()
        }
    }
}

/**
 * Efeito visual de confetes ao ganhar.
 */
@Composable
fun ConfettiEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "Confetti")
    val colors = listOf(Color.Yellow, Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan)
    
    val particles = remember {
        List(25) { Triple(Random.nextFloat(), Random.nextFloat(), colors.random()) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val density = LocalDensity.current

        particles.forEach { (xRatio, delayRatio, color) ->
            val yPos by infiniteTransition.animateFloat(
                initialValue = -100f,
                targetValue = height + 100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000 + (delayRatio * 1000).toInt(), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ConfettiY"
            )

            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 720f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000 + (delayRatio * 1000).toInt(), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ConfettiRotation"
            )

            Box(
                modifier = Modifier
                    .offset(x = with(density) { (xRatio * width).toDp() })
                    .graphicsLayer {
                        translationY = yPos
                        rotationZ = rotation
                    }
            ) {
                Box(modifier = Modifier.size(Random.nextInt(8, 14).dp).background(color, shape = RoundedCornerShape(2.dp)))
            }
        }
    }
}
