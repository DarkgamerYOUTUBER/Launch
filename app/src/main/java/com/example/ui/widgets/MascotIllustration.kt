package com.example.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.AnimeTheme

@Composable
fun MascotIllustration(
    theme: AnimeTheme,
    modifier: Modifier = Modifier
) {
    // Infinite transition for breathing/hovering effect
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_anim")
    
    val breathingOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val centerY = (h / 2f) + breathingOffset
        val radius = w * 0.32f

        // Draw background magic/cyber glow circle
        val bgGlowColor = when (theme) {
            AnimeTheme.SAKURA -> Color(0x33FFB7D5)
            AnimeTheme.CYBER -> Color(0x3300FFCC)
            AnimeTheme.KNIGHT -> Color(0x33FF9F0A)
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(bgGlowColor, Color.Transparent),
                center = Offset(centerX, centerY),
                radius = radius * 1.5f
            ),
            center = Offset(centerX, centerY),
            radius = radius * 1.5f
        )

        // Draw character specific geometries
        when (theme) {
            AnimeTheme.SAKURA -> {
                // SAKURA MAGE DRAWING
                
                // 1. Pink Twin Tails (Background hair)
                drawCircle(
                    color = Color(0xFFFFB7D5),
                    radius = radius * 0.4f,
                    center = Offset(centerX - radius * 0.8f, centerY + radius * 0.2f)
                )
                drawCircle(
                    color = Color(0xFFFFB7D5),
                    radius = radius * 0.4f,
                    center = Offset(centerX + radius * 0.8f, centerY + radius * 0.2f)
                )
                // Pigtail bows (purple)
                drawCircle(
                    color = Color(0xFFE8AEFF),
                    radius = radius * 0.12f,
                    center = Offset(centerX - radius * 0.7f, centerY)
                )
                drawCircle(
                    color = Color(0xFFE8AEFF),
                    radius = radius * 0.12f,
                    center = Offset(centerX + radius * 0.7f, centerY)
                )

                // 2. Face
                drawCircle(
                    color = Color(0xFFFFE0D3),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )

                // 3. Hair (Front Bangs / Frame)
                val hairPath = Path().apply {
                    moveTo(centerX - radius, centerY)
                    quadraticTo(centerX - radius * 0.6f, centerY - radius, centerX, centerY - radius * 0.6f)
                    quadraticTo(centerX + radius * 0.6f, centerY - radius, centerX + radius, centerY)
                    lineTo(centerX + radius * 0.7f, centerY - radius * 0.5f)
                    lineTo(centerX, centerY - radius * 0.3f)
                    lineTo(centerX - radius * 0.7f, centerY - radius * 0.5f)
                    close()
                }
                drawPath(
                    path = hairPath,
                    color = Color(0xFFFFB7D5)
                )

                // Hair strands details
                drawCircle(
                    color = Color(0xFFFF9ECB),
                    radius = radius * 0.95f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f)
                )

                // 4. Starry Eyes (Anime Style)
                val leftEyeCenter = Offset(centerX - radius * 0.35f, centerY + radius * 0.05f)
                val rightEyeCenter = Offset(centerX + radius * 0.35f, centerY + radius * 0.05f)
                
                // Eye orbits (Purple)
                drawCircle(color = Color(0xFF4C0F26), radius = radius * 0.18f, center = leftEyeCenter)
                drawCircle(color = Color(0xFF4C0F26), radius = radius * 0.18f, center = rightEyeCenter)
                
                // Star shines (Yellow)
                drawCircle(color = Color(0xFFFFD60A), radius = radius * 0.05f, center = Offset(leftEyeCenter.x + 5f, leftEyeCenter.y + 5f))
                drawCircle(color = Color(0xFFFFD60A), radius = radius * 0.05f, center = Offset(rightEyeCenter.x + 5f, rightEyeCenter.y + 5f))
                
                // White sparkle reflect
                drawCircle(color = Color.White, radius = radius * 0.04f, center = Offset(leftEyeCenter.x - 5f, leftEyeCenter.y - 5f))
                drawCircle(color = Color.White, radius = radius * 0.04f, center = Offset(rightEyeCenter.x - 5f, rightEyeCenter.y - 5f))

                // Cute Blush (Radial Pink)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x77FF7BB5), Color.Transparent),
                        center = Offset(leftEyeCenter.x, leftEyeCenter.y + radius * 0.25f),
                        radius = radius * 0.25f
                    ),
                    radius = radius * 0.25f,
                    center = Offset(leftEyeCenter.x, leftEyeCenter.y + radius * 0.25f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x77FF7BB5), Color.Transparent),
                        center = Offset(rightEyeCenter.x, rightEyeCenter.y + radius * 0.25f),
                        radius = radius * 0.25f
                    ),
                    radius = radius * 0.25f,
                    center = Offset(rightEyeCenter.x, rightEyeCenter.y + radius * 0.25f)
                )

                // 5. Smiling Mouth
                val mouthPath = Path().apply {
                    moveTo(centerX - radius * 0.12f, centerY + radius * 0.35f)
                    quadraticTo(centerX, centerY + radius * 0.45f, centerX + radius * 0.12f, centerY + radius * 0.35f)
                }
                drawPath(
                    path = mouthPath,
                    color = Color(0xFF4C0F26),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )

                // 6. Magic Witch Hat (Pointy wizard hat on top)
                val hatPath = Path().apply {
                    moveTo(centerX - radius * 1.1f, centerY - radius * 0.4f)
                    quadraticTo(centerX, centerY - radius * 0.6f, centerX + radius * 1.1f, centerY - radius * 0.4f)
                    quadraticTo(centerX + radius * 0.6f, centerY - radius * 1.6f, centerX, centerY - radius * 1.8f)
                    quadraticTo(centerX - radius * 0.6f, centerY - radius * 1.6f, centerX - radius * 1.1f, centerY - radius * 0.4f)
                    close()
                }
                drawPath(path = hatPath, color = Color(0xFF2E1A2D))
                
                // Hat ribbon (Pink)
                val ribbonPath = Path().apply {
                    moveTo(centerX - radius * 0.72f, centerY - radius * 0.6f)
                    quadraticTo(centerX, centerY - radius * 0.78f, centerX + radius * 0.72f, centerY - radius * 0.6f)
                    lineTo(centerX + radius * 0.65f, centerY - radius * 0.75f)
                    quadraticTo(centerX, centerY - radius * 0.92f, centerX - radius * 0.65f, centerY - radius * 0.75f)
                    close()
                }
                drawPath(path = ribbonPath, color = Color(0xFFFF7BB5))

                // Yellow Star on the hat
                drawCircle(
                    color = Color(0xFFFFD60A),
                    radius = radius * 0.15f,
                    center = Offset(centerX, centerY - radius * 1.2f)
                )
            }
            AnimeTheme.CYBER -> {
                // CYBER NINJA DRAWING
                
                // 1. Spiky Cyber Hair (Silver-Grey)
                val spikes = Path().apply {
                    moveTo(centerX - radius, centerY)
                    lineTo(centerX - radius * 1.2f, centerY - radius * 0.4f)
                    lineTo(centerX - radius * 0.7f, centerY - radius * 0.5f)
                    lineTo(centerX - radius * 0.8f, centerY - radius * 1.1f)
                    lineTo(centerX - radius * 0.3f, centerY - radius * 0.8f)
                    lineTo(centerX, centerY - radius * 1.4f)
                    lineTo(centerX + radius * 0.3f, centerY - radius * 0.8f)
                    lineTo(centerX + radius * 0.8f, centerY - radius * 1.1f)
                    lineTo(centerX + radius * 0.7f, centerY - radius * 0.5f)
                    lineTo(centerX + radius * 1.2f, centerY - radius * 0.4f)
                    lineTo(centerX + radius, centerY)
                    close()
                }
                drawPath(path = spikes, color = Color(0xFFB0BEC5))

                // 2. Face / Cyber-frame mask
                drawCircle(
                    color = Color(0xFF1E293B), // Cyber metal skin dark tone
                    radius = radius,
                    center = Offset(centerX, centerY)
                )

                // 3. Cyber Headset Ears (Magenta)
                drawRoundRect(
                    color = Color(0xFFFF007F),
                    topLeft = Offset(centerX - radius * 1.2f, centerY - radius * 0.3f),
                    size = Size(radius * 0.25f, radius * 0.6f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color(0xFFFF007F),
                    topLeft = Offset(centerX + radius * 0.95f, centerY - radius * 0.3f),
                    size = Size(radius * 0.25f, radius * 0.6f),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                // 4. Glowing Neon Cyan Visor
                val visorPath = Path().apply {
                    moveTo(centerX - radius * 0.85f, centerY - radius * 0.2f)
                    lineTo(centerX + radius * 0.85f, centerY - radius * 0.2f)
                    lineTo(centerX + radius * 0.7f, centerY + radius * 0.25f)
                    lineTo(centerX - radius * 0.7f, centerY + radius * 0.25f)
                    close()
                }
                drawPath(
                    path = visorPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF00FFCC), Color(0xFF00E5FF)),
                        start = Offset(centerX - radius, centerY),
                        end = Offset(centerX + radius, centerY)
                    )
                )
                
                // Glowing visor horizontal beam line (White)
                drawLine(
                    color = Color.White,
                    start = Offset(centerX - radius * 0.7f, centerY),
                    end = Offset(centerX + radius * 0.7f, centerY),
                    strokeWidth = 6f * glowAlpha,
                    cap = StrokeCap.Round
                )

                // 5. Breathing mouth piece (Ninja grid mask)
                val maskGrid = Path().apply {
                    moveTo(centerX - radius * 0.2f, centerY + radius * 0.5f)
                    lineTo(centerX + radius * 0.2f, centerY + radius * 0.5f)
                    lineTo(centerX + radius * 0.1f, centerY + radius * 0.75f)
                    lineTo(centerX - radius * 0.1f, centerY + radius * 0.75f)
                    close()
                }
                drawPath(path = maskGrid, color = Color(0xFF0A0F1D))
                
                // Mask line marks (Neon Cyan)
                drawLine(
                    color = Color(0xFF00FFCC),
                    start = Offset(centerX, centerY + radius * 0.5f),
                    end = Offset(centerX, centerY + radius * 0.7f),
                    strokeWidth = 3f
                )

                // 6. Holographic background HUD circles
                drawCircle(
                    color = Color(0xFF00FFCC),
                    radius = radius * 1.3f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f * glowAlpha)
                )
            }
            AnimeTheme.KNIGHT -> {
                // SOLAR KNIGHT DRAWING
                
                // 1. Red Plume (Helmet crest feather - dynamic)
                val plumePath = Path().apply {
                    moveTo(centerX, centerY - radius * 0.8f)
                    quadraticTo(centerX - radius * 0.4f, centerY - radius * 1.8f, centerX - radius * 0.8f, centerY - radius * 2.0f)
                    quadraticTo(centerX, centerY - radius * 2.1f, centerX + radius * 0.4f, centerY - radius * 1.5f)
                    quadraticTo(centerX, centerY - radius * 0.9f, centerX, centerY - radius * 0.8f)
                    close()
                }
                drawPath(
                    path = plumePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF453A), Color(0xFFFF9F0A))
                    )
                )

                // 2. Iron Helmet Core
                drawCircle(
                    color = Color(0xFF4E3629), // Warm iron base
                    radius = radius,
                    center = Offset(centerX, centerY)
                )

                // Helmet trim / Gold lining
                drawCircle(
                    color = Color(0xFFFFD60A),
                    radius = radius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 5f)
                )

                // 3. Knight Visor Slit
                val slitPath = Path().apply {
                    moveTo(centerX - radius * 0.75f, centerY - radius * 0.1f)
                    lineTo(centerX + radius * 0.75f, centerY - radius * 0.1f)
                    lineTo(centerX + radius * 0.6f, centerY + radius * 0.15f)
                    lineTo(centerX - radius * 0.6f, centerY + radius * 0.15f)
                    close()
                }
                drawPath(path = slitPath, color = Color(0xFF1E1510))

                // Glowing Golden-orange Warrior Eyes through slit
                drawCircle(
                    color = Color(0xFFFF9F0A),
                    radius = radius * 0.08f,
                    center = Offset(centerX - radius * 0.3f, centerY)
                )
                drawCircle(
                    color = Color(0xFFFF9F0A),
                    radius = radius * 0.08f,
                    center = Offset(centerX + radius * 0.3f, centerY)
                )
                // Eye glint
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.03f,
                    center = Offset(centerX - radius * 0.28f, centerY - 2f)
                )
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.03f,
                    center = Offset(centerX + radius * 0.32f, centerY - 2f)
                )

                // 4. Helmet Noseguard & Mouth plate
                val facePlate = Path().apply {
                    moveTo(centerX - radius * 0.15f, centerY - radius * 0.1f)
                    lineTo(centerX + radius * 0.15f, centerY - radius * 0.1f)
                    lineTo(centerX + radius * 0.4f, centerY + radius * 0.5f)
                    lineTo(centerX, centerY + radius * 0.9f)
                    lineTo(centerX - radius * 0.4f, centerY + radius * 0.5f)
                    close()
                }
                drawPath(path = facePlate, color = Color(0xFF6E503F))
                
                // Golden trim for face plate
                val facePlateTrim = Path().apply {
                    moveTo(centerX, centerY - radius * 0.1f)
                    lineTo(centerX, centerY + radius * 0.9f)
                }
                drawPath(
                    path = facePlateTrim,
                    color = Color(0xFFFFD60A),
                    style = Stroke(width = 4f)
                )

                // 5. Shoulder Armor / Gorget
                val armorPath = Path().apply {
                    moveTo(centerX - radius * 0.8f, centerY + radius * 0.7f)
                    quadraticTo(centerX, centerY + radius * 0.8f, centerX + radius * 0.8f, centerY + radius * 0.7f)
                    lineTo(centerX + radius * 1.1f, h)
                    lineTo(centerX - radius * 1.1f, h)
                    close()
                }
                drawPath(
                    path = armorPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E1F18), Color(0xFF1E1510))
                    )
                )
            }
        }
    }
}
