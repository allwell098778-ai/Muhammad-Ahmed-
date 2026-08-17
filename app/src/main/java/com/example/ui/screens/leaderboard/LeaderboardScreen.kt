package com.example.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardUser
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var isShowOnLeaderboard by remember(currentUser) { mutableStateOf(currentUser?.isLeaderboardVisible ?: true) }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x12FFFFFF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Global Intelligence Leaderboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Rankings based on verified analysis contributions, high-accuracy early signal verifications and community engagement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy Setting Switch: "Show me on leaderboard"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x10FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show me on leaderboard",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Allow public rank and activity score display",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Switch(
                            checked = isShowOnLeaderboard,
                            onCheckedChange = { checked ->
                                isShowOnLeaderboard = checked
                                coroutineScope.launch {
                                    viewModel.authManager.toggleLeaderboardVisibility(checked)
                                    viewModel.showToast(if (checked) "Public leaderboard visibility enabled" else "Leaderboard visibility hidden")
                                }
                            }
                        )
                    }
                }
            }
        }

        items(leaderboard) { entry ->
            LeaderboardItemCard(entry = entry, isCurrentUser = entry.username == currentUser?.username)
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun LeaderboardItemCard(
    entry: LeaderboardUser,
    isCurrentUser: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isCurrentUser) AmberPrimary else Color(0x1FFFFFFF),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) AmberPrimary.copy(alpha = 0.12f) else Color(0x0EFFFFFF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when (entry.rank) {
                            1 -> EagleGold
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> EagleNavy800
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${entry.rank}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = if (entry.rank <= 3) EagleNavy900 else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(EagleNavy900)
                    .border(1.dp, EagleGold.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🦅", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.fullName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (entry.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Analyst",
                            tint = SignalBullish,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "@${entry.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = entry.publicActivity,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.publicScore}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = EagleGold
                )
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
