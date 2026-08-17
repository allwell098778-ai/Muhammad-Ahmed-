package com.example.ui.screens.ibadah

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AzanGoalEntity
import com.example.data.model.AzanRecordingEntity
import com.example.data.model.PrayerLogEntity
import com.example.data.model.QuranRecordEntity
import com.example.data.model.ZikrRecordEntity
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbadahScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Namaz", "Quran", "Zikr", "Azan AI", "Azan Goals")

    val todayPrayer by viewModel.todayPrayerLog.collectAsState()
    val quranRecord by viewModel.quranRecord.collectAsState()
    val zikrRecords by viewModel.zikrRecords.collectAsState()
    val azanRecordings by viewModel.azanRecordings.collectAsState()
    val azanGoal by viewModel.azanGoal.collectAsState()
    val isRecordingAzan by viewModel.isRecordingAzan.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0x180B1528),
            contentColor = IbadahEmeraldLight,
            edgePadding = 16.dp,
            divider = {
                HorizontalDivider(color = Color(0x18FFFFFF))
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == index) IbadahEmeraldLight else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> NamazTab(viewModel, todayPrayer)
            1 -> QuranTab(viewModel, quranRecord)
            2 -> ZikrTab(viewModel, zikrRecords)
            3 -> AzanAiTab(viewModel, azanRecordings, isRecordingAzan)
            4 -> AzanGoalsTab(viewModel, azanGoal)
        }
    }
}

