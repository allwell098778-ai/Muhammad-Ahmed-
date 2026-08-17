package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientFrostedBackground
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.WorldEagleBrandingHeader
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var selectedAvatarEmoji by remember { mutableStateOf("🦅") }
    var bio by remember { mutableStateOf("Market intelligence enthusiast") }
    var country by remember { mutableStateOf("Pakistan") }
    var timezone by remember { mutableStateOf("UTC+5 (PKT)") }
    var phone by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var suggestedUsernames by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }

    val avatars = listOf("🦅", "⚡", "🛡️", "💼", "📈", "🕌", "🌟", "👑")
    val scrollState = rememberScrollState()

    AmbientFrostedBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .verticalScroll(scrollState),
                cornerRadius = 32.dp,
                containerColor = Color(0x12FFFFFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WorldEagleBrandingHeader(compact = true)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Create World Eagle Account",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Join global analysts, institutional trackers & community",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Avatar / DP selector
                    Text(
                        text = "Select Profile Picture / Avatar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberLight,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(avatars) { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedAvatarEmoji == emoji) AmberPrimary.copy(alpha = 0.25f)
                                        else Color(0x14FFFFFF)
                                    )
                                    .border(
                                        if (selectedAvatarEmoji == emoji) 2.dp else 1.dp,
                                        if (selectedAvatarEmoji == emoji) AmberPrimary else Color(0x24FFFFFF),
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 22.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x14FFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF),
                        focusedBorderColor = AmberPrimary,
                        unfocusedBorderColor = Color(0x24FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = AmberLight,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("Muhammad Ahmed", color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AmberPrimary) },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("signup_fullname_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Unique Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                            suggestedUsernames = emptyList()
                        },
                        label = { Text("Username (Unique Globally) *") },
                        placeholder = { Text("e.g. Investor1", color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = AmberPrimary) },
                        isError = usernameError != null,
                        supportingText = {
                            if (usernameError != null) {
                                Text(text = usernameError ?: "", color = SignalBearish)
                            }
                        },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("signup_username_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    // If username is taken, show suggestions
                    if (suggestedUsernames.isNotEmpty()) {
                        Text(
                            text = "Suggested alternatives:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AmberLight,
                            modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suggestedUsernames.take(3).forEach { alt ->
                                SuggestionChip(
                                    onClick = {
                                        username = alt
                                        usernameError = null
                                        suggestedUsernames = emptyList()
                                    },
                                    label = { Text(alt, style = MaterialTheme.typography.labelSmall, color = Color.White) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0x1AFFFFFF))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address *") },
                        placeholder = { Text("ahmed@worldeagle.com", color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AmberPrimary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password * (Min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password *") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = AmberPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth().testTag("signup_confirm_password_input"),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bio
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        placeholder = { Text("Market intelligence enthusiast", color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = AmberPrimary) },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Country & Timezone in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            colors = fieldColors,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = timezone,
                            onValueChange = { timezone = it },
                            label = { Text("Timezone") },
                            colors = fieldColors,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Phone & DOB
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone (Optional)") },
                            colors = fieldColors,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = dob,
                            onValueChange = { dob = it },
                            label = { Text("DOB (Optional)") },
                            placeholder = { Text("YYYY-MM-DD", color = Color(0xFF64748B)) },
                            colors = fieldColors,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Create Account Button
                    Button(
                        onClick = {
                            isSubmitting = true
                            viewModel.register(
                                fullName = fullName,
                                username = username,
                                email = email,
                                pass = password,
                                confirmPass = confirmPassword,
                                dpUrl = selectedAvatarEmoji,
                                bio = bio,
                                country = country,
                                timezone = timezone,
                                phone = phone,
                                dob = dob,
                                onSuccess = {
                                    isSubmitting = false
                                },
                                onError = { err, suggestions ->
                                    isSubmitting = false
                                    if (err.contains("Username already taken", ignoreCase = true)) {
                                        usernameError = "Username already taken"
                                        suggestedUsernames = suggestions
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("signup_submit_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color(0xFF050B18)
                        ),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color(0xFF050B18), modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CREATE ACCOUNT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Back to Login link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Already registered?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = { viewModel.navigateTo(AppDestination.LOGIN) },
                            modifier = Modifier.testTag("navigate_login_button")
                        ) {
                            Text(
                                text = "LOG IN HERE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = AmberPrimary
                            )
                        }
                    }

                    WorldEagleFooter()
                }
            }
        }
    }
}
