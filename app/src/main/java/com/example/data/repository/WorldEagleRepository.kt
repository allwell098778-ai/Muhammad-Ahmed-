package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WorldEagleRepository(private val db: AppDatabase) {

    // News
    val allNews: Flow<List<NewsItemEntity>> = db.newsDao().getAllNews()
    val earlyNews: Flow<List<NewsItemEntity>> = db.newsDao().getEarlyNews()
    val importantImpactNews: Flow<List<NewsItemEntity>> = db.newsDao().getImportantImpactNews()

    suspend fun insertNewsItem(news: NewsItemEntity): Long = withContext(Dispatchers.IO) {
        db.newsDao().insertSingleNews(news)
    }

    // Companies & Startups
    val allCompanies: Flow<List<CompanyEntity>> = db.companyDao().getAllCompanies()
    val newCompanies: Flow<List<NewCompanyEntity>> = db.companyDao().getAllNewCompanies()

    // Investors
    val topInvestors: Flow<List<InvestorEntity>> = db.investorDao().getTop20Investors()

    // Exchanges
    fun getExchangeEvents(exchange: String): Flow<List<ExchangeEventEntity>> =
        db.exchangeDao().getExchangeEvents(exchange)

    val allExchangeEvents: Flow<List<ExchangeEventEntity>> = db.exchangeDao().getAllExchangeEvents()

    // AI Videos
    val allAiVideos: Flow<List<AiVideoEntity>> = db.aiVideoDao().getAllVideos()

    // Leaderboard
    val leaderboard: Flow<List<LeaderboardUser>> = db.leaderboardDao().getLeaderboard()

    // Messaging
    val allConversations: Flow<List<ConversationEntity>> = db.messagingDao().getAllConversations()

    fun getMessages(convId: Long): Flow<List<MessageEntity>> =
        db.messagingDao().getMessagesForConversation(convId)

    suspend fun sendMessage(convId: Long, text: String, senderName: String, senderUsername: String, msgType: String = "TEXT", attachmentUrl: String = ""): Long =
        withContext(Dispatchers.IO) {
            val sdf = SimpleDateFormat("HH:mm PKT", Locale.getDefault())
            val time = sdf.format(Date())
            val msg = MessageEntity(
                conversationId = convId,
                senderUsername = senderUsername,
                senderFullName = senderName,
                text = text,
                messageType = msgType,
                attachmentUrl = attachmentUrl,
                timestamp = time,
                isMe = true
            )
            val msgId = db.messagingDao().insertMessage(msg)
            db.messagingDao().updateLastMessage(convId, text, time)
            msgId
        }

    suspend fun setChatLock(convId: Long, isLocked: Boolean, pin: String) = withContext(Dispatchers.IO) {
        db.messagingDao().setChatLock(convId, isLocked, pin)
    }

    suspend fun getConversation(convId: Long): ConversationEntity? = withContext(Dispatchers.IO) {
        db.messagingDao().getConversation(convId)
    }

    // Ibadah - Namaz
    fun getPrayerLogs(userId: Long): Flow<List<PrayerLogEntity>> =
        db.ibadahDao().getPrayerLogs(userId)

    suspend fun getTodayPrayerLog(userId: Long): PrayerLogEntity = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val existing = db.ibadahDao().getPrayerLog(userId, today)
        if (existing != null) {
            existing
        } else {
            val newLog = PrayerLogEntity(
                userId = userId,
                dateString = today,
                targetMonthLevel = 1
            )
            val id = db.ibadahDao().insertOrUpdatePrayerLog(newLog)
            newLog.copy(id = id)
        }
    }

    suspend fun togglePrayer(userId: Long, prayerName: String, isAda: Boolean) = withContext(Dispatchers.IO) {
        val current = getTodayPrayerLog(userId)
        val updated = when (prayerName.lowercase()) {
            "fajr" -> current.copy(fajr = isAda)
            "dhuhr" -> current.copy(dhuhr = isAda)
            "asr" -> current.copy(asr = isAda)
            "maghrib" -> current.copy(maghrib = isAda)
            "isha" -> current.copy(isha = isAda)
            else -> current
        }
        db.ibadahDao().insertOrUpdatePrayerLog(updated)
    }

    suspend fun updateTargetMonthLevel(userId: Long, level: Int) = withContext(Dispatchers.IO) {
        val current = getTodayPrayerLog(userId)
        db.ibadahDao().insertOrUpdatePrayerLog(current.copy(targetMonthLevel = level))
    }

    // Ibadah - Quran
    fun getQuranRecord(userId: Long): Flow<QuranRecordEntity?> =
        db.ibadahDao().getQuranRecord(userId)

    suspend fun initializeQuranRecordIfNeeded(userId: Long) = withContext(Dispatchers.IO) {
        val existing = db.ibadahDao().getQuranRecordSync(userId)
        if (existing == null) {
            db.ibadahDao().insertOrUpdateQuran(
                QuranRecordEntity(userId = userId)
            )
        }
    }

    suspend fun updateQuranProgress(userId: Long, surah: String, surahNum: Int, ayah: Int, juz: Int) = withContext(Dispatchers.IO) {
        val current = db.ibadahDao().getQuranRecordSync(userId) ?: QuranRecordEntity(userId = userId)
        val updated = current.copy(
            currentSurah = surah,
            currentSurahNumber = surahNum,
            currentAyah = ayah,
            currentJuz = juz,
            completedAyahsToday = current.completedAyahsToday + 1,
            totalVersesRead = current.totalVersesRead + 1
        )
        db.ibadahDao().insertOrUpdateQuran(updated)
    }

    suspend fun bookmarkQuran(userId: Long, surah: String, ayah: Int) = withContext(Dispatchers.IO) {
        val current = db.ibadahDao().getQuranRecordSync(userId) ?: QuranRecordEntity(userId = userId)
        db.ibadahDao().insertOrUpdateQuran(current.copy(bookmarkedSurah = surah, bookmarkedAyah = ayah))
    }

    // Ibadah - Zikr
    fun getZikrRecords(userId: Long): Flow<List<ZikrRecordEntity>> =
        db.ibadahDao().getZikrRecords(userId)

    suspend fun initializeZikrRecordsIfNeeded(userId: Long) = withContext(Dispatchers.IO) {
        val defaults = listOf(
            ZikrRecordEntity(userId = userId, zikrTitle = "SubhanAllah", count = 33, dailyGoal = 100),
            ZikrRecordEntity(userId = userId, zikrTitle = "Alhamdulillah", count = 33, dailyGoal = 100),
            ZikrRecordEntity(userId = userId, zikrTitle = "Allahu Akbar", count = 34, dailyGoal = 100),
            ZikrRecordEntity(userId = userId, zikrTitle = "Astaghfirullah", count = 70, dailyGoal = 100),
            ZikrRecordEntity(userId = userId, zikrTitle = "La ilaha illallah", count = 50, dailyGoal = 100)
        )
        defaults.forEach { db.ibadahDao().insertOrUpdateZikr(it) }
    }

    suspend fun updateZikrCount(record: ZikrRecordEntity, delta: Int) = withContext(Dispatchers.IO) {
        val newCount = (record.count + delta).coerceAtLeast(0)
        db.ibadahDao().insertOrUpdateZikr(record.copy(count = newCount, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun resetZikr(record: ZikrRecordEntity) = withContext(Dispatchers.IO) {
        db.ibadahDao().insertOrUpdateZikr(record.copy(count = 0, lastUpdated = System.currentTimeMillis()))
    }

    suspend fun addCustomZikr(userId: Long, title: String, goal: Int) = withContext(Dispatchers.IO) {
        db.ibadahDao().insertOrUpdateZikr(
            ZikrRecordEntity(userId = userId, zikrTitle = title, count = 0, dailyGoal = goal)
        )
    }

    // Ibadah - Azan AI
    fun getAzanRecordings(userId: Long): Flow<List<AzanRecordingEntity>> =
        db.ibadahDao().getAzanRecordings(userId)

    suspend fun saveAzanAnalysis(recording: AzanRecordingEntity): Long = withContext(Dispatchers.IO) {
        db.ibadahDao().insertAzanRecording(recording)
    }

    fun getAzanGoal(userId: Long): Flow<AzanGoalEntity?> =
        db.ibadahDao().getAzanGoal(userId)

    suspend fun initializeAzanGoalIfNeeded(userId: Long) = withContext(Dispatchers.IO) {
        val existing = db.ibadahDao().getAzanGoalSync(userId)
        if (existing == null) {
            db.ibadahDao().insertOrUpdateAzanGoal(
                AzanGoalEntity(
                    userId = userId,
                    currentStepNumber = 1,
                    currentStepName = "Daily Practice",
                    targetGoalLevel = "World Top 10"
                )
            )
        }
    }

    suspend fun updateAzanGoal(goal: AzanGoalEntity) = withContext(Dispatchers.IO) {
        db.ibadahDao().insertOrUpdateAzanGoal(goal)
    }

    // Alerts
    val allAlerts: Flow<List<SystemAlertEntity>> = db.alertsDao().getAllAlerts()

    suspend fun broadcastAlert(title: String, msg: String, type: String = "SYSTEM") = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("Today, HH:mm PKT", Locale.getDefault())
        db.alertsDao().insertAlert(
            SystemAlertEntity(
                title = title,
                message = msg,
                alertType = type,
                timestamp = sdf.format(Date())
            )
        )
    }

    // Admin Tools
    val allUsers: Flow<List<UserEntity>> = db.userDao().getAllUsers()

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        db.userDao().deleteUser(userId)
    }

    suspend fun setUserBanned(userId: Long, banned: Boolean) = withContext(Dispatchers.IO) {
        db.userDao().setBanned(userId, banned)
    }
}
