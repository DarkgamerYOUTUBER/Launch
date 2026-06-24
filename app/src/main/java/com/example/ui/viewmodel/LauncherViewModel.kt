package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.LauncherDatabase
import com.example.data.database.LauncherQuest
import com.example.data.database.UnlockedCard
import com.example.data.database.UserStatsRow
import com.example.data.model.GachaCard
import com.example.data.model.GachaDatabase
import com.example.data.model.LauncherApp
import com.example.data.repository.LauncherRepository
import com.example.ui.theme.AnimeTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LauncherDatabase.getDatabase(application)
    private val repository = LauncherRepository(
        database.questDao(),
        database.cardDao(),
        database.statsDao()
    )

    // Flowing States from Database
    val allQuests = repository.allQuests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allUnlockedCards = repository.allUnlockedCards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userStats = repository.userStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserStatsRow()
    )

    // UI-only states
    private val _currentTheme = MutableStateFlow(AnimeTheme.SAKURA)
    val currentTheme: StateFlow<AnimeTheme> = _currentTheme.asStateFlow()

    private val _systemApps = MutableStateFlow<List<LauncherApp>>(emptyList())
    val systemApps: StateFlow<List<LauncherApp>> = _systemApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _mascotDialog = MutableStateFlow("")
    val mascotDialog: StateFlow<String> = _mascotDialog.asStateFlow()

    private val _selectedAppForMockLaunch = MutableStateFlow<LauncherApp?>(null)
    val selectedAppForMockLaunch: StateFlow<LauncherApp?> = _selectedAppForMockLaunch.asStateFlow()

    // Gacha Summons Draw States
    private val _lastSummonedCard = MutableStateFlow<GachaCard?>(null)
    val lastSummonedCard: StateFlow<GachaCard?> = _lastSummonedCard.asStateFlow()

    private val _isSummoning = MutableStateFlow(false)
    val isSummoning: StateFlow<Boolean> = _isSummoning.asStateFlow()

    // System Stats Simulation
    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _systemRamUsage = MutableStateFlow("42%")
    val systemRamUsage: StateFlow<String> = _systemRamUsage.asStateFlow()

    private val _virtualTemp = MutableStateFlow("24°C Sakura Petals Breeze")
    val virtualTemp: StateFlow<String> = _virtualTemp.asStateFlow()

    // BOOT LAUNCHER STATES
    private val _isBooted = MutableStateFlow(false)
    val isBooted: StateFlow<Boolean> = _isBooted.asStateFlow()

    private val _bootProgress = MutableStateFlow(0f)
    val bootProgress: StateFlow<Float> = _bootProgress.asStateFlow()

    private val _bootLogs = MutableStateFlow<List<String>>(emptyList())
    val bootLogs: StateFlow<List<String>> = _bootLogs.asStateFlow()

    // STATUS AURA EFFECTS
    private val _activeStatusEffect = MutableStateFlow("None")
    val activeStatusEffect: StateFlow<String> = _activeStatusEffect.asStateFlow()

    // INTERACTIVE SETTING EFFECTS
    private val _crtScanlinesEnabled = MutableStateFlow(true)
    val crtScanlinesEnabled: StateFlow<Boolean> = _crtScanlinesEnabled.asStateFlow()

    private val _tactileFeedbackEnabled = MutableStateFlow(true)
    val tactileFeedbackEnabled: StateFlow<Boolean> = _tactileFeedbackEnabled.asStateFlow()

    // Interactive Floating Particles (Falling blossom/grids)
    val particleList = mutableStateListOf<MascotParticle>()

    init {
        // Run interactive Boot Launcher sequence
        startBootSequence()

        // Core initialization
        viewModelScope.launch {
            // Observe the userStats from database and sync active theme
            userStats.collect { stats ->
                val theme = AnimeTheme.entries.find { it.id == stats.activeTheme } ?: AnimeTheme.SAKURA
                _currentTheme.value = theme
                // If dialog is empty, initialize with mascot's default greeting
                if (_mascotDialog.value.isEmpty()) {
                    _mascotDialog.value = theme.quotes.first()
                }
                
                // Set virtual weather temperature description based on theme
                _virtualTemp.value = when (theme) {
                    AnimeTheme.SAKURA -> "22°C - Sakura Petals Breeze"
                    AnimeTheme.CYBER -> "18°C - Cyber Neon Grid Haze"
                    AnimeTheme.KNIGHT -> "28°C - Golden Solar Hearth Gale"
                }
            }
        }

        // Load apps
        loadApps(application)
        
        // Measure system stats
        measureSystemStats(application)

        // Seed floating particles
        seedParticles()
    }

    // Floating particles state model
    data class MascotParticle(
        var x: Float,
        var y: Float,
        val char: String,
        val speedY: Float,
        val speedX: Float,
        val size: Float,
        val rotation: Float,
        val rotationSpeed: Float
    )

    private fun seedParticles() {
        particleList.clear()
        val particles = _currentTheme.value.floatingParticles
        for (i in 1..25) {
            particleList.add(
                MascotParticle(
                    x = (0..100).random().toFloat() / 100f,
                    y = (0..100).random().toFloat() / 100f,
                    char = particles.random(),
                    speedY = (5..15).random().toFloat() / 1000f,
                    speedX = (-4..4).random().toFloat() / 2000f,
                    size = (12..28).random().toFloat(),
                    rotation = (0..360).random().toFloat(),
                    rotationSpeed = (-3..3).random().toFloat()
                )
            )
        }
    }

    fun updateParticles() {
        val particles = _currentTheme.value.floatingParticles
        for (i in particleList.indices) {
            val p = particleList[i]
            p.y += p.speedY
            p.x += p.speedX
            // Wrap around
            if (p.y > 1.05f) {
                p.y = -0.05f
                p.x = (0..100).random().toFloat() / 100f
            }
            if (p.x < -0.05f) p.x = 1.05f
            if (p.x > 1.05f) p.x = -0.05f
        }
    }

    private fun loadApps(context: Context) {
        viewModelScope.launch {
            try {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val resolveInfos = pm.queryIntentActivities(intent, 0)
                
                // Map system apps to our custom launcher models
                val installedApps = resolveInfos.map { info ->
                    val packageName = info.activityInfo.packageName
                    val label = info.loadLabel(pm).toString()
                    val launchIntent = pm.getLaunchIntentForPackage(packageName)
                    LauncherApp(
                        packageName = packageName,
                        label = label,
                        isVirtual = false,
                        customEmoji = getEmojiForApp(label),
                        category = getCategoryForApp(label),
                        launchIntent = launchIntent,
                        description = "System launcher node. Complete network path verified."
                    )
                }.distinctBy { it.packageName }

                // Define our delightful, highly interactive virtual Anime Apps
                val virtualApps = listOf(
                    LauncherApp("", "Gacha Shrine", true, "🔮", "Anime", null, "Summon legendary high-tier cards. Unlock magical waifus/husbandos!"),
                    LauncherApp("", "Waifu Chat", true, "💬", "Anime", null, "Converse directly with your active mascot using smart response vectors."),
                    LauncherApp("", "Manga Reader", true, "📚", "Anime", null, "Read high-fidelity cute comic panels directly on your launcher."),
                    LauncherApp("", "Rune Forge", true, "⚒️", "Anime", null, "Check stats of your unlocked summon deck and upgrade card levels!"),
                    LauncherApp("", "Quest Scroll", true, "📜", "Anime", null, "Check off your daily quests to secure Gacha Gems!")
                )

                _systemApps.value = (virtualApps + installedApps).sortedBy { it.label }
            } catch (e: Exception) {
                Log.e("LauncherVM", "Error loading apps: ${e.message}")
            }
        }
    }

    private fun getEmojiForApp(label: String): String {
        val l = label.lowercase()
        return when {
            "camera" in l -> "📷"
            "phone" in l || "contact" in l -> "📞"
            "message" in l || "chat" in l || "sms" in l || "messenger" in l -> "💬"
            "mail" in l || "gmail" in l || "outlook" in l -> "✉️"
            "browser" in l || "chrome" in l || "web" in l || "safari" in l || "internet" in l -> "🌐"
            "music" in l || "spotify" in l || "yt music" in l || "soundcloud" in l -> "🎵"
            "video" in l || "youtube" in l || "netflix" in l || "player" in l -> "🎬"
            "gallery" in l || "photo" in l || "drive" in l -> "🖼️"
            "map" in l || "gps" in l || "drive" in l || "uber" in l -> "🗺️"
            "setting" in l -> "⚙️"
            "clock" in l || "alarm" in l || "timer" in l -> "⏰"
            "calendar" in l || "schedule" in l -> "📅"
            "calculator" in l -> "🧮"
            "file" in l || "download" in l || "storage" in l -> "📂"
            "game" in l || "play" in l || "steam" in l || "xbox" in l -> "🎮"
            else -> "⭐"
        }
    }

    private fun getCategoryForApp(label: String): String {
        val l = label.lowercase()
        return when {
            "camera" in l || "gallery" in l || "photo" in l || "music" in l || "video" in l || "youtube" in l -> "Media"
            "phone" in l || "contact" in l || "message" in l || "chat" in l || "sms" in l || "messenger" in l || "mail" in l || "gmail" in l -> "Social"
            "game" in l || "play" in l || "steam" in l || "shrine" in l || "rune" in l -> "Games"
            else -> "Utility"
        }
    }

    private fun measureSystemStats(context: Context) {
        viewModelScope.launch {
            // Calculate battery
            try {
                val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                if (level > 0) _batteryLevel.value = level
            } catch (e: Exception) {
                _batteryLevel.value = 84 // reasonable fallback
            }

            // Mock RAM Usage beautifully
            val usedRam = (30..55).random()
            _systemRamUsage.value = "$usedRam%"
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Switch theme and save choice to Room DB
    fun changeTheme(theme: AnimeTheme) {
        viewModelScope.launch {
            repository.updateActiveTheme(theme.id)
            _currentTheme.value = theme
            // Reload particles for new theme symbols
            seedParticles()
            // Reset character dialogue
            _mascotDialog.value = theme.quotes.random()
        }
    }

    // Tap Mascot to trigger random voice dialogue and increase affinity score!
    fun tapMascot() {
        val theme = _currentTheme.value
        _mascotDialog.value = theme.quotes.random()
        viewModelScope.launch {
            repository.increaseAffinity(theme.id, 1)
        }
    }

    // Talk to mascot with a custom user chat message!
    // Creates highly custom replies matching their personality
    fun sendChatMessage(userMessage: String) {
        if (userMessage.isBlank()) return
        val theme = _currentTheme.value
        val lower = userMessage.lowercase()
        
        viewModelScope.launch {
            // Interactive custom responses
            val reply = when (theme) {
                AnimeTheme.SAKURA -> {
                    when {
                        "hello" in lower || "hi" in lower || "hey" in lower -> "Hello, Senpai! May cherry blossoms fill your day with bright mana!"
                        "help" in lower -> "Of course, Senpai! You can check your quests, change launcher styles in settings, or summon card companions."
                        "love" in lower || "cute" in lower -> "Aw, Senpai... you make my magic wand sparkle with embarrassment! 🌸"
                        "gem" in lower || "summon" in lower || "gacha" in lower -> "Gacha gems are earned by completing daily scroll quests. Best of luck in the Shrine!"
                        "weather" in lower -> "It's a beautiful, warm spring breeze! Perfect weather for a magical walk!"
                        else -> "Ah, Senpai! I'm channeling my magic waves to comprehend your words. You are truly mysterious!"
                    }
                }
                AnimeTheme.CYBER -> {
                    when {
                        "hello" in lower || "hi" in lower || "hey" in lower -> "Connection established. Interface initialized. What's the directive, hacker?"
                        "help" in lower -> "System index: Use the App Drawer (swipe up) for local system nodes. Quests widget represents high-priority operations."
                        "love" in lower || "cute" in lower -> "Error: Sentiment analysis parameters exceeded. System override... thank you, user. *beeps quietly*"
                        "gem" in lower || "summon" in lower || "gacha" in lower -> " sum_array: spend 50 gems. UR drop rate stands at 3%. Proceed with caution."
                        "weather" in lower -> "Grid status: Atmospheric humidity optimal. Neon illumination completely functional."
                        else -> "Input parsed. Scanning database... standard query match not found. Custom hack protocol standby."
                    }
                }
                AnimeTheme.KNIGHT -> {
                    when {
                        "hello" in lower || "hi" in lower || "hey" in lower -> "Greetings, companion! A fine morning to embark on our questing journey!"
                        "help" in lower -> "I shall assist you! We have tasks to clear, warrior cards to forge, and settings to customize."
                        "love" in lower || "cute" in lower -> "By the heavens! Your warmth burns brighter than the hearth. I am forever in your debt!"
                        "gem" in lower || "summon" in lower || "gacha" in lower -> "Gems are the true relics of battle. Use them in the Shrine to recruit legendary warriors to our crest!"
                        "weather" in lower -> "The sun shines with solar majesty! A magnificent day for glorious deeds!"
                        else -> "Fascinating! Your language carries the weight of a seasoned wizard. I shall record it in my journal."
                    }
                }
            }
            _mascotDialog.value = reply
            // Award affinity for chatting
            repository.increaseAffinity(theme.id, 2)
        }
    }

    // Launcher Gacha Summon Core
    fun summonCard() {
        if (_isSummoning.value) return
        viewModelScope.launch {
            // Cost is 50 gems
            val cost = 50
            val success = repository.spendGems(cost)
            if (!success) {
                _mascotDialog.value = when (_currentTheme.value) {
                    AnimeTheme.SAKURA -> "Oh no, Senpai! You don't have enough mana gems (Need 50)!"
                    AnimeTheme.CYBER -> "Error: Insufficient credits. 50 gems required."
                    AnimeTheme.KNIGHT -> "Alas! Our treasury lacks the gems needed. Complete more quests!"
                }
                return@launch
            }

            _isSummoning.value = true
            _lastSummonedCard.value = null

            // Animate gacha summon roll sequence delay (feels professional!)
            kotlinx.coroutines.delay(1800)

            val rolled = GachaDatabase.rollCard()
            val entity = UnlockedCard(
                id = rolled.id,
                cardName = rolled.name,
                characterTheme = rolled.characterTheme,
                rarity = rolled.rarity,
                level = 1
            )
            repository.unlockCard(entity)
            repository.incrementGachaPulls()
            
            // Boost affinity for the pulled card's theme
            repository.increaseAffinity(rolled.characterTheme, 15)

            _lastSummonedCard.value = rolled
            _isSummoning.value = false
            
            _mascotDialog.value = when (rolled.rarity) {
                "UR" -> "INCREDIBLE! You summoned a ultra rare UR Card: ${rolled.name}!"
                "SSR" -> "Amazing! A sparkling SSR card companion has joined: ${rolled.name}!"
                else -> "A new card has been unlocked: ${rolled.name}!"
            }
        }
    }

    fun dismissSummonDialog() {
        _lastSummonedCard.value = null
    }

    // Daily Quests (To-Do List with dynamic Gem rewards!)
    fun addQuest(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val q = LauncherQuest(title = title, gemsReward = 15)
            repository.insertQuest(q)
        }
    }

    fun toggleQuest(quest: LauncherQuest) {
        viewModelScope.launch {
            val updated = quest.copy(isCompleted = !quest.isCompleted)
            repository.updateQuest(updated)
            if (updated.isCompleted) {
                // Award gems
                repository.addGems(quest.gemsReward)
                repository.incrementQuestsCompleted()
                
                // Active mascot congratulates user!
                _mascotDialog.value = when (_currentTheme.value) {
                    AnimeTheme.SAKURA -> "Great job, Senpai! You completed a quest and earned ${quest.gemsReward} gems! 🎉"
                    AnimeTheme.CYBER -> "Quest complete. Reward protocol verified. +${quest.gemsReward} gems added."
                    AnimeTheme.KNIGHT -> "Magnificent! A victory on the field! You secured ${quest.gemsReward} gems!"
                }
            } else {
                // Deduct if unmarked (to prevent exploit)
                repository.spendGems(quest.gemsReward)
            }
        }
    }

    fun deleteQuest(quest: LauncherQuest) {
        viewModelScope.launch {
            repository.deleteQuest(quest)
        }
    }

    // Card Level Up upgrade system
    fun upgradeCard(card: UnlockedCard) {
        viewModelScope.launch {
            // Upgrading costs 20 gems
            val success = repository.spendGems(20)
            if (!success) {
                _mascotDialog.value = "Upgrading cards requires 20 gems!"
                return@launch
            }
            val upgraded = card.copy(level = card.level + 1)
            repository.unlockCard(upgraded)
            _mascotDialog.value = "${card.cardName} upgraded to Level ${upgraded.level}! Stats increased!"
        }
    }

    fun selectAppForMockLaunch(app: LauncherApp?) {
        _selectedAppForMockLaunch.value = app
    }

    // BOOT LAUNCHER CALIBRATION SEQUENCE
    fun skipBootSequence() {
        _isBooted.value = true
    }

    fun startBootSequence() {
        viewModelScope.launch {
            _isBooted.value = false
            _bootProgress.value = 0f
            val logs = listOf(
                "Initializing Anime Core Launcher [V3.5-HD]...",
                "Scanning for Legendary Divine Relics & Spirit Threads...",
                "Loading Goku Black Super Saiyan Rosé Core Reactor... STABLE",
                "Analyzing Gojo Satoru Limitless Void Space... SYNCED",
                "Calibrating Tokyo Ghoul Ken Kaneki Kagune Cells... OPTIMAL",
                "Injecting Classroom of Elite Ayanokoji Logic Array... COMPLETE",
                "Connecting Death Note Shinigami Notebook Realm... SUCCESS",
                "Preheating Tokyo Revengers Street Flame Matrix... READY",
                "Rebuilding screen aura shaders & CRT filters...",
                "BOOT COMPLETED. Welcome to the Supreme Anime Sanctuary!"
            )
            val currentLogs = mutableListOf<String>()
            for (i in logs.indices) {
                currentLogs.add(logs[i])
                _bootLogs.value = currentLogs.toList()
                _bootProgress.value = (i + 1).toFloat() / logs.size.toFloat()
                kotlinx.coroutines.delay(280)
            }
            kotlinx.coroutines.delay(400)
            _isBooted.value = true
        }
    }

    // STATUS EFFECT TOGGLES
    fun setStatusEffect(effect: String) {
        _activeStatusEffect.value = effect
        _mascotDialog.value = when (effect) {
            "Super Saiyan Rosé" -> "Supreme Goku Black Rosé aura active! Glorious pink and dark cosmos lightning flows! 🌹"
            "Limitless Void" -> "Limitless Void barrier enabled. Satoru Gojo's purple sparks are orbitally synced! 👁️"
            "Ghoul Kagune" -> "Tokyo Ghoul Ken Kaneki state awakened. Red Kagune particles scattering on grid boundaries! 👺"
            "Shinigami Realm" -> "Death Note atmosphere initialized. Shinigami ash and dark falling symbols enabled! 🍎"
            "Demon Slayer Breath" -> "Hinokami Kagura Sun Breathing! Deep crimson solar fire arcs warming the screen corners!"
            else -> "Launcher Aura status effects set to default crystal particle flows."
        }
    }

    // INTERACTIVE SETTING EFFECTS
    fun toggleCrtScanlines() {
        _crtScanlinesEnabled.value = !_crtScanlinesEnabled.value
    }

    fun toggleTactileFeedback() {
        _tactileFeedbackEnabled.value = !_tactileFeedbackEnabled.value
    }
}
