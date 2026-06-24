package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AnimeTheme(
    val id: String,
    val displayName: String,
    val mascotName: String,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBackground: Color,
    val border: Color,
    val floatingParticles: List<String>,
    val quotes: List<String>
) {
    SAKURA(
        id = "Sakura",
        displayName = "Sakura Mage",
        mascotName = "Sakura",
        primary = Color(0xFFFFB7D5),       // Light Magical Pink
        onPrimary = Color(0xFF4C0F26),
        secondary = Color(0xFFE8AEFF),     // Pastel Orchid Purple
        background = Color(0xFF1F111E),    // Twilight Lavender Night
        surface = Color(0xFF2E1A2D),       // Deep Amethyst Surface
        accent = Color(0xFFFF7BB5),        // Neon Star Crimson
        textPrimary = Color(0xFFFFF0F5),   // Lavender Blush Text
        textSecondary = Color(0xFFD4B2D1),  // Soft Mauve Text
        cardBackground = Color(0x22FFA6D2), // Semi-transparent Magical Glow Card
        border = Color(0xFFFF9ECA),        // Soft Pink Border
        floatingParticles = listOf("🌸", "✨", "🌸", "⭐", "💖"),
        quotes = listOf(
            "Welcome back, Senpai! Ready to cast some magic today?",
            "Your launcher efficiency has increased by 150%!",
            "I checked the mana flow, everything is working flawlessly!",
            "Need to organize your quests? Just write them in my Spell Book!",
            "Did you summon today? The stars show immense fortune!",
            "Remember to hydrate, Senpai! Magical girls need energy too!"
        )
    ),
    CYBER(
        id = "Cyber",
        displayName = "Cyber Ninja",
        mascotName = "Kaito",
        primary = Color(0xFF00FFCC),       // Neon Cyan
        onPrimary = Color(0xFF00332B),
        secondary = Color(0xFFFF007F),     // Hot Magenta
        background = Color(0xFF0A0F1D),    // Deep Space Netrunner Black
        surface = Color(0xFF121B2F),       // Hacker Grid Navy
        accent = Color(0xFF00E5FF),        // Electric Blue
        textPrimary = Color(0xFFE0F7FA),   // Bright Grid Cyan Text
        textSecondary = Color(0xFF80DEEA),  // Terminal Subtext
        cardBackground = Color(0x1A00FFCC), // Translucent Neon Grid Card
        border = Color(0xFF00FFCC),        // Neon Cyan Stroke
        floatingParticles = listOf("⚡", "0", "1", "💾", "💻", "✨"),
        quotes = listOf(
            "System online. Core temperature optimal. Standing by.",
            "Analyzing net connections... firewall completely secure.",
            "CPU load optimized. We are undetected, Netrunner.",
            "I logged a new quest on the terminal. Hack complete.",
            "Want to unlock rare database files? Roll the summon array!",
            "Information is power. Keep your daily quests updated."
        )
    ),
    KNIGHT(
        id = "Knight",
        displayName = "Solar Knight",
        mascotName = "Eldrin",
        primary = Color(0xFFFF9F0A),       // Solar Orange/Amber
        onPrimary = Color(0xFF3D2000),
        secondary = Color(0xFFFFD60A),     // Antique Gold
        background = Color(0xFF1E1510),    // Ash and Embers Charcoal
        surface = Color(0xFF2E1F18),       // Runestone Brown
        accent = Color(0xFFFF453A),        // Crimson Fire Accent
        textPrimary = Color(0xFFFFF2E6),   // Hearth Amber Text
        textSecondary = Color(0xFFD9BFB0),  // Roasted Earth Subtext
        cardBackground = Color(0x22FF9F0A), // Warm Hearth Fire Glow Card
        border = Color(0xFFFFB347),        // Rune Gold Border
        floatingParticles = listOf("🔥", "⚔️", "⭐", "✴️", "🛡️"),
        quotes = listOf(
            "Hail, brave Adventurer! Our path is lined with honor today.",
            "A noble heart never fears a busy schedule. Let us battle the quests!",
            "Your sword is sharp and your widgets are ready.",
            "The gacha portal holds sacred relic cards. Pull when you are ready!",
            "I shall stand guard on your home screen. No bugs shall pass!",
            "A true hero starts their day by clearing the quest log!"
        )
    )
}
