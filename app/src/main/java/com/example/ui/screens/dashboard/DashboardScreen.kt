package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SignalClassificationBadge
import com.example.ui.components.WorldEagleBrandingHeader
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val earlyNewsList by viewModel.earlyNews.collectAsState()
    val binanceEvents by viewModel.binanceEvents.collectAsState()
    val topInvestorsList by viewModel.topInvestors.collectAsState()
    val todayPrayer by viewModel.todayPrayerLog.collectAsState()
    val alerts by viewModel.systemAlerts.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Dashboard Top Banner with Welcome
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x12FFFFFF)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(AmberLight, AmberDark)
                                )
                            )
                            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.dpUrl?.ifBlank { "🦅" } ?: "🦅",
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WORLD EAGLE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                ),
                                color = Color.White
                            )
                            if (currentUser?.role == "ADMIN") {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = AmberPrimary,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "OWNER / ADMIN",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = Color(0xFF050B18),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Terminal Active: ${currentUser?.fullName ?: "Guest"} (@${currentUser?.username ?: ""})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Live Alerts Ticker
        if (alerts.isNotEmpty()) {
            item {
                Surface(
                    color = AmberPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = alerts.first().title + ": " + alerts.first().message,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Section: 12 Module Grid (All required modules)
        item {
            Text(
                text = "Intelligence & Platform Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "Early News",
                        subtitle = "Global Signals & Evidence",
                        icon = Icons.Default.Bolt,
                        accentColor = SignalBullish,
                        badge = "${earlyNewsList.size} Signals",
                        modifier = Modifier.weight(1f).testTag("tile_early_news"),
                        onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_EARLY_NEWS) }
                    )
                    ModuleTile(
                        title = "Important Impact",
                        subtitle = "Market Volatility Analysis",
                        icon = Icons.Default.WarningAmber,
                        accentColor = SignalHighImpact,
                        badge = "High Impact",
                        modifier = Modifier.weight(1f).testTag("tile_important_impact"),
                        onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_IMPORTANT_IMPACT) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "Company News",
                        subtitle = "Corporate Earnings & Execs",
                        icon = Icons.Default.Business,
                        accentColor = AmberPrimary,
                        badge = "4 Major Tracks",
                        modifier = Modifier.weight(1f).testTag("tile_company_news"),
                        onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_COMPANY_NEWS) }
                    )
                    ModuleTile(
                        title = "New Companies",
                        subtitle = "Upcoming Startups & IPOs",
                        icon = Icons.Default.RocketLaunch,
                        accentColor = SignalBullish,
                        badge = "Verified Pipeline",
                        modifier = Modifier.weight(1f).testTag("tile_new_companies"),
                        onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_NEW_COMPANIES) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "Top 20 Investors",
                        subtitle = "Verifiable Fund Flows",
                        icon = Icons.Default.AccountBalance,
                        accentColor = AmberLight,
                        badge = "SEC Form 13F",
                        modifier = Modifier.weight(1f).testTag("tile_top_investors"),
                        onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_TOP_INVESTORS) }
                    )
                    ModuleTile(
                        title = "Binance",
                        subtitle = "Whale Clusters & Depth",
                        icon = Icons.Default.ShowChart,
                        accentColor = AmberPrimary,
                        badge = "Whale #BN-8802",
                        modifier = Modifier.weight(1f).testTag("tile_binance"),
                        onClick = { viewModel.navigateTo(AppDestination.EXCHANGE_BINANCE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "MEXC",
                        subtitle = "Spot & Unusual Volume",
                        icon = Icons.Default.CurrencyExchange,
                        accentColor = SignalBullish,
                        badge = "Vol +410%",
                        modifier = Modifier.weight(1f).testTag("tile_mexc"),
                        onClick = { viewModel.navigateTo(AppDestination.EXCHANGE_MEXC) }
                    )
                    ModuleTile(
                        title = "WEEX",
                        subtitle = "Futures Open Interest",
                        icon = Icons.Default.CandlestickChart,
                        accentColor = SignalHighImpact,
                        badge = "$140M OI",
                        modifier = Modifier.weight(1f).testTag("tile_weex"),
                        onClick = { viewModel.navigateTo(AppDestination.EXCHANGE_WEEX) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "AI Videos",
                        subtitle = "2 Daily Video Briefings",
                        icon = Icons.Default.PlayCircle,
                        accentColor = Color(0xFFEC4899),
                        badge = "Muhammad Ahmed",
                        modifier = Modifier.weight(1f).testTag("tile_ai_videos"),
                        onClick = { viewModel.navigateTo(AppDestination.AI_VIDEOS) }
                    )
                    ModuleTile(
                        title = "Leaderboard",
                        subtitle = "Global Verified Ranks",
                        icon = Icons.Default.Leaderboard,
                        accentColor = AmberPrimary,
                        badge = "Top Ranks",
                        modifier = Modifier.weight(1f).testTag("tile_leaderboard"),
                        onClick = { viewModel.navigateTo(AppDestination.LEADERBOARD) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModuleTile(
                        title = "Messages",
                        subtitle = "Locked Chats & WebRTC",
                        icon = Icons.Default.Chat,
                        accentColor = SignalBullish,
                        badge = "PIN Locked",
                        modifier = Modifier.weight(1f).testTag("tile_messages"),
                        onClick = { viewModel.navigateTo(AppDestination.MESSAGES) }
                    )
                    ModuleTile(
                        title = "Ibadah Suite",
                        subtitle = "Namaz, Quran, Zikr & Azan AI",
                        icon = Icons.Default.Mosque,
                        accentColor = IbadahEmeraldLight,
                        badge = "Month ${todayPrayer?.targetMonthLevel ?: 1} Plan",
                        modifier = Modifier.weight(1f).testTag("tile_ibadah"),
                        onClick = { viewModel.navigateTo(AppDestination.IBADAH_NAMAZ) }
                    )
                }
            }
        }

        // Live Market & Early Signal Preview Card
        item {
            Text(
                text = "Realtime Global Intelligence Pulse",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        items(earlyNewsList.take(2)) { news ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(18.dp))
                    .clickable { viewModel.navigateTo(AppDestination.INTELLIGENCE_EARLY_NEWS) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x0EFFFFFF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SignalClassificationBadge(impactType = news.impactType)
                        Text(
                            text = "Confidence: ${news.confidence}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = news.headline,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = news.whatHappened,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Source: ${news.source}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = news.detectionTime,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SignalBullish
                        )
                    }
                }
            }
        }

        // Footer Requirement: "Created by MUHAMMAD AHMED"
        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun ModuleTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badge: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x10FFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
