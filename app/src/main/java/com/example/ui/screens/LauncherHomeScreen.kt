package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.LauncherQuest
import com.example.data.database.UnlockedCard
import com.example.data.database.UserStatsRow
import com.example.data.model.GachaCard
import com.example.data.model.GachaDatabase
import com.example.data.model.LauncherApp
import com.example.ui.theme.AnimeTheme
import com.example.ui.viewmodel.LauncherViewModel
import com.example.ui.widgets.MascotIllustration
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherHomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val quests by viewModel.allQuests.collectAsStateWithLifecycle()
    val unlockedCards by viewModel.allUnlockedCards.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val apps by viewModel.systemApps.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val mascotDialog by viewModel.mascotDialog.collectAsStateWithLifecycle()
    val lastSummonedCard by viewModel.lastSummonedCard.collectAsStateWithLifecycle()
    val isSummoning by viewModel.isSummoning.collectAsStateWithLifecycle()
    val selectedAppForLaunch by viewModel.selectedAppForMockLaunch.collectAsStateWithLifecycle()

    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val ramUsage by viewModel.systemRamUsage.collectAsStateWithLifecycle()
    val weatherDesc by viewModel.virtualTemp.collectAsStateWithLifecycle()

    val isBooted by viewModel.isBooted.collectAsStateWithLifecycle()
    val bootProgress by viewModel.bootProgress.collectAsStateWithLifecycle()
    val bootLogs by viewModel.bootLogs.collectAsStateWithLifecycle()
    val activeStatusEffect by viewModel.activeStatusEffect.collectAsStateWithLifecycle()
    val crtScanlinesEnabled by viewModel.crtScanlinesEnabled.collectAsStateWithLifecycle()
    val tactileFeedbackEnabled by viewModel.tactileFeedbackEnabled.collectAsStateWithLifecycle()

    // Sheet states
    var showAppDrawer by remember { mutableStateOf(false) }
    var showGachaShrine by remember { mutableStateOf(false) }
    var showQuestScroll by remember { mutableStateOf(false) }
    var showRuneForge by remember { mutableStateOf(false) }
    var showThemeSettings by remember { mutableStateOf(false) }
    var showChatPanel by remember { mutableStateOf(false) }

    // Chat prompt state
    var userChatInput by remember { mutableStateOf("") }

    // System App Launch Helper
    val onAppLaunch: (LauncherApp) -> Unit = { app ->
        if (app.isVirtual) {
            when (app.label) {
                "Gacha Shrine" -> showGachaShrine = true
                "Waifu Chat" -> showChatPanel = true
                "Manga Reader" -> {
                    viewModel.selectAppForMockLaunch(app)
                }
                "Rune Forge" -> showRuneForge = true
                "Quest Scroll" -> showQuestScroll = true
            }
        } else {
            if (app.launchIntent != null) {
                try {
                    context.startActivity(app.launchIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not launch ${app.label}", Toast.LENGTH_SHORT).show()
                }
            } else {
                viewModel.selectAppForMockLaunch(app)
            }
        }
    }

    // Dynamic clock ticking
    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            currentTimeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            currentDateString = SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(cal.time)
            delay(1000)
        }
    }

    // Continuous Particle Update Trigger
    LaunchedEffect(currentTheme) {
        while (true) {
            viewModel.updateParticles()
            delay(40) // ~25 FPS for smooth particle drifting
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F111A)) // Premium deep dark backdrop
                // Draw falling particle systems and beautiful mesh gradient glows programmatically on the background canvas!
                .drawBehind {
                    // Draw theme-specific glowing background mesh gradient blobs to simulate Frosted Glass ambient lighting
                    val (glow1, glow2) = when (currentTheme) {
                        AnimeTheme.SAKURA -> Pair(Color(0x28FFB7D5), Color(0x22E8AEFF)) // Pink & Purple
                        AnimeTheme.CYBER -> Pair(Color(0x3300FFCC), Color(0x25FF007F))  // Cyan & Hot Magenta
                        AnimeTheme.KNIGHT -> Pair(Color(0x2EFF9F0A), Color(0x22FF453A)) // Amber & Crimson
                    }
                    
                    // Top-Left radial glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glow1, Color.Transparent),
                            center = Offset(size.width * -0.1f, size.height * 0.15f),
                            radius = size.width * 0.85f
                        ),
                        radius = size.width * 0.85f,
                        center = Offset(size.width * -0.1f, size.height * 0.15f)
                    )

                    // Bottom-Right radial glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glow2, Color.Transparent),
                            center = Offset(size.width * 1.1f, size.height * 0.85f),
                            radius = size.width * 0.85f
                        ),
                        radius = size.width * 0.85f,
                        center = Offset(size.width * 1.1f, size.height * 0.85f)
                    )

                    // --- DYNAMIC ANIME STATUS AURA BACKGROUND GLOWS ---
                    when (activeStatusEffect) {
                        "Super Saiyan Rosé" -> {
                            // Dark dramatic magenta/pink godly flame aura
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x3FFF1C8F), Color(0x2235002D), Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                                    radius = size.width * 0.95f
                                ),
                                radius = size.width * 0.95f,
                                center = Offset(size.width * 0.5f, size.height * 0.5f)
                            )
                        }
                        "Limitless Void" -> {
                            // Satoru Gojo's infinite cosmic purple sphere
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x458E00FF), Color(0x1F000E33), Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                                    radius = size.width * 0.85f
                                ),
                                radius = size.width * 0.85f,
                                center = Offset(size.width * 0.5f, size.height * 0.35f)
                            )
                        }
                        "Ghoul Kagune" -> {
                            // Ken Kaneki's one-eyed kagune raw crimson energy
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x40FF0303), Color(0x251C0101), Color.Transparent),
                                    center = Offset(size.width * 0.1f, size.height * 0.9f),
                                    radius = size.width * 0.75f
                                ),
                                radius = size.width * 0.75f,
                                center = Offset(size.width * 0.1f, size.height * 0.9f)
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x40FF0303), Color(0x251C0101), Color.Transparent),
                                    center = Offset(size.width * 0.9f, size.height * 0.9f),
                                    radius = size.width * 0.75f
                                ),
                                radius = size.width * 0.75f,
                                center = Offset(size.width * 0.9f, size.height * 0.9f)
                            )
                        }
                        "Shinigami Realm" -> {
                            // Death Note cold, dark, grey vignette frame
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.Transparent, Color(0xE50B0C0E)),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.width * 1.3f
                                )
                            )
                        }
                        "Demon Slayer Breath" -> {
                            // Glowing deep orange Hinokami Kagura fire waves
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x49FF3700), Color(0x1F2B0700), Color.Transparent),
                                    center = Offset(size.width * 0.5f, size.height * 1.05f),
                                    radius = size.width * 0.9f
                                ),
                                radius = size.width * 0.9f,
                                center = Offset(size.width * 0.5f, size.height * 1.05f)
                            )
                        }
                    }

                    drawIntoCanvas { canvas ->
                        viewModel.particleList.forEach { p ->
                            // Custom specialty particle symbols depending on active status effect aura
                            val displayChar = when (activeStatusEffect) {
                                "Super Saiyan Rosé" -> if ((1..3).random() == 1) listOf("⚡", "🌹", "✨").random() else p.char
                                "Limitless Void" -> if ((1..3).random() == 1) listOf("🟣", "🧿", "♾️").random() else p.char
                                "Ghoul Kagune" -> if ((1..3).random() == 1) listOf("👺", "🩸", "🥀").random() else p.char
                                "Shinigami Realm" -> if ((1..3).random() == 1) listOf("🍎", "📓", "💀").random() else p.char
                                "Demon Slayer Breath" -> if ((1..3).random() == 1) listOf("🔥", "☀️", "🌊").random() else p.char
                                else -> p.char
                            }
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = p.size
                                alpha = (70..150).random()
                            }
                            canvas.nativeCanvas.drawText(
                                displayChar,
                                p.x * size.width,
                                p.y * size.height,
                                paint
                            )
                        }
                    }
                }
                .padding(innerPadding)
        ) {
            // MAIN HOME SCREEN CONTAINER (SWIPABLE PAGES or RICH SCROLL VIEW)
            // To support compact displays, we wrap in a beautiful scrollable Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // STYLIZED OS MOCK STATUS BAR (iOS Frosted Glass design style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusTime = if (currentTimeString.length >= 5) currentTimeString.substring(0, 5) else "09:41"
                    Text(
                        text = statusTime,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = currentTheme.textPrimary.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // LTE signal indicator circle dot style
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(modifier = Modifier.size(5.dp).background(currentTheme.primary, CircleShape))
                            Box(modifier = Modifier.size(5.dp).background(currentTheme.primary, CircleShape))
                            Box(modifier = Modifier.size(5.dp).background(currentTheme.primary, CircleShape))
                            Box(modifier = Modifier.size(5.dp).background(currentTheme.primary.copy(alpha = 0.4f), CircleShape))
                        }
                        Text(
                            text = "LTE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.textPrimary.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        )
                        // Battery outline with colored status level indicator
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(11.dp)
                                .border(1.dp, currentTheme.textPrimary.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                .padding(1.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(batteryLevel.toFloat() / 100f)
                                    .background(
                                        if (batteryLevel > 20) currentTheme.primary else currentTheme.accent,
                                        RoundedCornerShape(1.5.dp)
                                    )
                            )
                        }
                    }
                }

                // SECTION 1: SYSTEM TIME & CLOCK METRICS (iOS widget style)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Time Display
                    Text(
                        text = currentTimeString,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = currentTheme.primary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = currentTheme.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentDateString,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = currentTheme.textPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Simulated Weather & Phone Health HUD (Frosted Glass Style)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.08f)) // Frosted translucent background
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)) // Crisp translucent highlight
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Weather Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌤️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weatherDesc,
                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                            )
                        }
                        
                        // Separator
                        Text(text = "|", color = currentTheme.border.copy(alpha = 0.3f))

                        // RAM Usage Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "RAM",
                                tint = currentTheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RAM: $ramUsage",
                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                            )
                        }

                        // Separator
                        Text(text = "|", color = currentTheme.border.copy(alpha = 0.3f))

                        // Battery Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Battery",
                                tint = currentTheme.accent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BAT: $batteryLevel%",
                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                            )
                        }
                    }
                }

                // SECTION 2: INTERACTIVE MASCOT DISPLAY WIDGET (Frosted Glass Style)
                Card(
                    onClick = { viewModel.tapMascot() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("mascot_widget"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Character Canvas
                        Box(
                            modifier = Modifier
                                .size(105.dp)
                                .clip(CircleShape)
                                .border(2.dp, currentTheme.primary, CircleShape)
                        ) {
                            MascotIllustration(
                                theme = currentTheme,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        // Dialog Speech Box
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${currentTheme.mascotName} (Affinity: ${
                                    when(currentTheme) {
                                        AnimeTheme.SAKURA -> stats.affinitySakura
                                        AnimeTheme.CYBER -> stats.affinityCyber
                                        AnimeTheme.KNIGHT -> stats.affinityKnight
                                    }
                                })",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = mascotDialog,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = currentTheme.textPrimary,
                                        lineHeight = 18.sp
                                    ),
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { showChatPanel = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Chat",
                                        tint = currentTheme.onPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Chat",
                                        style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.onPrimary, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }

                // SECTION 3: CURRENCY & SHORTCUT HUB WIDGETS (iOS Style Row Layout - Frosted Glass)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Widget: Coin Ledger
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Gems Vault",
                                style = MaterialTheme.typography.labelMedium.copy(color = currentTheme.textSecondary)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💎", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stats.gems.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.primary
                                    )
                                )
                            }

                            Text(
                                text = "Completed Quests: ${stats.totalQuestsCompleted}",
                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                            )
                        }
                    }

                    // Right Widget: Summoning Shrine Deck Statistics
                    Card(
                        onClick = { showRuneForge = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mascot Deck",
                                style = MaterialTheme.typography.labelMedium.copy(color = currentTheme.textSecondary)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🔮", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${unlockedCards.size}/12",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.secondary
                                    )
                                )
                            }

                            Text(
                                text = "Pulls Cast: ${stats.totalGachaPulls}",
                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                            )
                        }
                    }
                }

                // SECTION 4: HYBRID LAUNCHER SHORTCUTS (Favorite grids, iOS layout style)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Core Launcher Programs",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.primary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display up to 8 core apps as hot shortcuts
                    val favoriteApps = apps.take(8)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        favoriteApps.chunked(4).forEach { rowApps ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    rowApps.forEach { app ->
                                        Column(
                                            modifier = Modifier
                                                .width(65.dp)
                                                .clickable { onAppLaunch(app) }
                                                .padding(vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            // Circle icon with a theme color border glowing!
                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(Color.White.copy(alpha = 0.08f))
                                                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = app.customEmoji, fontSize = 26.sp)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = app.label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = currentTheme.textPrimary,
                                                    fontSize = 11.sp
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SECTION 5: MASTER NAVIGATION DOCK (iOS Dock design style - Frosted Glass)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(32.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. App Drawer button
                        IconButton(
                            onClick = { showAppDrawer = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("app_drawer_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "App Drawer",
                                tint = currentTheme.primary
                            )
                        }

                        // 2. Gacha Shrine shortcut
                        IconButton(
                            onClick = { showGachaShrine = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("gacha_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Summon Shrine",
                                tint = currentTheme.secondary
                            )
                        }

                        // 3. Quest Scroll shortcut
                        IconButton(
                            onClick = { showQuestScroll = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("quest_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Quest Scroll",
                                tint = currentTheme.accent
                            )
                        }

                        // 4. Mascot Cards collections deck shortcut
                        IconButton(
                            onClick = { showRuneForge = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("deck_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Rune Forge",
                                tint = currentTheme.primary
                            )
                        }

                        // 5. Config Launcher Settings Cog
                        IconButton(
                            onClick = { showThemeSettings = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .testTag("settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Theme Settings",
                                tint = currentTheme.textPrimary
                            )
                        }
                    }
                }
            }

            // ==========================================
            // MODAL SHEET 1: APP DRAWER (All Installed + Anime Apps)
            // ==========================================
            if (showAppDrawer) {
                AppDrawerModal(
                    apps = apps,
                    searchQuery = searchQuery,
                    currentTheme = currentTheme,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onAppLaunch = {
                        showAppDrawer = false
                        onAppLaunch(it)
                    },
                    onDismiss = { showAppDrawer = false }
                )
            }

            // ==========================================
            // MODAL SHEET 2: SUMMON GACHA SHRINE
            // ==========================================
            if (showGachaShrine) {
                GachaShrineModal(
                    viewModel = viewModel,
                    stats = stats,
                    currentTheme = currentTheme,
                    isSummoning = isSummoning,
                    onDismiss = { showGachaShrine = false }
                )
            }

            // ==========================================
            // MODAL SHEET 3: DAILY QUESTS SCROLL
            // ==========================================
            if (showQuestScroll) {
                QuestScrollModal(
                    quests = quests,
                    stats = stats,
                    currentTheme = currentTheme,
                    onAddQuest = { viewModel.addQuest(it) },
                    onToggleQuest = { viewModel.toggleQuest(it) },
                    onDeleteQuest = { viewModel.deleteQuest(it) },
                    onDismiss = { showQuestScroll = false }
                )
            }

            // ==========================================
            // MODAL SHEET 4: RUNE FORGE (Card Deck & Level Up)
            // ==========================================
            if (showRuneForge) {
                RuneForgeModal(
                    unlockedCards = unlockedCards,
                    stats = stats,
                    currentTheme = currentTheme,
                    onUpgradeCard = { viewModel.upgradeCard(it) },
                    onDismiss = { showRuneForge = false }
                )
            }

            // ==========================================
            // MODAL SHEET 5: CHARACTER CHAT CONSOLE
            // ==========================================
            if (showChatPanel) {
                ChatConsoleModal(
                    currentTheme = currentTheme,
                    mascotDialog = mascotDialog,
                    userChatInput = userChatInput,
                    onChatInputChange = { userChatInput = it },
                    onSendMessage = {
                        viewModel.sendChatMessage(it)
                        userChatInput = ""
                    },
                    onDismiss = { showChatPanel = false }
                )
            }

            // ==========================================
            // MODAL SHEET 6: CUSTOMIZER & THEME SETTINGS
            // ==========================================
            if (showThemeSettings) {
                ThemeSettingsModal(
                    currentTheme = currentTheme,
                    stats = stats,
                    onThemeSelect = { viewModel.changeTheme(it) },
                    activeStatusEffect = activeStatusEffect,
                    onStatusEffectSelect = { viewModel.setStatusEffect(it) },
                    crtScanlinesEnabled = crtScanlinesEnabled,
                    onToggleCrtScanlines = { viewModel.toggleCrtScanlines() },
                    tactileFeedbackEnabled = tactileFeedbackEnabled,
                    onToggleTactileFeedback = { viewModel.toggleTactileFeedback() },
                    onTriggerBootCalibration = {
                        viewModel.startBootSequence()
                        showThemeSettings = false
                    },
                    onDismiss = { showThemeSettings = false }
                )
            }

            // ==========================================
            // MODAL SCREEN OVERLAY: RUNNING APP SIMULATOR
            // ==========================================
            selectedAppForLaunch?.let { app ->
                AppSimulatorOverlay(
                    app = app,
                    currentTheme = currentTheme,
                    onDismiss = { viewModel.selectAppForMockLaunch(null) }
                )
            }

            // ==========================================
            // PORTAL DIALOG: CARD SUMMON DRAMATIC REVEAL
            // ==========================================
            lastSummonedCard?.let { card ->
                SummonRevealDialog(
                    card = card,
                    currentTheme = currentTheme,
                    onDismiss = { viewModel.dismissSummonDialog() }
                )
            }

            // ==========================================
            // RETRO-FUTURISTIC CRT HOLOGRAPHIC SCANLINE SCREEN EFFECT
            // ==========================================
            if (crtScanlinesEnabled) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scanlineHeight = 4.dp.toPx()
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                        y += scanlineHeight
                    }
                }
            }

            // ==========================================
            // FULL SCREEN BOOT LAUNCHER / SPLASH CALIBRATION SEQUENCE
            // ==========================================
            if (!isBooted) {
                BootLauncherOverlay(
                    progress = bootProgress,
                    logs = bootLogs,
                    onSkip = { viewModel.skipBootSequence() }
                )
            }
        }
    }
}

