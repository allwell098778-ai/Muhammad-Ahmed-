package com.example.ui.screens.intelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyEntity
import com.example.data.model.InvestorEntity
import com.example.data.model.NewCompanyEntity
import com.example.data.model.NewsItemEntity
import com.example.ui.components.SignalClassificationBadge
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelligenceScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabTitles = listOf("Early News", "Important Impact", "Company News", "New Companies", "Top 20 Investors")

    val allNewsList by viewModel.allNews.collectAsState()
    val earlyNewsList by viewModel.earlyNews.collectAsState()
    val impactNewsList by viewModel.importantImpactNews.collectAsState()
    val companiesList by viewModel.allCompanies.collectAsState()
    val newCompaniesList by viewModel.newCompanies.collectAsState()
    val topInvestorsList by viewModel.topInvestors.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0x180B1528),
            contentColor = AmberPrimary,
            edgePadding = 16.dp,
            divider = {
                HorizontalDivider(color = Color(0x18FFFFFF))
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == index) AmberPrimary else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> EarlyNewsTab(earlyNewsList)
            1 -> ImportantImpactTab(impactNewsList)
            2 -> CompanyNewsTab(companiesList)
            3 -> NewCompaniesTab(newCompaniesList)
            4 -> TopInvestorsTab(topInvestorsList)
        }
    }
}

@Composable
fun EarlyNewsTab(newsList: List<NewsItemEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                color = AmberPrimary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AmberPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Global Intelligence Lead Engine: Early public signal detections with confidence & Roman English breakdown.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        items(newsList) { news ->
            EarlyNewsCard(news)
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun EarlyNewsCard(news: NewsItemEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x24FFFFFF), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0x10FFFFFF)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SignalClassificationBadge(impactType = news.impactType)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Confidence: ${news.confidence}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prob: ${news.probability}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = SignalBullish
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = news.headline,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = news.whatHappened,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // AI Roman English Explanation Section
            Surface(
                color = Color(0x18FFFFFF),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Roman English Explanation (World Eagle)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = news.romanUrduEnglishExpl,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF1F5F9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Market Impact & Affected Assets
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Assets: ${news.affectedAssets}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AmberPrimary
                )
                Text(
                    text = news.detectionTime,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SignalBullish
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Evidence: ${news.evidence}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF94A3B8)
            )
            Text(
                text = "Source: ${news.source} • Published: ${news.publishedTime}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun ImportantImpactTab(impactList: List<NewsItemEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SignalHighImpact.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SignalHighImpact.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ Important News Impact Engine",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = SignalHighImpact
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Highlighting market crash risks, bullish breakouts and sector-wide shocks. Confirmed information is strictly separated from AI estimates.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }

        items(impactList) { news ->
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = if (news.isConfirmed) SignalBullish.copy(alpha = 0.2f) else SignalNeutral.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (news.isConfirmed) "CONFIRMED INFORMATION" else "AI ESTIMATE (NON-CONFIRMED)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (news.isConfirmed) SignalBullish else SignalNeutral,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        SignalClassificationBadge(impactType = news.impactType)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = news.headline,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Possible Market Impact: ${news.possibleMarketImpact}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = AmberPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Evidence: ${news.evidence}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun CompanyNewsTab(companies: List<CompanyEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(companies) { comp ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = comp.logoEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${comp.name} (${comp.symbol})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "CEO: ${comp.ceoName} • ${comp.sector}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Surface(
                            color = AmberPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Confidence ${comp.confidence}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🟢 Positive News: ${comp.positiveNews}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SignalBullish
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔴 Regulatory/Negative Risk: ${comp.negativeNews}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SignalBearish
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Earnings & Margins: ${comp.earnings}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = "Product Launches: ${comp.productLaunches}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Partnerships: ${comp.partnerships}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Source: ${comp.source}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = comp.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
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
fun NewCompaniesTab(newCompanies: List<NewCompanyEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Upcoming Public Companies & Startups",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Tracked publicly announced companies with verifiable filings. Unverified details are strictly labeled.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        items(newCompanies) { comp ->
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
                        Text(
                            text = comp.companyName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Surface(
                            color = if (comp.isVerified) SignalBullish.copy(alpha = 0.2f) else SignalNeutral.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (comp.isVerified) "VERIFIED FILING" else "NOT PUBLICLY VERIFIED",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (comp.isVerified) SignalBullish else SignalNeutral,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Founder / CEO: ${comp.founder} (CEO: ${comp.ceo})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AmberPrimary
                    )
                    Text(
                        text = "Business Summary: ${comp.businessSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "Key Products: ${comp.products}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Funding & Investors: ${comp.funding} • ${comp.investors}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Text(
                        text = "Planned Listing / Launch: ${comp.plannedLaunchDate}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberPrimary
                    )
                    Text(
                        text = "Evidence & Source: ${comp.evidence} (${comp.source})",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        item {
            WorldEagleFooter()
        }
    }
}

@Composable
fun TopInvestorsTab(investors: List<InvestorEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Top 20 Publicly Verifiable Investors & Funds",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Tracking institutional allocations via Form 13F and public registry disclosures. No private credentials exposed.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        items(investors) { inv ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AmberPrimary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "#${inv.rank}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color(0xFF050B18),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = inv.investorName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = inv.fundName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Position Change: ${inv.positionChange}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = SignalBullish
                    )
                    Text(
                        text = "Target Focus: ${inv.targetCompanyOrAsset} • Size: ${inv.investmentAmount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Text(
                        text = "Reason / Signal: ${inv.reasonSignals}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Evidence: ${inv.evidence}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = inv.timestamp,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AmberPrimary
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
