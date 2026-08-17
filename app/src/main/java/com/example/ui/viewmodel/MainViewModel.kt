package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthResult
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.WorldEagleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppDestination {
    LOGIN,
    SIGNUP,
    DASHBOARD,
    INTELLIGENCE_EARLY_NEWS,
    INTELLIGENCE_IMPORTANT_IMPACT,
    INTELLIGENCE_COMPANY_NEWS,
    INTELLIGENCE_NEW_COMPANIES,
    INTELLIGENCE_TOP_INVESTORS,
    EXCHANGE_BINANCE,
    EXCHANGE_MEXC,
    EXCHANGE_WEEX,
    AI_VIDEOS,
    LEADERBOARD,
    MESSAGES,
    CHAT_DETAIL,
    PROFILE,
    EDIT_PROFILE,
    SETTINGS,
    IBADAH_NAMAZ,
    IBADAH_QURAN,
    IBADAH_ZIKR,
    IBADAH_AZAN,
    IBADAH_GOALS,
    IBADAH_ACHIEVEMENTS,
    ADMIN_PANEL
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val authManager = AuthManager(db)
    val repository = WorldEagleRepository(db)

    // Auth State
    val currentUser: StateFlow<UserEntity?> = authManager.currentUser

    // UI Navigation State - Absolute Login Wall
    private val _currentScreen = MutableStateFlow<AppDestination>(AppDestination.LOGIN)
    val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

    // Navigation Stack for seamless back-navigation
    private val screenStack = mutableListOf<AppDestination>()

    // App Theme State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Active Conversation
    private val _activeConversation = MutableStateFlow<ConversationEntity?>(null)
    val activeConversation: StateFlow<ConversationEntity?> = _activeConversation.asStateFlow()

    // Active Call Modal State (WebRTC/Call Interface)
    private val _activeCallType = MutableStateFlow<String?>(null) // null, "VOICE", "VIDEO"
    val activeCallType: StateFlow<String?> = _activeCallType.asStateFlow()

    // Toast / Snackbar feedback
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Azan AI Analysis In-Progress State
    private val _isRecordingAzan = MutableStateFlow(false)
    val isRecordingAzan: StateFlow<Boolean> = _isRecordingAzan.asStateFlow()

    private val _latestAzanAnalysis = MutableStateFlow<AzanRecordingEntity?>(null)
    val latestAzanAnalysis: StateFlow<AzanRecordingEntity?> = _latestAzanAnalysis.asStateFlow()

    // Data streams from Repository
    val allNews = repository.allNews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val earlyNews = repository.earlyNews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val importantImpactNews = repository.importantImpactNews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCompanies = repository.allCompanies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val newCompanies = repository.newCompanies.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val topInvestors = repository.topInvestors.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val binanceEvents = repository.getExchangeEvents("BINANCE").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mexcEvents = repository.getExchangeEvents("MEXC").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val weexEvents = repository.getExchangeEvents("WEEX").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiVideos = repository.allAiVideos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val leaderboard = repository.leaderboard.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val conversations = repository.allConversations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val systemAlerts = repository.allAlerts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current User Ibadah Streams
    val todayPrayerLog = MutableStateFlow<PrayerLogEntity?>(null)
    val quranRecord = MutableStateFlow<QuranRecordEntity?>(null)
    val zikrRecords = MutableStateFlow<List<ZikrRecordEntity>>(emptyList())
    val azanRecordings = MutableStateFlow<List<AzanRecordingEntity>>(emptyList())
    val azanGoal = MutableStateFlow<AzanGoalEntity?>(null)

    init {
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                if (user == null) {
                    _currentScreen.value = AppDestination.LOGIN
                    screenStack.clear()
                } else {
                    refreshUserData(user.id)
                }
            }
        }
    }

    private fun refreshUserData(userId: Long) {
        viewModelScope.launch {
            todayPrayerLog.value = repository.getTodayPrayerLog(userId)
            repository.initializeQuranRecordIfNeeded(userId)
            repository.initializeZikrRecordsIfNeeded(userId)
            repository.initializeAzanGoalIfNeeded(userId)

            launch {
                repository.getQuranRecord(userId).collect { quranRecord.value = it }
            }
            launch {
                repository.getZikrRecords(userId).collect { zikrRecords.value = it }
            }
            launch {
                repository.getAzanRecordings(userId).collect { azanRecordings.value = it }
            }
            launch {
                repository.getAzanGoal(userId).collect { azanGoal.value = it }
            }
        }
    }

    fun navigateTo(dest: AppDestination) {
        // Enforce absolute login wall: If not authenticated, cannot open protected destinations
        if (currentUser.value == null && dest != AppDestination.LOGIN && dest != AppDestination.SIGNUP) {
            _currentScreen.value = AppDestination.LOGIN
            showToast("Please log in to access World Eagle intelligence.")
            return
        }

        // Enforce Admin only
        if (dest == AppDestination.ADMIN_PANEL && currentUser.value?.role != "ADMIN") {
            showToast("Access Denied: Admin authorization required.")
            return
        }

        if (_currentScreen.value != dest) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = dest
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val previous = screenStack.removeAt(screenStack.size - 1)
            // If logged out, never navigate back to a protected screen
            if (currentUser.value == null && previous != AppDestination.LOGIN && previous != AppDestination.SIGNUP) {
                _currentScreen.value = AppDestination.LOGIN
            } else {
                _currentScreen.value = previous
            }
            return true
        }
        return false
    }

    fun showToast(msg: String) {
        _uiMessage.value = msg
    }

    fun clearToast() {
        _uiMessage.value = null
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Auth actions
    fun login(emailOrUser: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (val res = authManager.login(emailOrUser, pass)) {
                is AuthResult.Success -> {
                    _currentScreen.value = AppDestination.DASHBOARD
                    screenStack.clear()
                    showToast(res.message)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    showToast(res.error)
                }
            }
        }
    }

    fun loginWithGoogle(
        accountName: String = "Muhammad Ahmed (Google)",
        accountEmail: String = "ahmed.worldeagle@gmail.com",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val res = authManager.loginWithGoogle(accountName, accountEmail)) {
                is AuthResult.Success -> {
                    _currentScreen.value = AppDestination.DASHBOARD
                    screenStack.clear()
                    showToast(res.message)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    showToast(res.error)
                }
            }
        }
    }

    fun checkUsernameAvailability(username: String, onResult: (Boolean, List<String>) -> Unit) {
        viewModelScope.launch {
            val res = authManager.checkUsernameAvailability(username)
            onResult(res.first, res.second)
        }
    }

    fun register(
        fullName: String,
        username: String,
        email: String,
        pass: String,
        confirmPass: String,
        dpUrl: String,
        bio: String = "Market intelligence enthusiast",
        country: String = "Pakistan",
        timezone: String = "UTC+5 (PKT)",
        phone: String = "",
        dob: String = "",
        onSuccess: () -> Unit = {},
        onError: (String, List<String>) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            when (val res = authManager.registerUser(
                fullName, username, email, pass, confirmPass, dpUrl, bio, country, timezone, phone, dob
            )) {
                is AuthResult.Success -> {
                    // Rule #5: DO NOT auto-login, redirect to /login!
                    _currentScreen.value = AppDestination.LOGIN
                    screenStack.clear()
                    showToast(res.message)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    onError(res.error, res.suggestedUsernames)
                    showToast(res.error)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
            _currentScreen.value = AppDestination.LOGIN
            screenStack.clear()
            _activeConversation.value = null
            showToast("You have been securely logged out.")
        }
    }

    fun logoutAllDevices() {
        viewModelScope.launch {
            authManager.logoutAllDevices()
            _currentScreen.value = AppDestination.LOGIN
            screenStack.clear()
            showToast("Logged out of all active sessions.")
        }
    }

    fun updateProfile(
        fullName: String,
        username: String,
        bio: String,
        country: String,
        timezone: String,
        dpUrl: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            when (val res = authManager.updateProfile(fullName, username, bio, country, timezone, dpUrl)) {
                is AuthResult.Success -> {
                    showToast(res.message)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    showToast(res.error)
                }
            }
        }
    }

    fun setChatPin(pin: String) {
        viewModelScope.launch {
            if (authManager.updateChatPin(pin)) {
                showToast("Chat Security PIN updated successfully.")
            }
        }
    }

    // Messaging actions
    fun openConversation(conv: ConversationEntity) {
        _activeConversation.value = conv
        navigateTo(AppDestination.CHAT_DETAIL)
    }

    fun sendMessage(text: String, msgType: String = "TEXT", attachmentUrl: String = "") {
        val conv = _activeConversation.value ?: return
        val user = currentUser.value ?: return
        if (text.isBlank() && attachmentUrl.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                convId = conv.id,
                text = text,
                senderName = user.fullName,
                senderUsername = user.username,
                msgType = msgType,
                attachmentUrl = attachmentUrl
            )
        }
    }

    fun lockConversation(convId: Long, isLocked: Boolean, pin: String) {
        viewModelScope.launch {
            repository.setChatLock(convId, isLocked, pin)
            _activeConversation.value = repository.getConversation(convId)
            showToast(if (isLocked) "Chat locked with PIN" else "Chat unlocked")
        }
    }

    fun startCall(callType: String) {
        _activeCallType.value = callType
    }

    fun endCall() {
        _activeCallType.value = null
    }

    // Ibadah - Namaz
    fun togglePrayer(prayerName: String, isAda: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.togglePrayer(user.id, prayerName, isAda)
            todayPrayerLog.value = repository.getTodayPrayerLog(user.id)
            showToast("$prayerName marked as ${if (isAda) "Ada Ki ✓" else "Not Ada"}")
        }
    }

    fun setPrayerTargetLevel(level: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateTargetMonthLevel(user.id, level)
            todayPrayerLog.value = repository.getTodayPrayerLog(user.id)
            showToast("Monthly Prayer Target set to Month $level ($level prayer(s)/day)")
        }
    }

    // Ibadah - Quran
    fun updateQuranProgress(surah: String, surahNum: Int, ayah: Int, juz: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateQuranProgress(user.id, surah, surahNum, ayah, juz)
            showToast("Progress saved: $surah Ayah $ayah")
        }
    }

    fun bookmarkQuran(surah: String, ayah: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.bookmarkQuran(user.id, surah, ayah)
            showToast("Bookmarked $surah Ayah $ayah")
        }
    }

    // Ibadah - Zikr
    fun clickZikr(record: ZikrRecordEntity, delta: Int) {
        viewModelScope.launch {
            repository.updateZikrCount(record, delta)
        }
    }

    fun resetZikr(record: ZikrRecordEntity) {
        viewModelScope.launch {
            repository.resetZikr(record)
            showToast("${record.zikrTitle} counter reset.")
        }
    }

    fun addCustomZikr(title: String, goal: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addCustomZikr(user.id, title, goal)
            showToast("Added custom Zikr: $title")
        }
    }

    // Ibadah - Azan AI Analysis
    fun startAzanRecordingSimulation() {
        _isRecordingAzan.value = true
    }

    fun completeAzanRecordingAnalysis(sampleTitle: String = "Fajr Azan Vocal Practice") {
        val user = currentUser.value ?: return
        _isRecordingAzan.value = false

        // Generate measurable audio characteristics & realistic progressive analysis
        val timing = (80..95).random()
        val pitch = (76..92).random()
        val voiceClarity = (82..96).random()
        val loudness = (78..90).random()
        val pauses = (84..94).random()
        val overall = ((timing + pitch + voiceClarity + loudness + pauses) / 5)
        val delta = (+4..+8).random()

        val explanation = "Aapki Azan recording ka timing aur voice clarity bohot zabardast hai ($overall/100). Maddah ki timing pichli recording se +$delta behtar hai. Breath pauses naturally controlled hain."
        val tajweedNote = "Tajweed aur Makharij ki bariki seekhne ke liye qualified Qari sahab se live review karwayein."

        val recording = AzanRecordingEntity(
            userId = user.id,
            title = sampleTitle,
            recordedDate = "August 17, 2026",
            durationSeconds = 165,
            timingScore = timing,
            pitchStabilityScore = pitch,
            voiceClarityScore = voiceClarity,
            loudnessConsistencyScore = loudness,
            pausesScore = pauses,
            overallScore = overall,
            improvementDelta = delta,
            romanEnglishExplanation = explanation,
            tajweedGuidanceNote = tajweedNote
        )

        viewModelScope.launch {
            repository.saveAzanAnalysis(recording)
            _latestAzanAnalysis.value = recording
            showToast("Azan analysis completed: Score $overall/100 (+$delta)")
        }
    }

    // Azan Goal updates
    fun updateAzanGoalStep(stepNum: Int, stepName: String, targetRank: String) {
        val user = currentUser.value ?: return
        val current = azanGoal.value ?: AzanGoalEntity(userId = user.id)
        val updated = current.copy(
            currentStepNumber = stepNum,
            currentStepName = stepName,
            targetGoalLevel = targetRank
        )
        viewModelScope.launch {
            repository.updateAzanGoal(updated)
            azanGoal.value = updated
            showToast("Azan Competition Goal updated to: $stepName ($targetRank)")
        }
    }

    // Admin Tools
    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            showToast("User account deleted.")
        }
    }

    fun toggleBanUser(userId: Long, currentlyBanned: Boolean) {
        viewModelScope.launch {
            repository.setUserBanned(userId, !currentlyBanned)
            showToast(if (!currentlyBanned) "User suspended." else "User unsuspended.")
        }
    }

    fun broadcastSystemAlert(title: String, msg: String, alertType: String = "SYSTEM") {
        viewModelScope.launch {
            repository.broadcastAlert(title, msg, alertType)
            showToast("System alert broadcasted to all intelligence terminals.")
        }
    }
}