// ==========================================
// FULL-SCREEN DIAGNOSTIC BOOT LOADER SYSTEM
// ==========================================
@Composable
fun BootLauncherOverlay(
    progress: Float,
    logs: List<String>,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080C))
            .clickable(enabled = false) {}, // absorb clicks
        contentAlignment = Alignment.Center
    ) {
        // Futuristic grid background lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 40.dp.toPx()
            val gridColor = Color(0xFF1E293B).copy(alpha = 0.15f)
            // Vertical lines
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += gridSpacing
            }
            // Horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                y += gridSpacing
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: System Diagnostic Calibration
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "OTAKU OS SYSTEM INITIALIZATION",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "SUPREME ANIME LAUNCHER v3.5-HD",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AURA CALIBRATOR: INTEGRATED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Center: Big anime emblem sphere representing DBZ/JJK/Tokyo Ghoul cores
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, Color(0xFFEC4899), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Glow circles
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f), CircleShape)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌹", fontSize = 54.sp) // Super Saiyan Rosé / Tanjiro fire / Gojo core
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ANIME MATRIX CORES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFF472B6),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Bottom Block: Logs & Progress bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Diagnostics console terminal panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        reverseLayout = true
                    ) {
                        items(logs.reversed()) { log ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = ">> ",
                                    color = Color(0xFF38BDF8),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = log,
                                    color = if (log.contains("COMPLETE") || log.contains("SUCCESS") || log.contains("COMPLETED") || log.contains("ONLINE") || log.contains("OPTIMAL") || log.contains("READY") || log.contains("STABLE")) Color(0xFF34D399) else Color(0xFF94A3B8),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar with percent text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALIBRATING CORES...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFEC4899),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Glow progress bar track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Skip option
                OutlinedButton(
                    onClick = onSkip,
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Text(
                        text = "BYPASS CALIBRATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------
// COMPONENTS: INDIVIDUAL POPUPS & MODALS
// ---------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerModal(
    apps: List<LauncherApp>,
    searchQuery: String,
    currentTheme: AnimeTheme,
    onSearchQueryChange: (String) -> Unit,
    onAppLaunch: (LauncherApp) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Anime", "Utility", "Social", "Media", "Games")

    val filteredApps = apps.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (searchQuery.isEmpty() || it.label.lowercase().contains(searchQuery.lowercase()))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header search field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "App Index Console",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Spotlight search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search programs...", color = currentTheme.textSecondary) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = currentTheme.primary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.primary,
                            unfocusedBorderColor = currentTheme.border.copy(alpha = 0.5f),
                            focusedContainerColor = currentTheme.background,
                            unfocusedContainerColor = currentTheme.background
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_search_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category scroll row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            AssistChip(
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, color = if (isSelected) currentTheme.onPrimary else currentTheme.textPrimary) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) currentTheme.primary else currentTheme.cardBackground
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // App Grids
                    if (filteredApps.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No nodes match parameters.", color = currentTheme.textSecondary)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredApps) { app ->
                                Column(
                                    modifier = Modifier
                                        .clickable { onAppLaunch(app) }
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (app.isVirtual) currentTheme.primary.copy(alpha = 0.15f)
                                                else currentTheme.cardBackground
                                            )
                                            .border(1.dp, currentTheme.border, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = app.customEmoji, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = app.label,
                                        style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textPrimary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaShrineModal(
    viewModel: LauncherViewModel,
    stats: UserStatsRow,
    currentTheme: AnimeTheme,
    isSummoning: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isPulsing by remember { mutableStateOf(false) }

    LaunchedEffect(isSummoning) {
        if (isSummoning) {
            isPulsing = true
        } else {
            isPulsing = false
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSummoning) onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Runic Gacha Shrine",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = currentTheme.primary),
                            modifier = Modifier.weight(1f)
                        )
                        if (!isSummoning) {
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                            }
                        }
                    }

                    // Content Core / Gacha Summon Button
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isSummoning) {
                            // High fidelity summoning magical matrix
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .rotate(if (isPulsing) 360f else 0f)
                                    .drawBehind {
                                        drawCircle(
                                            color = currentTheme.accent,
                                            radius = size.minDimension / 2f,
                                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                MascotIllustration(theme = currentTheme, modifier = Modifier.size(120.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "SUMMONING RESONANCE CORES...",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.accent
                                )
                            )
                            Text(
                                text = "Bypassing firewalls & casting mana seals",
                                style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary)
                            )
                        } else {
                            // Static Portal Idle Screen
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape)
                                    .background(currentTheme.cardBackground)
                                    .border(2.dp, currentTheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔮", fontSize = 72.sp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Gacha Summon Portal",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.textPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Spend 50 Gems to summon a randomized companion card.\nUR (3%), SSR (12%), SR (35%), R (50%)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = currentTheme.textSecondary,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "🔥 FEATURED HD GUEST STARS (RATE-UP) 🔥",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.primary,
                                    letterSpacing = 1.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Horizontal interactive guest stars showcase with Coil HD loaders
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val guests = GachaDatabase.cards.filter { it.characterTheme == "Legendary" }
                                guests.forEach { guest ->
                                    var showGuestDetail by remember { mutableStateOf(false) }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { showGuestDetail = true }
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(1.dp, guest.rarityColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .padding(6.dp)
                                            .width(62.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(currentTheme.background),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (guest.imageUrl != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(guest.imageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = guest.name,
                                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            } else {
                                                Text(text = guest.emoji, fontSize = 20.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = guest.name.split(":")[0],
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = currentTheme.textPrimary
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (showGuestDetail) {
                                        AlertDialog(
                                            onDismissRequest = { showGuestDetail = false },
                                            confirmButton = {
                                                TextButton(onClick = { showGuestDetail = false }) {
                                                    Text("AWESOME", color = guest.rarityColor, fontWeight = FontWeight.Bold)
                                                }
                                            },
                                            title = {
                                                Text(
                                                    text = guest.name,
                                                    fontWeight = FontWeight.Bold,
                                                    color = currentTheme.textPrimary,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            },
                                            text = {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(130.dp)
                                                            .clip(RoundedCornerShape(16.dp))
                                                            .background(Color.White.copy(alpha = 0.05f))
                                                            .border(2.dp, guest.rarityColor, RoundedCornerShape(16.dp))
                                                            .padding(4.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (guest.imageUrl != null) {
                                                            AsyncImage(
                                                                model = ImageRequest.Builder(LocalContext.current)
                                                                    .data(guest.imageUrl)
                                                                    .crossfade(true)
                                                                    .build(),
                                                                contentDescription = guest.name,
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                                            )
                                                        } else {
                                                            Text(text = guest.emoji, fontSize = 54.sp)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Text(
                                                        text = guest.subtitle,
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                            color = guest.rarityColor,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = guest.desc,
                                                        style = MaterialTheme.typography.bodyMedium.copy(color = currentTheme.textSecondary),
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(currentTheme.cardBackground)
                                                            .padding(8.dp),
                                                        horizontalArrangement = Arrangement.SpaceAround
                                                    ) {
                                                        Text("HP: ${guest.hp}", color = currentTheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                        Text("ATK: ${guest.attack}", color = currentTheme.secondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(24.dp),
                                            containerColor = currentTheme.surface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(currentTheme.cardBackground)
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💎 Your Balance: ", color = currentTheme.textPrimary)
                                Text(
                                    text = stats.gems.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.primary
                                )
                            }
                        }
                    }

                    // Bottom Cast Buttons
                    if (!isSummoning) {
                        Button(
                            onClick = { viewModel.summonCard() },
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accent),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("summon_submit_btn")
                        ) {
                            Text(
                                text = "SUMMON ONCE (50 💎)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestScrollModal(
    quests: List<LauncherQuest>,
    stats: UserStatsRow,
    currentTheme: AnimeTheme,
    onAddQuest: (String) -> Unit,
    onToggleQuest: (LauncherQuest) -> Unit,
    onDeleteQuest: (LauncherQuest) -> Unit,
    onDismiss: () -> Unit
) {
    var newQuestTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Quest Scroll",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quest Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(currentTheme.cardBackground)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Hero Level", style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary))
                            Text(
                                "LV. ${stats.totalQuestsCompleted / 5 + 1}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = currentTheme.primary)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Quests Completed", style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary))
                            Text(
                                stats.totalQuestsCompleted.toString(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = currentTheme.secondary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Add Quest Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newQuestTitle,
                            onValueChange = { newQuestTitle = it },
                            placeholder = { Text("Record a new daily quest...", color = currentTheme.textSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.primary,
                                unfocusedBorderColor = currentTheme.border.copy(alpha = 0.5f),
                                focusedContainerColor = currentTheme.background,
                                unfocusedContainerColor = currentTheme.background
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quest_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newQuestTitle.isNotBlank()) {
                                    onAddQuest(newQuestTitle)
                                    newQuestTitle = ""
                                }
                            })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newQuestTitle.isNotBlank()) {
                                    onAddQuest(newQuestTitle)
                                    newQuestTitle = ""
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(currentTheme.primary, RoundedCornerShape(12.dp))
                                .testTag("add_quest_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = currentTheme.onPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quest List
                    if (quests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📜", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No quests present in the logs.", color = currentTheme.textSecondary)
                                Text("Add a quest above to earn Gacha Gems!", style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quests) { quest ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(currentTheme.cardBackground)
                                        .border(
                                            1.dp,
                                            if (quest.isCompleted) currentTheme.primary.copy(alpha = 0.4f)
                                            else currentTheme.border.copy(alpha = 0.2f),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = quest.isCompleted,
                                        onCheckedChange = { onToggleQuest(quest) },
                                        colors = CheckboxDefaults.colors(checkedColor = currentTheme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = quest.title,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = if (quest.isCompleted) currentTheme.textSecondary
                                                else currentTheme.textPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Reward: +${quest.gemsReward} 💎",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = currentTheme.secondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    IconButton(onClick = { onDeleteQuest(quest) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = currentTheme.accent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuneForgeModal(
    unlockedCards: List<UnlockedCard>,
    stats: UserStatsRow,
    currentTheme: AnimeTheme,
    onUpgradeCard: (UnlockedCard) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rune Companion Forge",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your Companion Deck of unlocked character summon cards. Spend 20 Gems to upgrade a card's levels!",
                        style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(currentTheme.cardBackground)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "💎 Your Balance: ", color = currentTheme.textPrimary)
                        Text(text = "${stats.gems} Gems", fontWeight = FontWeight.Bold, color = currentTheme.primary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Companion Grids
                    if (unlockedCards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🥋", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No unlocked companions in your deck.", color = currentTheme.textSecondary)
                                Text("Roll the Gacha Shrine to unlock them!", style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary))
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(unlockedCards) { item ->
                                val cardModel = GachaDatabase.cards.find { it.id == item.id } ?: return@items
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.5.dp, cardModel.rarityColor, RoundedCornerShape(16.dp)),
                                    colors = CardDefaults.cardColors(containerColor = currentTheme.background),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Rarity Pill & Level
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(cardModel.rarityColor)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = cardModel.rarity,
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Text(
                                                text = "LV.${item.level}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.primary, fontWeight = FontWeight.Bold)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Card Avatar (Coil with fallback)
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(currentTheme.cardBackground)
                                                .border(1.5.dp, cardModel.rarityColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (cardModel.imageUrl != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(cardModel.imageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = cardModel.name,
                                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                )
                                            } else {
                                                Text(text = cardModel.emoji, fontSize = 28.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Card Name
                                        Text(
                                            text = cardModel.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = currentTheme.textPrimary,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Companion Level Stats
                                        val multiplier = 1f + (item.level - 1) * 0.15f
                                        Text(
                                            text = "HP: ${(cardModel.hp * multiplier).toInt()} | ATK: ${(cardModel.attack * multiplier).toInt()}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = { onUpgradeCard(item) },
                                            colors = ButtonDefaults.buttonColors(containerColor = cardModel.rarityColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(28.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = "UPGRADE (20 💎)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatConsoleModal(
    currentTheme: AnimeTheme,
    mascotDialog: String,
    userChatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.82f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive Waifu Chat",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Big Mascot Illustration & Chat Logs
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(3.dp, currentTheme.primary, CircleShape)
                        ) {
                            MascotIllustration(theme = currentTheme, modifier = Modifier.fillMaxSize())
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currentTheme.mascotName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Speech Bubble
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(currentTheme.cardBackground)
                                .border(1.dp, currentTheme.border.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = mascotDialog,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = currentTheme.textPrimary,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "💡 Tap the character on home screen or write below to unlock customized dialogs!",
                            style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Suggestion Chip Prompts
                    val suggestions = when(currentTheme) {
                        AnimeTheme.SAKURA -> listOf("Hello!", "How can you help?", "Tell me about weather", "Unlock gems")
                        AnimeTheme.CYBER -> listOf("Hello", "How is my CPU?", "Unlock gems", "Hack system")
                        AnimeTheme.KNIGHT -> listOf("Greetings!", "I want quests!", "About weather", "Spend gems")
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestions.forEach { suggest ->
                            SuggestionChip(
                                onClick = { onSendMessage(suggest) },
                                label = { Text(suggest, color = currentTheme.textPrimary) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = currentTheme.cardBackground
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Type Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userChatInput,
                            onValueChange = onChatInputChange,
                            placeholder = { Text("Ask ${currentTheme.mascotName} something...", color = currentTheme.textSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.primary,
                                unfocusedBorderColor = currentTheme.border.copy(alpha = 0.4f),
                                focusedContainerColor = currentTheme.background,
                                unfocusedContainerColor = currentTheme.background
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_text"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (userChatInput.isNotBlank()) {
                                    onSendMessage(userChatInput)
                                }
                            })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (userChatInput.isNotBlank()) {
                                    onSendMessage(userChatInput)
                                }
                            },
                            modifier = Modifier
                                .size(50.dp)
                                .background(currentTheme.primary, RoundedCornerShape(12.dp))
                                .testTag("chat_send_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = currentTheme.onPrimary)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsModal(
    currentTheme: AnimeTheme,
    stats: UserStatsRow,
    onThemeSelect: (AnimeTheme) -> Unit,
    activeStatusEffect: String,
    onStatusEffectSelect: (String) -> Unit,
    crtScanlinesEnabled: Boolean,
    onToggleCrtScanlines: () -> Unit,
    tactileFeedbackEnabled: Boolean,
    onToggleTactileFeedback: () -> Unit,
    onTriggerBootCalibration: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(32.dp),
                color = currentTheme.surface.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AURA & SETTINGS CUSTOMIZER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = currentTheme.textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // SECTION 1: COMPANION THEME SELECT
                        Text(
                            text = "1. CHOOSE COMPANION & THEME",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.secondary
                            )
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimeTheme.entries.forEach { theme ->
                                val isSelected = currentTheme == theme
                                Card(
                                    onClick = { onThemeSelect(theme) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            2.dp,
                                            if (isSelected) theme.primary else Color.Transparent,
                                            RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = theme.background)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .border(1.5.dp, theme.primary, CircleShape)
                                        ) {
                                            MascotIllustration(theme = theme, modifier = Modifier.fillMaxSize())
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = theme.displayName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = theme.primary
                                                )
                                            )
                                            Text(
                                                text = "Mascot: ${theme.mascotName}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = theme.textSecondary)
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = theme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // SECTION 2: STATUS EFFECT AURAS
                        Text(
                            text = "2. CHOOSE STATUS EFFECT AURA",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.secondary
                            )
                        )

                        val statusEffects = listOf(
                            Pair("None", "Default crystalline particle flows"),
                            Pair("Super Saiyan Rosé", "Divine crimson & purple lightning (DBZ)"),
                            Pair("Limitless Void", "Infinite cosmic purple spatial sparks (JJK)"),
                            Pair("Ghoul Kagune", "Crimson kagune cells & edge shadows (Tokyo Ghoul)"),
                            Pair("Shinigami Realm", "Death Note falling ash, apples & notebook auras"),
                            Pair("Demon Slayer Breath", "Solar Hinokami flame waves at boundaries")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            statusEffects.forEach { (effect, desc) ->
                                val isSelected = activeStatusEffect == effect
                                val indicatorColor = when(effect) {
                                    "Super Saiyan Rosé" -> Color(0xFFFF247F)
                                    "Limitless Void" -> Color(0xFF8F00FF)
                                    "Ghoul Kagune" -> Color(0xFFFF0D0D)
                                    "Shinigami Realm" -> Color(0xFF475569)
                                    "Demon Slayer Breath" -> Color(0xFFFF4D00)
                                    else -> currentTheme.primary
                                }

                                Card(
                                    onClick = { onStatusEffectSelect(effect) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.5.dp,
                                            if (isSelected) indicatorColor else Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) indicatorColor.copy(alpha = 0.15f) else currentTheme.cardBackground
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(indicatorColor, CircleShape)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = effect,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = currentTheme.textPrimary
                                                )
                                            )
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary)
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = indicatorColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // SECTION 3: SYSTEM INTERACTIVE SETTING EFFECTS
                        Text(
                            text = "3. INTERACTIVE SETTINGS EFFECTS",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.secondary
                            )
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = currentTheme.cardBackground)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // CRT Scanline Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Holographic CRT Scanlines",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = currentTheme.textPrimary
                                            )
                                        )
                                        Text(
                                            text = "Applies retro CRT screen layer to active panel.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary)
                                        )
                                    }
                                    Switch(
                                        checked = crtScanlinesEnabled,
                                        onCheckedChange = { onToggleCrtScanlines() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = currentTheme.primary,
                                            checkedTrackColor = currentTheme.primary.copy(alpha = 0.4f)
                                        )
                                    )
                                }

                                Divider(color = Color.White.copy(alpha = 0.08f))

                                // Tactile Feedback Toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Simulated Tactile Pulses",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = currentTheme.textPrimary
                                            )
                                        )
                                        Text(
                                            text = "Creates virtual feedback on interactive events.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = currentTheme.textSecondary)
                                        )
                                    }
                                    Switch(
                                        checked = tactileFeedbackEnabled,
                                        onCheckedChange = { onToggleTactileFeedback() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = currentTheme.primary,
                                            checkedTrackColor = currentTheme.primary.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                            }
                        }

                        // SECTION 4: TRIGGER SYSTEM BOOT
                        Button(
                            onClick = onTriggerBootCalibration,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reboot")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TRIGGER SYSTEM COLD REBOOT",
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Footer credits
                    Text(
                        text = "OtakuOS Launcher v3.5-HD • Developed with passion.",
                        style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

@Composable
fun AppSimulatorOverlay(
    app: LauncherApp,
    currentTheme: AnimeTheme,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .blur(8.dp)
            .clickable(enabled = false) {} // block click behind
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .border(2.dp, currentTheme.border, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = currentTheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(currentTheme.cardBackground)
                        .border(2.dp, currentTheme.border, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = app.customEmoji, fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Launching Program",
                    style = MaterialTheme.typography.labelLarge.copy(color = currentTheme.primary)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = app.label,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = app.description.ifEmpty { "Executing virtual core instructions..." },
                    style = MaterialTheme.typography.bodyMedium.copy(color = currentTheme.textSecondary),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CLOSE NODE INTERFACE", color = currentTheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummonRevealDialog(
    card: GachaCard,
    currentTheme: AnimeTheme,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(enabled = false) {} // block clicking
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .border(3.dp, card.rarityColor, RoundedCornerShape(28.dp))
                .background(currentTheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flash Sparkles
            Text(text = "✨ RESONANCE SUMMON REVEAL ✨", color = card.rarityColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // Rarity Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(card.rarityColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${card.rarity} Companion",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Emblem (Coil with fallback and high-definition card presentation frame)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(2.dp, card.rarityColor, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (card.imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(card.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = card.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Text(text = card.emoji, fontSize = 72.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = card.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = card.subtitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = currentTheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lore Text
            Text(
                text = card.desc,
                style = MaterialTheme.typography.bodyMedium.copy(color = currentTheme.textSecondary),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(currentTheme.cardBackground)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BASE HP", style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary))
                    Text(
                        card.hp.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = currentTheme.primary)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BASE ATK", style = MaterialTheme.typography.labelSmall.copy(color = currentTheme.textSecondary))
                    Text(
                        card.attack.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = currentTheme.secondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = card.rarityColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("RECRUIT TO DECK", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
