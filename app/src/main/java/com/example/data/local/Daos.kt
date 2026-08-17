package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    @Query("UPDATE users SET isBanned = :banned WHERE id = :userId")
    suspend fun setBanned(userId: Long, banned: Boolean)

    @Query("SELECT username FROM users")
    suspend fun getAllUsernames(): List<String>
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE token = :token AND isActive = 1 LIMIT 1")
    suspend fun getActiveSession(token: String): SessionEntity?

    @Query("SELECT * FROM sessions WHERE userId = :userId AND isActive = 1")
    fun getActiveSessionsForUser(userId: Long): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("UPDATE sessions SET isActive = 0 WHERE token = :token")
    suspend fun invalidateSession(token: String)

    @Query("UPDATE sessions SET isActive = 0 WHERE userId = :userId")
    suspend fun invalidateAllUserSessions(userId: Long)
}

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_items ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsItemEntity>>

    @Query("SELECT * FROM news_items WHERE isEarlySignal = 1 ORDER BY timestamp DESC")
    fun getEarlyNews(): Flow<List<NewsItemEntity>>

    @Query("SELECT * FROM news_items WHERE isImportantImpact = 1 ORDER BY timestamp DESC")
    fun getImportantImpactNews(): Flow<List<NewsItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleNews(news: NewsItemEntity): Long
}

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies ORDER BY name ASC")
    fun getAllCompanies(): Flow<List<CompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<CompanyEntity>)

    @Query("SELECT * FROM new_companies ORDER BY id DESC")
    fun getAllNewCompanies(): Flow<List<NewCompanyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewCompanies(companies: List<NewCompanyEntity>)
}

@Dao
interface InvestorDao {
    @Query("SELECT * FROM investors ORDER BY rank ASC")
    fun getTop20Investors(): Flow<List<InvestorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestors(investors: List<InvestorEntity>)
}

@Dao
interface ExchangeDao {
    @Query("SELECT * FROM exchange_events WHERE exchange = :exchange ORDER BY id DESC")
    fun getExchangeEvents(exchange: String): Flow<List<ExchangeEventEntity>>

    @Query("SELECT * FROM exchange_events ORDER BY id DESC")
    fun getAllExchangeEvents(): Flow<List<ExchangeEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ExchangeEventEntity>)
}

@Dao
interface AiVideoDao {
    @Query("SELECT * FROM ai_videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<AiVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<AiVideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: AiVideoEntity): Long
}

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard_entries ORDER BY publicScore DESC")
    fun getLeaderboard(): Flow<List<LeaderboardUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(entries: List<LeaderboardUser>)
}

@Dao
interface MessagingDao {
    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :convId LIMIT 1")
    suspend fun getConversation(convId: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY id ASC")
    fun getMessagesForConversation(convId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE conversations SET lastMessage = :lastMsg, lastMessageTime = :time WHERE id = :convId")
    suspend fun updateLastMessage(convId: Long, lastMsg: String, time: String)

    @Query("UPDATE conversations SET isLocked = :isLocked, pinHash = :pin WHERE id = :convId")
    suspend fun setChatLock(convId: Long, isLocked: Boolean, pin: String)
}

@Dao
interface IbadahDao {
    // Namaz
    @Query("SELECT * FROM prayer_logs WHERE userId = :userId AND dateString = :dateString LIMIT 1")
    suspend fun getPrayerLog(userId: Long, dateString: String): PrayerLogEntity?

    @Query("SELECT * FROM prayer_logs WHERE userId = :userId ORDER BY dateString DESC")
    fun getPrayerLogs(userId: Long): Flow<List<PrayerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerLog(log: PrayerLogEntity): Long

    // Quran
    @Query("SELECT * FROM quran_records WHERE userId = :userId LIMIT 1")
    fun getQuranRecord(userId: Long): Flow<QuranRecordEntity?>

    @Query("SELECT * FROM quran_records WHERE userId = :userId LIMIT 1")
    suspend fun getQuranRecordSync(userId: Long): QuranRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuran(record: QuranRecordEntity)

    // Zikr
    @Query("SELECT * FROM zikr_records WHERE userId = :userId")
    fun getZikrRecords(userId: Long): Flow<List<ZikrRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateZikr(record: ZikrRecordEntity)

    // Azan AI
    @Query("SELECT * FROM azan_recordings WHERE userId = :userId ORDER BY id DESC")
    fun getAzanRecordings(userId: Long): Flow<List<AzanRecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAzanRecording(recording: AzanRecordingEntity): Long

    // Azan Goals
    @Query("SELECT * FROM azan_goals WHERE userId = :userId LIMIT 1")
    fun getAzanGoal(userId: Long): Flow<AzanGoalEntity?>

    @Query("SELECT * FROM azan_goals WHERE userId = :userId LIMIT 1")
    suspend fun getAzanGoalSync(userId: Long): AzanGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAzanGoal(goal: AzanGoalEntity)
}

@Dao
interface AlertsDao {
    @Query("SELECT * FROM system_alerts ORDER BY id DESC")
    fun getAllAlerts(): Flow<List<SystemAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SystemAlertEntity): Long
}
