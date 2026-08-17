package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val dpUrl: String = "",
    val bio: String = "",
    val country: String = "Pakistan",
    val timezone: String = "UTC+5 (PKT)",
    val phone: String = "",
    val dob: String = "",
    val role: String = "USER", // ADMIN (first user is OWNER/ADMIN) or USER
    val joinedDate: String = "August 2026",
    val isLeaderboardVisible: Boolean = true,
    val isOnline: Boolean = true,
    val chatPin: String = "",
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val token: String,
    val userId: Long,
    val deviceName: String,
    val ipAddress: String,
    val isActive: Boolean = true,
    val loginTime: Long = System.currentTimeMillis(),
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "news_items")
data class NewsItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val headline: String,
    val whatHappened: String,
    val source: String,
    val publishedTime: String,
    val detectionTime: String,
    val impactType: String, // BULLISH, BEARISH, NEUTRAL, HIGH_IMPACT
    val possibleMarketImpact: String,
    val affectedAssets: String,
    val evidence: String,
    val romanUrduEnglishExpl: String,
    val confidence: Int, // e.g. 88
    val probability: Int, // e.g. 92
    val isConfirmed: Boolean = true,
    val isEarlySignal: Boolean = true,
    val isImportantImpact: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "companies")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val symbol: String,
    val sector: String,
    val logoEmoji: String = "🏢",
    val positiveNews: String,
    val negativeNews: String,
    val earnings: String,
    val ceoName: String,
    val productLaunches: String,
    val partnerships: String,
    val acquisitions: String,
    val regulatoryNews: String,
    val marketImpact: String,
    val source: String,
    val timestamp: String,
    val confidence: Int
)

@Entity(tableName = "new_companies")
data class NewCompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyName: String,
    val founder: String,
    val ceo: String,
    val businessSummary: String,
    val products: String,
    val businessModel: String,
    val funding: String,
    val investors: String,
    val plannedLaunchDate: String,
    val expectedImpact: String,
    val evidence: String,
    val source: String,
    val isVerified: Boolean
)

@Entity(tableName = "investors")
data class InvestorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rank: Int,
    val investorName: String,
    val fundName: String,
    val targetCompanyOrAsset: String,
    val investmentAmount: String,
    val positionChange: String,
    val reasonSignals: String,
    val evidence: String,
    val source: String,
    val timestamp: String
)

@Entity(tableName = "exchange_events")
data class ExchangeEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exchange: String, // BINANCE, MEXC, WEEX
    val eventType: String, // LARGE_TRADE, UNUSUAL_VOLUME, ORDER_BOOK, LIQUIDATION, WHALE_ACTIVITY
    val assetPair: String,
    val title: String,
    val details: String,
    val estimatedVolume: String,
    val confidence: Int,
    val timestamp: String,
    val anonymousWhaleId: String = "",
    val signalClassification: String = "🟢 Bullish"
)

@Entity(tableName = "ai_videos")
data class AiVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val romanEnglishScript: String,
    val source: String,
    val publishedDate: String,
    val durationText: String,
    val videoCategory: String,
    val creatorCredit: String = "MUHAMMAD AHMED",
    val brandingTag: String = "WORLD EAGLE",
    val thumbnailGradientIndex: Int = 0,
    val viewsCount: Int = 1420
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardUser(
    @PrimaryKey val username: String,
    val fullName: String,
    val dpUrl: String,
    val publicActivity: String,
    val publicScore: Int,
    val rank: Int,
    val isVerified: Boolean = false
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isGroup: Boolean,
    val participantUsernames: String, // comma separated
    val lastMessage: String,
    val lastMessageTime: String,
    val isLocked: Boolean = false,
    val pinHash: String = "",
    val unreadCount: Int = 0,
    val avatarEmoji: String = "💬"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val senderUsername: String,
    val senderFullName: String,
    val text: String,
    val messageType: String = "TEXT", // TEXT, IMAGE, VOICE, FILE, STICKER
    val attachmentUrl: String = "",
    val timestamp: String,
    val isMe: Boolean = false
)

@Entity(tableName = "prayer_logs")
data class PrayerLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateString: String, // YYYY-MM-DD
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val targetMonthLevel: Int = 1 // Month 1 = 1 prayer/day ... Month 5 = 5 prayers/day
)

@Entity(tableName = "quran_records")
data class QuranRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val currentSurah: String = "Al-Baqarah",
    val currentSurahNumber: Int = 2,
    val currentAyah: Int = 255,
    val currentJuz: Int = 3,
    val bookmarkedSurah: String = "Ya-Sin",
    val bookmarkedAyah: Int = 1,
    val dailyTargetAyahs: Int = 10,
    val completedAyahsToday: Int = 7,
    val totalVersesRead: Int = 420,
    val streakDays: Int = 12
)

@Entity(tableName = "zikr_records")
data class ZikrRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val zikrTitle: String,
    val count: Int,
    val dailyGoal: Int = 100,
    val streakDays: Int = 5,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "azan_recordings")
data class AzanRecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val recordedDate: String,
    val durationSeconds: Int,
    val timingScore: Int,
    val pitchStabilityScore: Int,
    val voiceClarityScore: Int,
    val loudnessConsistencyScore: Int,
    val pausesScore: Int,
    val overallScore: Int,
    val improvementDelta: Int, // e.g. +6 vs yesterday
    val romanEnglishExplanation: String,
    val tajweedGuidanceNote: String
)

@Entity(tableName = "azan_goals")
data class AzanGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val currentStepNumber: Int = 1,
    val currentStepName: String = "Daily Practice",
    val targetGoalLevel: String = "World Top 10", // "World Top 10" or "World #1"
    val personalNotes: String = "Focusing on breath stamina and clear Makharij",
    val targetYear: String = "2026-2027"
)

@Entity(tableName = "system_alerts")
data class SystemAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val alertType: String = "MARKET_ALERT", // SYSTEM, MARKET_ALERT, SECURITY
    val timestamp: String,
    val isRead: Boolean = false
)
