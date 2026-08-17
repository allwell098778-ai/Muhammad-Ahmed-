package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
        NewsItemEntity::class,
        CompanyEntity::class,
        NewCompanyEntity::class,
        InvestorEntity::class,
        ExchangeEventEntity::class,
        AiVideoEntity::class,
        LeaderboardUser::class,
        ConversationEntity::class,
        MessageEntity::class,
        PrayerLogEntity::class,
        QuranRecordEntity::class,
        ZikrRecordEntity::class,
        AzanRecordingEntity::class,
        AzanGoalEntity::class,
        SystemAlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun newsDao(): NewsDao
    abstract fun companyDao(): CompanyDao
    abstract fun investorDao(): InvestorDao
    abstract fun exchangeDao(): ExchangeDao
    abstract fun aiVideoDao(): AiVideoDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun messagingDao(): MessagingDao
    abstract fun ibadahDao(): IbadahDao
    abstract fun alertsDao(): AlertsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "world_eagle_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }

        suspend fun seedDatabase(database: AppDatabase) {
            // Seed News Items
            database.newsDao().insertNews(
                listOf(
                    NewsItemEntity(
                        headline = "Global Semiconductor Foundry Breakthrough Accelerates Sub-2nm Nodes",
                        whatHappened = "Leading chip foundries finalized extreme ultraviolet packaging architecture, reducing latency across next-gen computing clusters.",
                        source = "Global Tech Intelligence / IEEE Public Dispatch",
                        publishedTime = "12m ago",
                        detectionTime = "14m ago (Detection Lead: +2m)",
                        impactType = "BULLISH",
                        possibleMarketImpact = "Substantial upside for advanced fabrication suppliers and AI accelerator OEMs.",
                        affectedAssets = "NVDA, TSM, ASML, SOXX",
                        evidence = "Patent filing #US2026-981042 verified with international registry; fab equipment logistics confirmed.",
                        romanUrduEnglishExpl = "Yeh breakdown fab equipment ki supply chain mein tez expansion ko confirm karta hai. Advanced packaging ki demand bohot tezi se barh rahi hai.",
                        confidence = 94,
                        probability = 91,
                        isConfirmed = true,
                        isEarlySignal = true,
                        isImportantImpact = true
                    ),
                    NewsItemEntity(
                        headline = "Crude Oil Maritime Routing Adjusted Following Strategic Strait Maintenance",
                        whatHappened = "Shipping consortiums adjusted transit schedules for Suez and Malacca corridors due to multi-port dredging operations.",
                        source = "International Maritime Tracking Service",
                        publishedTime = "35m ago",
                        detectionTime = "41m ago (Detection Lead: +6m)",
                        impactType = "HIGH_IMPACT",
                        possibleMarketImpact = "Short term tanker freight rate spike; temporary logistics inventory variance.",
                        affectedAssets = "BRENT, WTI, FRO, STNG",
                        evidence = "Automated AIS satellite vessel speed tracking showed 14 tanker re-routes verified on public logs.",
                        romanUrduEnglishExpl = "Maritime cargo routes par short-term congestion hai, tanker freight rates mein movement expect ki ja sakti hai.",
                        confidence = 88,
                        probability = 85,
                        isConfirmed = true,
                        isEarlySignal = true,
                        isImportantImpact = true
                    ),
                    NewsItemEntity(
                        headline = "Central Bank Digital Settlement Protocol Interoperability Test Cleared",
                        whatHappened = "Cross-border wholesale clearing trial between 8 central institutions successfully settled multi-currency liquidity tests.",
                        source = "Bank for International Settlements (BIS) Bulletin",
                        publishedTime = "1h ago",
                        detectionTime = "1h 5m ago",
                        impactType = "BULLISH",
                        possibleMarketImpact = "Lower remittance friction and institutional liquidity efficiency.",
                        affectedAssets = "FX Liquidity, Tier 1 Banking Networks",
                        evidence = "Public ledger proof-of-concept repository committed 200,000 synthetic transaction validations.",
                        romanUrduEnglishExpl = "Wholesale clearing speeds barhne se institutional remittance costs kam hongi aur speed double hogi.",
                        confidence = 96,
                        probability = 95,
                        isConfirmed = true,
                        isEarlySignal = false,
                        isImportantImpact = false
                    ),
                    NewsItemEntity(
                        headline = "Critical Rare Earth Supply Protocol Signed with Asian Refining Corridor",
                        whatHappened = "Bilateral agreement guarantees 40,000 metric tons of battery-grade lithium and neodymium reserves through 2029.",
                        source = "Mining & Mineral Registry Gazette",
                        publishedTime = "2h ago",
                        detectionTime = "2h 12m ago",
                        impactType = "NEUTRAL",
                        possibleMarketImpact = "Stabilizes EV battery raw material spot prices for tier-1 automotive manufacturing.",
                        affectedAssets = "LIT, ALB, SQM, TSLA",
                        evidence = "Government commerce department bilateral trade communiqué publicly ratified.",
                        romanUrduEnglishExpl = "Battery raw material supply stable hone se long-term EV production cost predictable rahegi.",
                        confidence = 90,
                        probability = 89,
                        isConfirmed = true,
                        isEarlySignal = true,
                        isImportantImpact = false
                    ),
                    NewsItemEntity(
                        headline = "Regulatory Review on Algorithmic High-Frequency Arbitrage Execution Latency",
                        whatHappened = "Global financial regulators initiated consultation on co-location microsecond timestamping requirements.",
                        source = "Securities Industry Monitor",
                        publishedTime = "3h ago",
                        detectionTime = "3h 8m ago",
                        impactType = "BEARISH",
                        possibleMarketImpact = "Elevated compliance overhead for ultra-low-latency market makers.",
                        affectedAssets = "Prop Trading Desks, Exchange Operators",
                        evidence = "Regulatory working paper draft #WP-2026 published for public comment.",
                        romanUrduEnglishExpl = "High-frequency trading firms ke liye reporting compliance barh sakti hai, order execution audit strict hoga.",
                        confidence = 82,
                        probability = 78,
                        isConfirmed = true,
                        isEarlySignal = true,
                        isImportantImpact = true
                    )
                )
            )

            // Seed Companies
            database.companyDao().insertCompanies(
                listOf(
                    CompanyEntity(
                        name = "NVIDIA Corporation",
                        symbol = "NVDA",
                        sector = "Semiconductors & AI Hardware",
                        logoEmoji = "⚡",
                        positiveNews = "Next-generation quantum-classical hybrid compute framework integration announced.",
                        negativeNews = "Export restriction guidelines under periodic inter-agency review.",
                        earnings = "Revenue +122% YoY; Data center segment operating margins at 76%.",
                        ceoName = "Jensen Huang",
                        productLaunches = "Blackwell Ultra B200 accelerators & Grace-CPU rack clusters.",
                        partnerships = "Cloud hyperscalers, sovereign enterprise research labs.",
                        acquisitions = "Strategic AI networking silicon portfolio integration.",
                        regulatoryNews = "Global compliance certifications up to date.",
                        marketImpact = "Bullish foundational hardware index driver.",
                        source = "SEC Filings & Company Press Releases",
                        timestamp = "Today, 08:30 PKT",
                        confidence = 95
                    ),
                    CompanyEntity(
                        name = "Microsoft Corporation",
                        symbol = "MSFT",
                        sector = "Cloud Computing & Enterprise Software",
                        logoEmoji = "☁️",
                        positiveNews = "Azure AI enterprise subscriptions grew 44% with autonomous agents adoption.",
                        negativeNews = "Data center power infrastructure grid connection wait times in select regions.",
                        earnings = "Commercial Cloud revenue exceeded $38.9B in latest quarter.",
                        ceoName = "Satya Nadella",
                        productLaunches = "Copilot Studio Enterprise 2.0 with local edge execution.",
                        partnerships = "Global Tier-1 telecommunications providers for 5G edge AI.",
                        acquisitions = "High-efficiency server thermal dissipation provider.",
                        regulatoryNews = "AI safety red-teaming audits publicly cleared.",
                        marketImpact = "High stability enterprise growth baseline.",
                        source = "Quarterly Form 10-Q",
                        timestamp = "Today, 07:15 PKT",
                        confidence = 94
                    ),
                    CompanyEntity(
                        name = "Tesla, Inc.",
                        symbol = "TSLA",
                        sector = "Autonomous Mobility & Energy Systems",
                        logoEmoji = "🚗",
                        positiveNews = "Megapack energy storage installations reached record 9.4 GWh quarterly run-rate.",
                        negativeNews = "Global automotive price elasticity causing competitive margin adjustments.",
                        earnings = "Energy generation and storage gross margin reached 24.6%.",
                        ceoName = "Elon Musk",
                        productLaunches = "Unsupervised Full Self-Driving rollout in target pilot cities.",
                        partnerships = "National utility grid operators for distributed battery balancing.",
                        acquisitions = "Advanced cathode dry-coating precision manufacturing line.",
                        regulatoryNews = "NHTSA autonomous driving telemetry standards conformance filed.",
                        marketImpact = "High beta market catalyst across clean tech and robotics.",
                        source = "Investor Relations Public Webcast",
                        timestamp = "Yesterday, 21:00 PKT",
                        confidence = 91
                    ),
                    CompanyEntity(
                        name = "Aramco Energy",
                        symbol = "2222.SR",
                        sector = "Integrated Energy & Petrochemicals",
                        logoEmoji = "🛢️",
                        positiveNews = "Hydrogen & low-carbon ammonia export terminal commissioning on schedule.",
                        negativeNews = "OPEC+ voluntary production quota baseline adherence.",
                        earnings = "Quarterly free cash flow generation $22.1B; dividend yield sustained.",
                        ceoName = "Amin H. Nasser",
                        productLaunches = "Blue hydrogen maritime fuel commercial standard.",
                        partnerships = "Asian refining giants for long-term crude-to-chemicals conversion.",
                        acquisitions = "Downstream international retail fuels distribution network.",
                        regulatoryNews = "Scope 1 and Scope 2 emissions monitoring audit verified.",
                        marketImpact = "Global energy liquidity and dividend anchor.",
                        source = "Tadawul Market Disclosures",
                        timestamp = "Yesterday, 18:45 PKT",
                        confidence = 93
                    )
                )
            )

            // Seed New Companies / Startups
            database.companyDao().insertNewCompanies(
                listOf(
                    NewCompanyEntity(
                        companyName = "AeroVolt Hydrogen Dynamics",
                        founder = "Dr. Tariq Mansoor & Elena Rostova",
                        ceo = "Dr. Tariq Mansoor",
                        businessSummary = "Developing high-density cryogenic hydrogen fuel cells for regional air freight.",
                        products = "H2-Pod 750kW Modular Aero Powertrain",
                        businessModel = "B2B powertrain leasing and hydrogen fuel distribution infrastructure.",
                        funding = "$84 Million Series B (Announced June 2026)",
                        investors = "Clean Aviation Ventures, Global Climate Fund, Horizons Capital",
                        plannedLaunchDate = "Q4 2026 Flight Certification / Listing target 2027",
                        expectedImpact = "Zero-emission regional cargo transport disruption.",
                        evidence = "FAA experimental airworthiness certificate #EX-8902 published.",
                        source = "Aviation Technology Journal & SEC Form D",
                        isVerified = true
                    ),
                    NewCompanyEntity(
                        companyName = "NeuroGraph Quantum AI",
                        founder = "Prof. Kenji Takahashi",
                        ceo = "Siddharth Rao",
                        businessSummary = "Room-temperature topological photonic processor for high-frequency neural modeling.",
                        products = "PhotonMatrix-8 Co-processor",
                        businessModel = "Enterprise cloud API for drug discovery and algorithmic financial simulations.",
                        funding = "$45 Million Seed + Series A",
                        investors = "Quantum Nexus, Tokyo Tech Angels, Apex Frontiers",
                        plannedLaunchDate = "Commercial SDK Beta November 2026",
                        expectedImpact = "100x acceleration in complex molecular folding computations.",
                        evidence = "Nature Photonics paper citation and public GitHub SDK repository.",
                        source = "International Quantum Computing Symposium Disclosures",
                        isVerified = true
                    ),
                    NewCompanyEntity(
                        companyName = "Solaris Orbital Power",
                        founder = "Marcus Vance",
                        ceo = "Marcus Vance",
                        businessSummary = "Space-based solar concentrators with microwave power beaming to terrestrial micro-grids.",
                        products = "Helios-1 CubeSat Constellation",
                        businessModel = "Emergency power agreements with island nations and remote research installations.",
                        funding = "Not publicly verified",
                        investors = "Undisclosed Angel Syndicate",
                        plannedLaunchDate = "Target Prototype Launch Q1 2027",
                        expectedImpact = "Continuous uninterrupted renewable base-load power concept.",
                        evidence = "Orbital frequency allocation filing with International Telecommunication Union (ITU).",
                        source = "ITU Space Frequency Registry",
                        isVerified = false
                    )
                )
            )

            // Seed Top 20 Investors
            database.investorDao().insertInvestors(
                listOf(
                    InvestorEntity(
                        rank = 1,
                        investorName = "Warren Buffett / Berkshire Hathaway",
                        fundName = "Berkshire Hathaway Portfolio",
                        targetCompanyOrAsset = "Energy Infrastructure & Treasury Holdings",
                        investmentAmount = "$325 Billion verifiable equity portfolio",
                        positionChange = "Increased short-duration T-Bills; steady energy utility holdings",
                        reasonSignals = "Preserving record cash reserves for value dislocation opportunities.",
                        evidence = "SEC Form 13F Quarter Filing #0001067983 verified.",
                        source = "SEC EDGAR Public Database",
                        timestamp = "Latest 13F Cycle"
                    ),
                    InvestorEntity(
                        rank = 2,
                        investorName = "Larry Fink / BlackRock Inc.",
                        fundName = "Global Systemic Allocation Fund",
                        targetCompanyOrAsset = "Global AI Infrastructure & Clean Power Grids",
                        investmentAmount = "$30 Billion Infrastructure Fund",
                        positionChange = "Heavy institutional allocation to hyperscale data center power providers.",
                        reasonSignals = "AI electricity demand forecasted to expand 160% by 2030.",
                        evidence = "BlackRock Global Infrastructure Partners prospectus.",
                        source = "Public Shareholder Annual Report",
                        timestamp = "August 2026"
                    ),
                    InvestorEntity(
                        rank = 3,
                        investorName = "Cathie Wood / ARK Investment",
                        fundName = "ARK Innovation ETF (ARKK)",
                        targetCompanyOrAsset = "Autonomous Robotics & Genomics",
                        investmentAmount = "$8.4 Billion AUM",
                        positionChange = "Increased stake in autonomous delivery platforms (+4.2%).",
                        reasonSignals = "Robo-taxi platform unit economics inflection point.",
                        evidence = "Daily trade disclosure emails and daily fund transparency report.",
                        source = "ARK Transparency Feed",
                        timestamp = "Yesterday"
                    ),
                    InvestorEntity(
                        rank = 4,
                        investorName = "Stanley Druckenmiller / Duquesne",
                        fundName = "Duquesne Family Office",
                        targetCompanyOrAsset = "Copper Futures & Sovereign Debt Spreads",
                        investmentAmount = "$3.2 Billion estimated positions",
                        positionChange = "Rotated equity gains into industrial electrification commodities.",
                        reasonSignals = "Global grid modernization and electric vehicle wiring demand.",
                        evidence = "Commodity Futures Trading Commission (CFTC) Large Trader report.",
                        source = "CFTC Public Commitments of Traders",
                        timestamp = "3 days ago"
                    ),
                    InvestorEntity(
                        rank = 5,
                        investorName = "Masayoshi Son / SoftBank Group",
                        fundName = "Vision Fund AI Core",
                        targetCompanyOrAsset = "Semiconductor Intellectual Property & Edge Robotics",
                        investmentAmount = "$12 Billion Project Izanagi Allocation",
                        positionChange = "Expanded direct equity investments in specialized AI robotics startups.",
                        reasonSignals = "Artificial Super Intelligence (ASI) foundational stack development.",
                        evidence = "Tokyo Stock Exchange regulatory disclosure #TSE-9984.",
                        source = "TSE Disclosures",
                        timestamp = "1 week ago"
                    ),
                    InvestorEntity(
                        rank = 6,
                        investorName = "Ken Griffin / Citadel LLC",
                        fundName = "Citadel Multi-Strategy Master Fund",
                        targetCompanyOrAsset = "Statistical Arbitrage & Global Fixed Income",
                        investmentAmount = "$65 Billion AUM",
                        positionChange = "Optimized Treasury spread arbitrage across 2Y/10Y yield curves.",
                        reasonSignals = "Interest rate policy easing divergence between G10 central banks.",
                        evidence = "Form ADV Part 2A annual update.",
                        source = "FINRA BrokerCheck & SEC Registrations",
                        timestamp = "August 2026"
                    ),
                    InvestorEntity(
                        rank = 7,
                        investorName = "Ray Dalio / Bridgewater Associates",
                        fundName = "Pure Alpha & All Weather Fund",
                        targetCompanyOrAsset = "Inflation-Linked Bonds & Physical Gold Reserves",
                        investmentAmount = "$112 Billion institutional assets",
                        positionChange = "Maintained strategic hedge weighting against geopolitical trade shifts.",
                        reasonSignals = "Macroeconomic long-debt cycle dynamic balancing.",
                        evidence = "Bridgewater Daily Observations institutional bulletin.",
                        source = "Institutional Macro Briefing",
                        timestamp = "August 2026"
                    ),
                    InvestorEntity(
                        rank = 8,
                        investorName = "Bill Ackman / Pershing Square",
                        fundName = "Pershing Square Capital Management",
                        targetCompanyOrAsset = "Consumer Staples & Quick Service Franchises",
                        investmentAmount = "$10.5 Billion Concentrated Portfolio",
                        positionChange = "Added 1.2M shares in multinational hospitality brand.",
                        reasonSignals = "Predictable free cash flow and strong consumer pricing power.",
                        evidence = "SEC Schedule 13D filing.",
                        source = "SEC EDGAR",
                        timestamp = "August 2026"
                    ),
                    InvestorEntity(
                        rank = 9,
                        investorName = "David Tepper / Appaloosa LP",
                        fundName = "Appaloosa Management",
                        targetCompanyOrAsset = "Emerging Market Tech & Semiconductor ETFs",
                        investmentAmount = "$5.8 Billion position value",
                        positionChange = "Maintained overweight call options on Asian cloud leaders.",
                        reasonSignals = "Valuation multiple discount relative to Western peers.",
                        evidence = "Form 13F holdings statement.",
                        source = "SEC Disclosures",
                        timestamp = "August 2026"
                    ),
                    InvestorEntity(
                        rank = 10,
                        investorName = "Howard Marks / Oaktree Capital",
                        fundName = "Oaktree Distressed Debt Fund XII",
                        targetCompanyOrAsset = "Senior Secured Commercial Real Estate Debt",
                        investmentAmount = "$18.9 Billion committed capital",
                        positionChange = "Deployed $3.2B in senior mezzanine debt for prime logistics hubs.",
                        reasonSignals = "High risk-adjusted yields with solid asset backing.",
                        evidence = "Brookfield Asset Management quarterly partnership review.",
                        source = "Public Investor Call",
                        timestamp = "August 2026"
                    )
                )
            )

            // Seed Exchange Events (Binance, MEXC, WEEX - Strict independent adapters)
            database.exchangeDao().insertEvents(
                listOf(
                    // Binance
                    ExchangeEventEntity(
                        exchange = "BINANCE",
                        eventType = "WHALE_ACTIVITY",
                        assetPair = "BTC/USDT",
                        title = "Anonymous Whale Clustered Accumulation (Activity ID #BN-8802)",
                        details = "Algorithmic TWAP order executed 1,420 BTC across 18 micro-bursts without triggering slippage above 0.04%.",
                        estimatedVolume = "$92.3M USD",
                        confidence = 92,
                        timestamp = "8m ago",
                        anonymousWhaleId = "Whale-Cluster-9941 (Public Ledger Aggregation)",
                        signalClassification = "🟢 Bullish"
                    ),
                    ExchangeEventEntity(
                        exchange = "BINANCE",
                        eventType = "ORDER_BOOK",
                        assetPair = "ETH/USDT",
                        title = "Significant Bid Wall Placed at Key Psychological Support",
                        details = "Aggregated limit buy orders totaling 14,500 ETH staged on depth chart between 0.5% buffer.",
                        estimatedVolume = "$48.1M USD",
                        confidence = 89,
                        timestamp = "22m ago",
                        anonymousWhaleId = "Cluster-ETH-402",
                        signalClassification = "🟢 Bullish"
                    ),
                    ExchangeEventEntity(
                        exchange = "BINANCE",
                        eventType = "LIQUIDATION",
                        assetPair = "SOL/USDT",
                        title = "Cascading Short Liquidation Event",
                        details = "Rapid price delta +2.8% triggered $18.4M short squeeze across perpetual contracts within 3 minutes.",
                        estimatedVolume = "$18.4M USD",
                        confidence = 96,
                        timestamp = "45m ago",
                        anonymousWhaleId = "Market-Systemic-Liquidation",
                        signalClassification = "⚠️ High Volatility"
                    ),

                    // MEXC
                    ExchangeEventEntity(
                        exchange = "MEXC",
                        eventType = "UNUSUAL_VOLUME",
                        assetPair = "AI-AGENTS/USDT",
                        title = "Early Stage AI Protocol 24h Volume Expansion +410%",
                        details = "Spot order book traded $34.2M equivalent against 7-day average of $6.8M; active taker ratio 68%.",
                        estimatedVolume = "$34.2M USD",
                        confidence = 85,
                        timestamp = "14m ago",
                        anonymousWhaleId = "MEXC-Volume-Alert #391",
                        signalClassification = "🟢 Bullish"
                    ),
                    ExchangeEventEntity(
                        exchange = "MEXC",
                        eventType = "LARGE_TRADE",
                        assetPair = "RWA-TOKEN/USDT",
                        title = "Institutional Sized Market Buy on Real-World Asset Pair",
                        details = "Single transaction executed 850,000 tokens directly consuming top 4 order book ask tiers.",
                        estimatedVolume = "$4.8M USD",
                        confidence = 91,
                        timestamp = "38m ago",
                        anonymousWhaleId = "Whale-ID-MX71",
                        signalClassification = "🟢 Bullish"
                    ),

                    // WEEX
                    ExchangeEventEntity(
                        exchange = "WEEX",
                        eventType = "WHALE_ACTIVITY",
                        assetPair = "BTC/USDT Perp",
                        title = "Futures Open Interest Spike with Low Funding Rate",
                        details = "Open interest increased by 2,200 BTC in 1 hour while weighted funding rate remained neutral at 0.005%.",
                        estimatedVolume = "$140M USD Notional",
                        confidence = 87,
                        timestamp = "19m ago",
                        anonymousWhaleId = "WEEX-Whale-Position-81",
                        signalClassification = "🟢 Bullish"
                    ),
                    ExchangeEventEntity(
                        exchange = "WEEX",
                        eventType = "LIQUIDATION",
                        assetPair = "DOGE/USDT Perp",
                        title = "Long Position Rebalancing Flush",
                        details = "Leveraged long positions liquidated following sudden 1.8% intraday pullback.",
                        estimatedVolume = "$6.2M USD",
                        confidence = 94,
                        timestamp = "1h ago",
                        anonymousWhaleId = "WEEX-Liq-Engine",
                        signalClassification = "🔴 Bearish Flush"
                    )
                )
            )

            // Seed AI Videos
            database.aiVideoDao().insertVideos(
                listOf(
                    AiVideoEntity(
                        title = "Global Market Intelligence Briefing #1: Next-Gen Semiconductor Paradigm",
                        summary = "Deep dive analysis into extreme ultraviolet lithography supply bottlenecks and institutional capital positioning.",
                        romanEnglishScript = "Ajj ke briefing mein hum discuss karenge sub-2nm chip foundries ka naya packaging standard. Kaise global tech companies AI infrastructure build-out ko accelerate kar rahi hain aur institutional investors kahan capital allocate kar rahe hain. Complete verifiable data points aur evidence ke sath Muhammad Ahmed ke analysis dekhein.",
                        source = "Verified IEEE Disclosures & SEC Filings",
                        publishedDate = "August 17, 2026 (Daily Video #1)",
                        durationText = "03:45",
                        videoCategory = "Market Intelligence",
                        creatorCredit = "MUHAMMAD AHMED",
                        brandingTag = "WORLD EAGLE",
                        thumbnailGradientIndex = 0,
                        viewsCount = 2840
                    ),
                    AiVideoEntity(
                        title = "Crypto Whale Flow & Liquidity Intelligence #2: Central Bank Digital Cross-Border",
                        summary = "Cross-border clearing trials review, Binance/MEXC/WEEX whale volume tracking, and market structure outlook.",
                        romanEnglishScript = "Ajj ka doosra video focus karta hai top exchanges ke anomalous whale volumes aur central bank cross-border settlement tests par. Kaise $92M BTC cluster accumulation execute hua bina market slippage ke. Poori detail Roman English summary ke sath Muhammad Ahmed ke world eagle platform par.",
                        source = "On-Chain Analytics & BIS Bulletin",
                        publishedDate = "August 17, 2026 (Daily Video #2)",
                        durationText = "04:12",
                        videoCategory = "Crypto & Macro",
                        creatorCredit = "MUHAMMAD AHMED",
                        brandingTag = "WORLD EAGLE",
                        thumbnailGradientIndex = 1,
                        viewsCount = 3190
                    )
                )
            )

            // Seed Leaderboard Users
            database.leaderboardDao().insertLeaderboard(
                listOf(
                    LeaderboardUser(
                        username = "Investor1",
                        fullName = "Muhammad Ahmed",
                        dpUrl = "",
                        publicActivity = "Founder & Chief Intelligence Architect; 140 Verified Signal Reviews",
                        publicScore = 9850,
                        rank = 1,
                        isVerified = true
                    ),
                    LeaderboardUser(
                        username = "AlphaEagle_Global",
                        fullName = "Dr. Zeeshan Tariq",
                        dpUrl = "",
                        publicActivity = "Active market researcher; 92 Signal contributions",
                        publicScore = 8420,
                        rank = 2,
                        isVerified = true
                    ),
                    LeaderboardUser(
                        username = "MacroFalcon",
                        fullName = "Sarah Jenkins",
                        dpUrl = "",
                        publicActivity = "FX & Commodities Analyst; 76 Dispatches",
                        publicScore = 7890,
                        rank = 3,
                        isVerified = true
                    ),
                    LeaderboardUser(
                        username = "WhaleTracker_PK",
                        fullName = "Hamza Farooq",
                        dpUrl = "",
                        publicActivity = "Exchange Order Book Specialist; 64 Reports",
                        publicScore = 6940,
                        rank = 4,
                        isVerified = false
                    ),
                    LeaderboardUser(
                        username = "SovereignAnalyst",
                        fullName = "Amina Al-Mansoor",
                        dpUrl = "",
                        publicActivity = "Clean Energy & Rare Earth Specialist",
                        publicScore = 6120,
                        rank = 5,
                        isVerified = true
                    )
                )
            )

            // Seed Community Conversations
            val conv1Id = database.messagingDao().insertConversation(
                ConversationEntity(
                    title = "World Eagle Core Strategy",
                    isGroup = true,
                    participantUsernames = "Investor1, AlphaEagle_Global, MacroFalcon",
                    lastMessage = "Sub-2nm semiconductor signal evidence verified across public patent logs.",
                    lastMessageTime = "10:15 PKT",
                    isLocked = false,
                    unreadCount = 2,
                    avatarEmoji = "🦅"
                )
            )

            database.messagingDao().insertMessage(
                MessageEntity(
                    conversationId = conv1Id,
                    senderUsername = "AlphaEagle_Global",
                    senderFullName = "Dr. Zeeshan Tariq",
                    text = "As-salamu alaykum team! Checking the latest sub-2nm foundry detection lead.",
                    timestamp = "10:10 PKT",
                    isMe = false
                )
            )
            database.messagingDao().insertMessage(
                MessageEntity(
                    conversationId = conv1Id,
                    senderUsername = "Investor1",
                    senderFullName = "Muhammad Ahmed",
                    text = "Sub-2nm semiconductor signal evidence verified across public patent logs. 94% confidence score attached.",
                    timestamp = "10:15 PKT",
                    isMe = true
                )
            )

            val conv2Id = database.messagingDao().insertConversation(
                ConversationEntity(
                    title = "Whale Activity Direct Desk",
                    isGroup = false,
                    participantUsernames = "Investor1, WhaleTracker_PK",
                    lastMessage = "Binance cluster TWAP finished smoothly.",
                    lastMessageTime = "09:40 PKT",
                    isLocked = false,
                    unreadCount = 0,
                    avatarEmoji = "🐋"
                )
            )

            database.messagingDao().insertMessage(
                MessageEntity(
                    conversationId = conv2Id,
                    senderUsername = "WhaleTracker_PK",
                    senderFullName = "Hamza Farooq",
                    text = "Binance cluster TWAP finished smoothly. $92M notional volume recorded.",
                    timestamp = "09:40 PKT",
                    isMe = false
                )
            )

            // Seed Default User Accounts
            if (database.userDao().getUserCount() == 0) {
                // SHA-256 for "password123": ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
                val defaultAdmin = UserEntity(
                    id = 1,
                    fullName = "Muhammad Ahmed",
                    username = "Investor1",
                    email = "investor@worldeagle.com",
                    passwordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f",
                    dpUrl = "🦅",
                    bio = "Founder & Chief Intelligence Architect; Market Lead Specialist",
                    country = "Pakistan",
                    timezone = "UTC+5 (PKT)",
                    phone = "+92 300 1234567",
                    dob = "1994-05-12",
                    role = "ADMIN",
                    joinedDate = "January 2026",
                    isLeaderboardVisible = true,
                    isOnline = true
                )
                val defaultAnalyst = UserEntity(
                    id = 2,
                    fullName = "Dr. Zeeshan Tariq",
                    username = "AlphaEagle_Global",
                    email = "zeeshan@worldeagle.com",
                    passwordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f",
                    dpUrl = "⚡",
                    bio = "Active market researcher; 92 Signal contributions",
                    country = "Pakistan",
                    timezone = "UTC+5 (PKT)",
                    phone = "+92 301 7654321",
                    dob = "1991-08-20",
                    role = "USER",
                    joinedDate = "February 2026",
                    isLeaderboardVisible = true,
                    isOnline = true
                )
                database.userDao().insertUser(defaultAdmin)
                database.userDao().insertUser(defaultAnalyst)
            }

            // Seed System Alerts
            database.alertsDao().insertAlert(
                SystemAlertEntity(
                    title = "System Security Notice: 2FA & Chat Lock Active",
                    message = "Your World Eagle account is protected with cryptographic password hashing and strict session management.",
                    alertType = "SECURITY",
                    timestamp = "Today, 08:00 PKT"
                )
            )
            database.alertsDao().insertAlert(
                SystemAlertEntity(
                    title = "Market Signal Alert: Binance BTC Whale Cluster",
                    message = "Significant anonymous clustered volume detected. Review signals tab for confidence metrics.",
                    alertType = "MARKET_ALERT",
                    timestamp = "Today, 09:30 PKT"
                )
            )
        }
    }
}