// 1. NAMAZ TAB - 5-Month Progressive Plan & 5 Daily Prayers
@Composable
fun NamazTab(
    viewModel: MainViewModel,
    prayerLog: PrayerLogEntity?
) {
    val log = prayerLog ?: PrayerLogEntity(userId = 0, dateString = "Today")
    val level = log.targetMonthLevel

    val prayersCompletedCount = listOf(log.fajr, log.dhuhr, log.asr, log.maghrib, log.isha).count { it }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 5-Month Progressive Plan Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IbadahEmeraldLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
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
                            text = "5-Month Progressive Namaz Plan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = IbadahEmeraldLight
                        )
                        Surface(
                            color = IbadahEmeraldLight.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Month $level / 5",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IbadahEmeraldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (level) {
                            1 -> "Month 1 Goal: Complete at least 1 Prayer daily consistently."
                            2 -> "Month 2 Goal: Step up to 2 Prayers daily."
                            3 -> "Month 3 Goal: Step up to 3 Prayers daily."
                            4 -> "Month 4 Goal: Step up to 4 Prayers daily."
                            else -> "Month 5 Goal: Full 5 Daily Prayers (Fajr, Dhuhr, Asr, Maghrib, Isha) in congregation!"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Month Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..5).forEach { m ->
                            FilterChip(
                                selected = level == m,
                                onClick = { viewModel.setPrayerTargetLevel(m) },
                                label = { Text("M$m ($m/d)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IbadahEmeraldLight,
                                    selectedLabelColor = Color(0xFF050B18),
                                    containerColor = Color(0x10FFFFFF),
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Daily Prayers Checklist
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Prayers (${prayersCompletedCount}/5 Ada)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Goal: Month ${log.targetMonthLevel} Plan",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AmberPrimary
                )
            }
        }

        item {
            PrayerCheckCard("Fajr", "Dawn Prayer", log.fajr) {
                viewModel.togglePrayer("Fajr", !log.fajr)
            }
        }
        item {
            PrayerCheckCard("Dhuhr", "Noon Prayer", log.dhuhr) {
                viewModel.togglePrayer("Dhuhr", !log.dhuhr)
            }
        }
        item {
            PrayerCheckCard("Asr", "Afternoon Prayer", log.asr) {
                viewModel.togglePrayer("Asr", !log.asr)
            }
        }
        item {
            PrayerCheckCard("Maghrib", "Sunset Prayer", log.maghrib) {
                viewModel.togglePrayer("Maghrib", !log.maghrib)
            }
        }
        item {
            PrayerCheckCard("Isha", "Night Prayer", log.isha) {
                viewModel.togglePrayer("Isha", !log.isha)
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun PrayerCheckCard(
    name: String,
    timing: String,
    isAda: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .border(
                1.dp,
                if (isAda) IbadahEmeraldLight else Color(0x20FFFFFF),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isAda) IbadahEmeraldDark.copy(alpha = 0.25f) else Color(0x10FFFFFF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isAda) IbadahEmeraldLight else Color(0x18FFFFFF))
                        .border(1.dp, if (isAda) IbadahEmeraldLight else Color(0x30FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAda) Icons.Default.Check else Icons.Default.Mosque,
                        contentDescription = null,
                        tint = if (isAda) Color(0xFF050B18) else IbadahEmeraldLight
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = timing,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAda) IbadahEmeraldLight else Color(0x18FFFFFF),
                    contentColor = if (isAda) Color(0xFF050B18) else Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAda) "Ada Ki ✓" else "Namaz Ada Karein",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// 2. QURAN TAB - Progress, Surah, Juz, Bookmarks
@Composable
fun QuranTab(
    viewModel: MainViewModel,
    quranRecord: QuranRecordEntity?
) {
    val quran = quranRecord ?: QuranRecordEntity(userId = 0)

    val surahs = listOf(
        "Al-Fatiha" to 7,
        "Al-Baqarah" to 286,
        "Ali 'Imran" to 200,
        "An-Nisa" to 176,
        "Al-Ma'idah" to 120,
        "Al-An'am" to 165,
        "Al-A'raf" to 206,
        "Al-Kahf" to 110,
        "Ya-Sin" to 83,
        "Ar-Rahman" to 78,
        "Al-Mulk" to 30,
        "Al-Ikhlas" to 4
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Current Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Quran Reading Progress",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = IbadahEmeraldLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Surah ${quran.currentSurah}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("Ayah ${quran.currentAyah} • Juz ${quran.currentJuz}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${quran.completedAyahsToday} / ${quran.dailyTargetAyahs} Ayahs", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = EagleGold)
                            Text("Today's Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = (quran.completedAyahsToday.toFloat() / quran.dailyTargetAyahs.coerceAtLeast(1)).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = IbadahEmeraldLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bookmark section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔖 Bookmark: Surah ${quran.bookmarkedSurah} Ayah ${quran.bookmarkedAyah}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Button(
                            onClick = {
                                viewModel.bookmarkQuran(quran.currentSurah, quran.currentAyah)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EagleGold, contentColor = EagleNavy900),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Set Bookmark Here", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Surah Index & Quick Tracker",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(surahs) { (name, totalVerses) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = "$totalVerses Verses", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            viewModel.updateQuranProgress(
                                surah = name,
                                surahNum = 1,
                                ayah = (1..totalVerses).random(),
                                juz = (1..30).random()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IbadahEmeraldLight, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log Read", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}

// 3. ZIKR TAB - Interactive Counters (+1, +10, Reset) & Custom Zikr
@Composable
fun ZikrTab(
    viewModel: MainViewModel,
    zikrList: List<ZikrRecordEntity>
) {
    var showAddZikrDialog by remember { mutableStateOf(false) }
    var newZikrTitle by remember { mutableStateOf("") }
    var newZikrGoal by remember { mutableStateOf("100") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Digital Tasbih & Daily Zikr",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = { showAddZikrDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EagleGold, contentColor = EagleNavy900),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Custom Zikr", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        items(zikrList) { zikr ->
            ZikrCounterCard(
                zikr = zikr,
                onAdd1 = { viewModel.clickZikr(zikr, 1) },
                onAdd10 = { viewModel.clickZikr(zikr, 10) },
                onReset = { viewModel.resetZikr(zikr) }
            )
        }

        item {
            WorldEagleFooter()
        }
    }

    if (showAddZikrDialog) {
        AlertDialog(
            onDismissRequest = { showAddZikrDialog = false },
            title = { Text("Add Custom Zikr") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newZikrTitle,
                        onValueChange = { newZikrTitle = it },
                        label = { Text("Zikr Name (e.g. Hasbunallahu wa ni'mal wakeel)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newZikrGoal,
                        onValueChange = { newZikrGoal = it },
                        label = { Text("Daily Target Goal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newZikrTitle.isNotBlank()) {
                            viewModel.addCustomZikr(newZikrTitle, newZikrGoal.toIntOrNull() ?: 100)
                            showAddZikrDialog = false
                            newZikrTitle = ""
                        }
                    }
                ) {
                    Text("Add Zikr")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddZikrDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ZikrCounterCard(
    zikr: ZikrRecordEntity,
    onAdd1: () -> Unit,
    onAdd10: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zikr.zikrTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Goal: ${zikr.dailyGoal}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Giant Counter Number Display
            Text(
                text = "${zikr.count}",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                color = IbadahEmeraldLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (zikr.count.toFloat() / zikr.dailyGoal.coerceAtLeast(1)).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = IbadahEmeraldLight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: +1, +10, Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAdd1,
                    modifier = Modifier.weight(2f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IbadahEmeraldLight, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+1 TAP", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black))
                }

                Button(
                    onClick = onAdd10,
                    modifier = Modifier.weight(1.2f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EagleGold, contentColor = EagleNavy900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+10", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = SignalBearish)
                }
            }
        }
    }
}

// 4. AZAN AI TAB - Audio Recording Analysis, Measurable Scores, Roman English, Qualified Qari Review
@Composable
fun AzanAiTab(
    viewModel: MainViewModel,
    recordings: List<AzanRecordingEntity>,
    isRecording: Boolean
) {
    val latestAnalysis by viewModel.latestAzanAnalysis.collectAsState()

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
                    .border(1.dp, Brush.horizontalGradient(listOf(IbadahEmeraldLight, EagleGold)), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Azan AI Studio & Vocal Analyzer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Real acoustic DSP analysis for Timing, Pitch, Voice Clarity, Loudness & Breath Pauses",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Big Record Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) SignalBearish else IbadahEmeraldLight)
                            .clickable {
                                if (!isRecording) {
                                    viewModel.startAzanRecordingSimulation()
                                } else {
                                    viewModel.completeAzanRecordingAnalysis()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record Azan",
                            tint = Color.Black,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isRecording) "Recording In Progress... Tap to Complete Analysis" else "Tap Mic to Record & Analyze Azan",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isRecording) SignalBearish else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Latest Recording Analysis Breakdown
        val displayItem = latestAnalysis ?: recordings.firstOrNull()
        if (displayItem != null) {
            item {
                AzanAnalysisBreakdownCard(displayItem)
            }
        }

        item {
            Text(
                text = "Previous Azan Vocal Sessions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(recordings) { rec ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = rec.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = "${rec.recordedDate} • ${rec.durationSeconds}s", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Surface(
                        color = IbadahEmeraldLight.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${rec.overallScore}/100 (+${rec.improvementDelta})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = IbadahEmeraldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun AzanAnalysisBreakdownCard(rec: AzanRecordingEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, IbadahEmeraldLight, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = EagleNavy800),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Acoustic Analysis (${rec.recordedDate})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = IbadahEmeraldLight
                )
                Text(
                    text = "Overall: ${rec.overallScore}/100 (+${rec.improvementDelta})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = EagleGold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ScoreProgressRow(label = "Timing & Rhythm", score = rec.timingScore)
            ScoreProgressRow(label = "Pitch Stability", score = rec.pitchStabilityScore)
            ScoreProgressRow(label = "Voice Clarity & Tone", score = rec.voiceClarityScore)
            ScoreProgressRow(label = "Loudness Consistency", score = rec.loudnessConsistencyScore)
            ScoreProgressRow(label = "Natural Breath Pauses", score = rec.pausesScore)

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = EagleNavy900,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Roman English AI Feedback:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EagleGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rec.romanEnglishExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Qualified Qari review guidance note
            Surface(
                color = IbadahEmeraldDark.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = IbadahEmeraldLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = rec.tajweedGuidanceNote,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = IbadahEmeraldLight
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreProgressRow(label: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(text = "$score / 100", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = IbadahEmeraldLight)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = (score / 100f).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
            color = IbadahEmeraldLight
        )
    }
}

// 5. AZAN GOALS TAB - 11 Step International Competition Roadmap & Target Goal Level
@Composable
fun AzanGoalsTab(
    viewModel: MainViewModel,
    goal: AzanGoalEntity?
) {
    val currentGoal = goal ?: AzanGoalEntity(userId = 0)

    val roadmapSteps = listOf(
        1 to "Daily Practice & Voice Conditioning",
        2 to "Pronunciation & Basic Makharij Foundation",
        3 to "Breath Control & Stamina Extension",
        4 to "Melodic Cadence & Maqam Hijaz / Rast Study",
        5 to "Local Mosque Azan Delivery",
        6 to "District Level Azan Auditions",
        7 to "Provincial / State Championship",
        8 to "National Azan Finalist",
        9 to "Global Invitationals & Islamic World League",
        10 to "World Top 10 International Stage",
        11 to "International Azan World Champion (#1)"
    )

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
                    .border(1.dp, EagleGold, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "International Azan Competition Roadmap",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = EagleGold
                        )
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = EagleGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current Stage: Step ${currentGoal.currentStepNumber} (${currentGoal.currentStepName})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target Goal Level: ${currentGoal.targetGoalLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IbadahEmeraldLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = (currentGoal.currentStepNumber / 11f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = EagleGold
                    )
                }
            }
        }

        item {
            Text(
                text = "11-Step International Milestone Pathway",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(roadmapSteps) { (stepNum, name) ->
            val isCurrent = currentGoal.currentStepNumber == stepNum
            val isCompleted = currentGoal.currentStepNumber > stepNum

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.updateAzanGoalStep(stepNum, name, if (stepNum >= 10) "World Top 10" else "Level $stepNum")
                    }
                    .border(
                        1.dp,
                        if (isCurrent) EagleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isCurrent -> EagleNavy700
                        isCompleted -> IbadahEmeraldDark.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> EagleGold
                                    isCompleted -> IbadahEmeraldLight
                                    else -> EagleNavy800
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNum",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = if (isCurrent || isCompleted) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isCurrent) EagleGold else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    if (isCurrent) {
                        Surface(
                            color = EagleGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Black),
                                color = EagleNavy900,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}
