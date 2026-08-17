package com.example.ui.screens.exchanges

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExchangeEventEntity
import com.example.ui.components.SignalClassificationBadge
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(
    viewModel: MainViewModel,
    initialExchange: String = "BINANCE",
    modifier: Modifier = Modifier
) {
    var selectedExchange by remember { mutableStateOf(initialExchange) }
    val exchanges = listOf("BINANCE", "MEXC", "WEEX")

    val binanceEvents by viewModel.binanceEvents.collectAsState()
    val mexcEvents by viewModel.mexcEvents.collectAsState()
    val weexEvents by viewModel.weexEvents.collectAsState()

    val currentEvents = when (selectedExchange) {
        "BINANCE" -> binanceEvents
        "MEXC" -> mexcEvents
        "WEEX" -> weexEvents
        else -> binanceEvents
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Exchange selection tabs - Strict independent data sets
        PrimaryTabRow(
            selectedTabIndex = exchanges.indexOf(selectedExchange),
            containerColor = Color(0x180B1528),
            contentColor = AmberPrimary,
            divider = {
                HorizontalDivider(color = Color(0x18FFFFFF))
            }
        ) {
            exchanges.forEachIndexed { index, name ->
                Tab(
                    selected = selectedExchange == name,
                    onClick = { selectedExchange = name },
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedExchange == name) FontWeight.Black else FontWeight.Medium
                            ),
                            color = if (selectedExchange == name) AmberPrimary else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0x12FFFFFF)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedExchange Liquidity & Whale Desk",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Surface(
                                color = SignalBullish.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "LIVE ADAPTER ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SignalBullish,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Independent data adapter parsing public order book depth, large TWAP blocks and clustered anonymous whale activity. Never combined across exchanges.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            items(currentEvents) { event ->
                ExchangeEventCard(event)
            }

            item {
                WorldEagleFooter()
            }
        }
    }
}

@Composable
fun ExchangeEventCard(event: ExchangeEventEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0x10FFFFFF)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = AmberPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.assetPair,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.signalClassification,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confidence: ${event.confidence}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.details,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (event.anonymousWhaleId.isNotBlank()) {
                Surface(
                    color = Color(0x18FFFFFF),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Public Cluster ID: ${event.anonymousWhaleId}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFFF1F5F9)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Est. Notional Volume: ${event.estimatedVolume}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SignalBullish
                )
                Text(
                    text = event.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
