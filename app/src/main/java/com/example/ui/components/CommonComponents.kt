package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Ambient Frosted Glass Background with glowing Indigo & Amber aura orbs
 */
@Composable
fun AmbientFrostedBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FrostedBackgroundDark)
    ) {
        // Glowing Ambient Orbs Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Indigo ambient glow on top-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        FrostedGlowIndigo,
                        FrostedGlowIndigo.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(0f, size.height * 0.1f),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(0f, size.height * 0.1f)
            )

            // Amber ambient glow on center-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        FrostedGlowAmber,
                        FrostedGlowAmber.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 1.1f, size.height * 0.55f),
                    radius = size.width * 0.85f
                ),
                radius = size.width * 0.85f,
                center = Offset(size.width * 1.1f, size.height * 0.55f)
            )

            // Emerald / Mint ambient glow at bottom-left for subtle depth
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        FrostedGlowEmerald.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.95f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.1f, size.height * 0.95f)
            )
        }

        content()
    }
}

/**
 * Frosted Glass Card with translucent acrylic background and crisp luminous border
 */
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    borderBrush: Brush = Brush.verticalGradient(
        listOf(
            Color(0x33FFFFFF),
            Color(0x12FFFFFF),
            Color(0x08FFFFFF)
        )
    ),
    containerColor: Color = GlassSurfaceDark,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, borderBrush, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
fun WorldEagleBrandingHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Squircle Golden Gradient Emblem with 🦅
        Box(
            modifier = Modifier
                .size(if (compact) 54.dp else 80.dp)
                .clip(RoundedCornerShape(if (compact) 18.dp else 24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(AmberLight, AmberDark)
                    )
                )
                .border(
                    1.5.dp,
                    Color(0x4DFFFFFF),
                    RoundedCornerShape(if (compact) 18.dp else 24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🦅",
                fontSize = if (compact) 28.sp else 40.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "WORLD EAGLE",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = Color.White
        )

        if (!compact) {
            Text(
                text = "INTELLIGENCE & IBADAH",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                ),
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun WorldEagleFooter(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = Color(0x1FFFFFFF),
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CREATED BY ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.5.sp
                ),
                color = Color(0xFF64748B)
            )
            Text(
                text = "MUHAMMAD AHMED",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = AmberPrimary.copy(alpha = 0.9f)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "WORLD EAGLE • FROSTED GLASS TERMINAL",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun SignalClassificationBadge(
    impactType: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, text) = when (impactType.uppercase()) {
        "BULLISH" -> Triple(SignalBullish.copy(alpha = 0.18f), SignalBullish, "🟢 Bullish")
        "BEARISH" -> Triple(SignalBearish.copy(alpha = 0.18f), SignalBearish, "🔴 Bearish")
        "NEUTRAL" -> Triple(SignalNeutral.copy(alpha = 0.18f), SignalNeutral, "🟡 Neutral")
        "HIGH_IMPACT" -> Triple(SignalHighImpact.copy(alpha = 0.18f), SignalHighImpact, "⚠️ High Impact")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface, impactType)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatMetricBadge(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CallModalDialog(
    callType: String,
    contactName: String,
    onEndCall: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onEndCall,
        confirmButton = {
            Button(
                onClick = onEndCall,
                colors = ButtonDefaults.buttonColors(containerColor = SignalBearish)
            ) {
                Icon(Icons.Default.CallEnd, contentDescription = "End Call")
                Spacer(modifier = Modifier.width(6.dp))
                Text("End Call")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = null,
                    tint = SignalBullish
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("World Eagle $callType Call")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(EagleNavy700)
                        .border(2.dp, SignalBullish, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = contactName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "End-to-End Encrypted WebRTC Session (Active: 00:24)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SignalBullish
                )
            }
        }
    )
}
