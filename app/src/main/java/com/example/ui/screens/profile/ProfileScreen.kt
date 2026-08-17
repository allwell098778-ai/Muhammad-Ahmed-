package com.example.ui.screens.profile

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return
    var showPublicPreview by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x12FFFFFF)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.2f))
                            .border(3.dp, AmberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.dpUrl.ifBlank { "🦅" }, fontSize = 48.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        if (user.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = AmberPrimary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OWNER / ADMIN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color(0xFF050B18),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.titleSmall,
                        color = AmberPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(label = "Country", value = user.country)
                        ProfileStatItem(label = "Timezone", value = user.timezone)
                        ProfileStatItem(label = "Joined", value = user.joinedDate)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(AppDestination.EDIT_PROFILE) },
                            modifier = Modifier.weight(1f).testTag("edit_profile_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = Color(0xFF050B18)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showPublicPreview = !showPublicPreview },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (showPublicPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showPublicPreview) "Private View" else "Public View")
                        }
                    }
                }
            }
        }

        if (showPublicPreview) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EagleNavy800),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = SignalBullish)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Public Profile Preview",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SignalBullish
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Other members will see: ${user.fullName} (@${user.username}) from ${user.country}. Your password hash, private chat PIN, and email remain strictly concealed.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Settings Shortcuts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        title = "Security & Settings",
                        subtitle = "Chat PIN, active sessions, dark theme"
                    ) {
                        viewModel.navigateTo(AppDestination.SETTINGS)
                    }
                    if (user.role == "ADMIN") {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ProfileMenuRow(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Admin Command Center",
                            subtitle = "User registry, ban controls, system alerts",
                            iconColor = EagleGold
                        ) {
                            viewModel.navigateTo(AppDestination.ADMIN_PANEL)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ProfileMenuRow(
                        icon = Icons.Default.Logout,
                        title = "Logout Session",
                        subtitle = "Sign out and lock terminal",
                        iconColor = SignalBearish
                    ) {
                        viewModel.logout()
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
fun EditProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return

    var fullName by remember { mutableStateOf(user.fullName) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }
    var country by remember { mutableStateOf(user.country) }
    var timezone by remember { mutableStateOf(user.timezone) }
    var selectedAvatar by remember { mutableStateOf(user.dpUrl.ifBlank { "🦅" }) }

    val avatars = listOf("🦅", "⚡", "🛡️", "💼", "📈", "🕌", "🌟", "👑")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Edit World Eagle Profile",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Text(
                text = "Choose Profile Picture (DP)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(avatars) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (selectedAvatar == emoji) EagleGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(if (selectedAvatar == emoji) 2.dp else 1.dp, if (selectedAvatar == emoji) EagleGold else MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { selectedAvatar = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().testTag("edit_fullname_input"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username (Unique Globally)") },
                modifier = Modifier.fillMaxWidth().testTag("edit_username_input"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = timezone,
                    onValueChange = { timezone = it },
                    label = { Text("Timezone") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.updateProfile(
                        fullName = fullName,
                        username = username,
                        bio = bio,
                        country = country,
                        timezone = timezone,
                        dpUrl = selectedAvatar,
                        onSuccess = {
                            viewModel.navigateBack()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EagleGold, contentColor = EagleNavy900),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
