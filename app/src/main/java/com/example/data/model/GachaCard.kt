package com.example.data.model

import androidx.compose.ui.graphics.Color

data class GachaCard(
    val id: String,
    val name: String,
    val characterTheme: String, // "Sakura", "Cyber", "Knight", or "Legendary"
    val rarity: String,         // "R", "SR", "SSR", "UR"
    val subtitle: String,
    val hp: Int,
    val attack: Int,
    val emoji: String,
    val desc: String,
    val imageUrl: String? = null
) {
    val rarityColor: Color
        get() = when (rarity) {
            "UR" -> Color(0xFFFF3B30)   // Red
            "SSR" -> Color(0xFFFFD60A)  // Gold
            "SR" -> Color(0xFFBF5AF2)   // Purple
            else -> Color(0xFF64D2FF)    // Sky Blue
        }
}

object GachaDatabase {
    val cards = listOf(
        // Sakura
        GachaCard("sakura_r", "Chibi Sakura: Tea Time", "Sakura", "R", "Earl Grey Sorcery", 450, 110, "🍵", "Enjoying sweet afternoon treats and recovering magical reserves."),
        GachaCard("sakura_sr", "Sakura: Star Cadet", "Sakura", "SR", "Apprentice Wizard", 950, 280, "⭐", "Spelling the skies with small stars under academic supervision."),
        GachaCard("sakura_ssr", "Sakura: Cherry Blossom Spell", "Sakura", "SSR", "First Bloom Empress", 2400, 720, "🌸", "Summoning protective pink boundary barrier shield grids in blossom season."),
        GachaCard("sakura_ur", "Sakura: Cosmic Mana Empress", "Sakura", "UR", "Galactic Celestial Deity", 6000, 1850, "✨", "Transcending space-time dimensions to sync Senpai's digital schedule completely."),

        // Cyber
        GachaCard("cyber_r", "Kaito: Rookie Runner", "Cyber", "R", "Kernel Compiler", 380, 140, "💾", "Compiling local system code files and testing standard terminal firewalls."),
        GachaCard("cyber_sr", "Kaito: Cyber Katana Clash", "Cyber", "SR", "Grid Slicer", 880, 310, "⚔️", "Defending memory sectors from malware using high-frequency energy blades."),
        GachaCard("cyber_ssr", "Kaito: Netrunner Architect", "Cyber", "SSR", "Mainframe Phantom", 2100, 810, "💻", "Overriding security networks to bypass constraints and fetch developer logs."),
        GachaCard("cyber_ur", "Kaito: Singularity Spectre", "Cyber", "UR", "Net-Transcended Ghost", 5200, 2100, "⚡", "Becoming pure data flow inside the global net grid. Immune to firewall blocks."),

        // Knight
        GachaCard("knight_r", "Eldrin: Shield Training", "Knight", "R", "Iron Squire", 600, 80, "🛡️", "Practicing blocking maneuvers against mock dragon fire arrows."),
        GachaCard("knight_sr", "Eldrin: Dragonslayer Stand", "Knight", "SR", "Vanguard Knight", 1200, 220, "⚔️", "Standing firm in hot volcanic ash, protecting civilians from the dragon nest."),
        GachaCard("knight_ssr", "Eldrin: Solar Eruption", "Knight", "SSR", "Hearth Fire Marshal", 2900, 620, "🔥", "Imbued with the solar corona crest, charging the runic sword with solar flares."),
        GachaCard("knight_ur", "Eldrin: Champion of Ash & Gold", "Knight", "UR", "Legendary Sun Phoenix", 7500, 1600, "✴️", "Ascended dragon lord with majestic fire wings, guiding all souls with solar glory."),

        // Legendary HD Anime Guest Stars (Naruto, Luffy, Goku, Gojo)
        GachaCard(
            id = "naruto_ur",
            name = "Naruto: Sage of Six Paths",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The Nine-Tails Sage Shroud",
            hp = 9500,
            attack = 2600,
            emoji = "🦊",
            desc = "A transcendent state of combat where Naruto harmonizes Sage Jutsu with Kurama's golden shroud. Master of the ultimate Rasenshuriken.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/15/Naruto-Uzumaki-PNG-Cutout.png"
        ),
        GachaCard(
            id = "luffy_ur",
            name = "Luffy: Gear 5 (Joyboy)",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The Warrior of Liberation",
            hp = 9200,
            attack = 2500,
            emoji = "👒",
            desc = "Luffy's peak form, representing the white-haired sun god Joyboy. Possesses infinite physical flexibility and fights with joyous, cartoonish freedom.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/15/Monkey-D-Luffy-One-Piece-PNG.png"
        ),
        GachaCard(
            id = "goku_ur",
            name = "Goku: Mastered Ultra Instinct",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "State of Absolute Autonomy",
            hp = 9999,
            attack = 2900,
            emoji = "🌀",
            desc = "An ultra-divine state that bypasses the nervous system entirely, allowing Goku's body to move, block, and strike instinctively with white cosmic flame.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/15/Goku-Ultra-Instinct-PNG-Photo.png"
        ),
        GachaCard(
            id = "gojo_ur",
            name = "Satoru Gojo: Hollow Purple",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The Limitless Honored One",
            hp = 8900,
            attack = 2800,
            emoji = "👁️",
            desc = "The strongest sorcerer of modern times. Manipulates infinity to manifest red repulsion and blue attraction, synthesizing them into the ultimate Purple void.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/13/Gojo-Satoru-Jujutsu-Kaisen-PNG.png"
        ),
        GachaCard(
            id = "gokublack_ur",
            name = "Goku Black: Super Saiyan Rosé",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The Beautiful Wrath of a God",
            hp = 9700,
            attack = 2850,
            emoji = "🌹",
            desc = "Zamasu's supreme state combining divine soul energy with mortal Saiyan potential. Manifests a gorgeous dark-pink fiery blade and absolute power.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/15/Goku-Black-PNG-Clipart.png"
        ),
        GachaCard(
            id = "kaneki_ur",
            name = "Ken Kaneki: Centipede Kagune",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The One-Eyed Ghoul King",
            hp = 9100,
            attack = 2750,
            emoji = "👺",
            desc = "An awakened half-ghoul with white hair who fully accepts his ghoul nature. Summons four crimson kagune tentacles with immense combat velocity.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/5/Ken-Kaneki-PNG-File.png"
        ),
        GachaCard(
            id = "ryuk_ur",
            name = "Ryuk & Light: Shinigami Pact",
            characterTheme = "Legendary",
            rarity = "UR",
            subtitle = "The Death Note Duo",
            hp = 8500,
            attack = 2400,
            emoji = "🍎",
            desc = "The legendary high-school mastermind who writes names in the black ledger, watched over by Ryuk, a shinigami who loves juicy human apples.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/13/Death-Note-Ryuk-PNG.png"
        ),
        GachaCard(
            id = "mikey_ssr",
            name = "Manjiro Sano: Invincible Mikey",
            characterTheme = "Legendary",
            rarity = "SSR",
            subtitle = "Tokyo Manji Gang Leader",
            hp = 5800,
            attack = 1950,
            emoji = "🏍️",
            desc = "The undisputed street combat legend of Tokyo. His peerless high-kick can knock out any adversary in a single frame. Driven by dark impulses.",
            imageUrl = "https://www.pngall.com/wp-content/uploads/15/Tokyo-Revengers-PNG-Image.png"
        ),
        GachaCard(
            id = "ayanokoji_ssr",
            name = "Ayanokoji: Quiet Mastermind",
            characterTheme = "Legendary",
            rarity = "SSR",
            subtitle = "Classroom of the Elite Genius",
            hp = 6200,
            attack = 1800,
            emoji = "♟️",
            desc = "A mysterious student from the White Room. Extremely analytical, viewing all humans as tools. Secretly orchestrates every classroom strategy from the shadows.",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/5c378546b27e390c58e99b24/1586558444498-A39BODRDKIJSKJSWMTQW/Ayanokoji.png"
        )
    )

    fun rollCard(): GachaCard {
        val rand = (1..100).random()
        val rarity = when {
            rand <= 3 -> "UR"
            rand <= 15 -> "SSR"
            rand <= 50 -> "SR"
            else -> "R"
        }
        val pool = cards.filter { it.rarity == rarity }
        return pool.random()
    }
}
