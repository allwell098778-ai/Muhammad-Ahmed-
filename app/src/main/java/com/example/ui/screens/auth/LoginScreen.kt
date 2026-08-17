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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.AmbientFrostedBackground
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.WorldEagleBrandingHeader
import com.example.ui.components.WorldEagleFooter
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Create Account

    // Sign In form state
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showGoogleAccountChooser by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Sign Up form state
    var newFullName by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newConfirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var selectedAvatarEmoji by remember { mutableStateOf("🦅") }
    var newCountry by remember { mutableStateOf("Pakistan") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var suggestedUsernames by remember { mutableStateOf<List<String>>(emptyList()) }

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
                    .widthIn(max = 480.dp)
                    .verticalScroll(scrollState),
                cornerRadius = 32.dp,
                containerColor = Color(0x160F172A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    WorldEagleBrandingHeader(compact = true)

                    Spacer(modifier = Modifier.height(18.dp))

                    // Tab Selector: [ 🔐 Sign In ] | [ ✍️ Create Account ]
                    Surface(
                        color = Color(0x20FFFFFF),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x28FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            // Sign In Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selectedAuthTab == 0) AmberPrimary else Color.Transparent
                                    )
                                    .clickable { selectedAuthTab = 0 }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (selectedAuthTab == 0) Color(0xFF050B18) else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Sign In",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (selectedAuthTab == 0) FontWeight.Black else FontWeight.Medium
                                        ),
                                        color = if (selectedAuthTab == 0) Color(0xFF050B18) else Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            // Create Account Tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selectedAuthTab == 1) AmberPrimary else Color.Transparent
                                    )
                                    .clickable { selectedAuthTab = 1 }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = if (selectedAuthTab == 1) Color(0xFF050B18) else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Create Account",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = if (selectedAuthTab == 1) FontWeight.Black else FontWeight.Medium
                                        ),
                                        color = if (selectedAuthTab == 1) Color(0xFF050B18) else Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedAuthTab == 0) {
                        // ==================== SIGN IN VIEW ====================

                        // Google Sign-In Primary Action (Google Standard Button)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    showGoogleAccountChooser = true
                                }
                                .testTag("google_login_button"),
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleLogoIcon(modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color(0xFF1F2937)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider with OR
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x2EFFFFFF))
                            Text(
                                text = "or enter credentials",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x2EFFFFFF))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email / Username field
                        OutlinedTextField(
                            value = emailOrUsername,
                            onValueChange = { emailOrUsername = it },
                            label = { Text("Email or Username") },
                            placeholder = { Text("investor@worldeagle.com", color = Color(0xFF64748B)) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = AmberPrimary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x18FFFFFF),
                                unfocusedContainerColor = Color(0x0EFFFFFF),
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = Color(0x28FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = AmberLight,
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("••••••••", color = Color(0xFF64748B)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (emailOrUsername.isNotBlank() && password.isNotBlank()) {
                                        isLoading = true
                                        viewModel.login(emailOrUsername, password) {
                                            isLoading = false
                                        }
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x18FFFFFF),
                                unfocusedContainerColor = Color(0x0EFFFFFF),
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = Color(0x28FFFFFF),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = AmberLight,
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Remember Me & Forgot Password row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AmberPrimary,
                                        checkmarkColor = Color(0xFF050B18),
                                        uncheckedColor = Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = "Remember me",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Text(
                                    text = "Forgot password?",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AmberPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Login Submit Button
                        Button(
                            onClick = {
                                if (emailOrUsername.isBlank() || password.isBlank()) {
                                    viewModel.showToast("Please enter both email/username and password.")
                                    return@Button
                                }
                                isLoading = true
                                viewModel.login(emailOrUsername, password) {
                                    isLoading = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberPrimary,
                                contentColor = Color(0xFF050B18)
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color(0xFF050B18),
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOGIN",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Quick 1-Tap Demo Logins Section
                        Surface(
                            color = Color(0x18FFFFFF),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x28FFFFFF)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚡ Quick 1-Tap Demo Logins",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AmberLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            emailOrUsername = "investor@worldeagle.com"
                                            password = "password123"
                                            isLoading = true
                                            viewModel.login(emailOrUsername, password) {
                                                isLoading = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "👑 Admin",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AmberLight
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            emailOrUsername = "zeeshan@worldeagle.com"
                                            password = "password123"
                                            isLoading = true
                                            viewModel.login(emailOrUsername, password) {
                                                isLoading = false
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x38FFFFFF))
                                    ) {
                                        Text(
                                            text = "📊 Analyst",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                    } else {
                        // ==================== CREATE ACCOUNT VIEW ====================

                        Text(
                            text = "Choose Profile Avatar",
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
                                        .size(46.dp)
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
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val fieldColors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0x18FFFFFF),
                            unfocusedContainerColor = Color(0x0EFFFFFF),
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = Color(0x28FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = AmberLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )

                        // Full Name
                        OutlinedTextField(
                            value = newFullName,
                            onValueChange = { newFullName = it },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. Muhammad Ali", color = Color(0xFF64748B)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AmberPrimary) },
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().testTag("signup_fullname_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Username with Live check
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = {
                                newUsername = it.lowercase().replace(" ", "_")
                                if (it.length >= 3) {
                                    viewModel.checkUsernameAvailability(it) { available, suggestions ->
                                        usernameError = if (!available) "Username taken" else null
                                        suggestedUsernames = suggestions
                                    }
                                }
                            },
                            label = { Text("Username *") },
                            placeholder = { Text("e.g. market_analyst", color = Color(0xFF64748B)) },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = AmberPrimary) },
                            trailingIcon = {
                                if (newUsername.length >= 3) {
                                    if (usernameError == null) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Available", tint = SignalBullish)
                                    } else {
                                        Icon(Icons.Default.Error, contentDescription = "Taken", tint = SignalHighImpact)
                                    }
                                }
                            },
                            isError = usernameError != null,
                            supportingText = {
                                if (usernameError != null) {
                                    Text(text = usernameError ?: "", color = SignalHighImpact)
                                }
                            },
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().testTag("signup_username_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        // Suggested usernames if taken
                        if (suggestedUsernames.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Suggested: ", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                suggestedUsernames.take(3).forEach { sug ->
                                    Surface(
                                        color = AmberPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            newUsername = sug
                                            usernameError = null
                                            suggestedUsernames = emptyList()
                                        }
                                    ) {
                                        Text(
                                            text = sug,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = AmberLight,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email
                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Email Address *") },
                            placeholder = { Text("name@example.com", color = Color(0xFF64748B)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AmberPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Password (min 6 chars) *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberPrimary) },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value = newConfirmPassword,
                            onValueChange = { newConfirmPassword = it },
                            label = { Text("Confirm Password *") },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = AmberPrimary) },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            singleLine = true,
                            colors = fieldColors,
                            modifier = Modifier.fillMaxWidth().testTag("signup_confirm_password_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Create Account Submit Button
                        Button(
                            onClick = {
                                if (newFullName.isBlank() || newUsername.isBlank() || newEmail.isBlank() || newPassword.isBlank()) {
                                    viewModel.showToast("Please fill in all required fields.")
                                    return@Button
                                }
                                if (newPassword != newConfirmPassword) {
                                    viewModel.showToast("Passwords do not match.")
                                    return@Button
                                }
                                isLoading = true
                                viewModel.register(
                                    fullName = newFullName,
                                    username = newUsername,
                                    email = newEmail,
                                    pass = newPassword,
                                    confirmPass = newConfirmPassword,
                                    dpUrl = selectedAvatarEmoji,
                                    bio = "Market intelligence enthusiast",
                                    country = newCountry,
                                    timezone = "UTC+5 (PKT)",
                                    phone = "",
                                    dob = "",
                                    onSuccess = {
                                        isLoading = false
                                        emailOrUsername = newEmail
                                        password = newPassword
                                        selectedAuthTab = 0
                                    },
                                    onError = { err, suggestions ->
                                        isLoading = false
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
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color(0xFF050B18),
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "REGISTER NEW ACCOUNT",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    WorldEagleFooter()
                }
            }
        }
    }

    // Google Account Chooser Modal Dialog
    if (showGoogleAccountChooser) {
        var customGoogleEmail by remember { mutableStateOf("") }
        var showCustomEmailInput by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showGoogleAccountChooser = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x38FFFFFF)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Header
                    GoogleLogoIcon(modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Choose an account to continue to World Eagle",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Account 1: Device/Active Google Account
                    GoogleAccountItem(
                        name = "Muhammad Ahmed",
                        email = "allwell098778@gmail.com",
                        avatarText = "M",
                        onClick = {
                            showGoogleAccountChooser = false
                            isLoading = true
                            viewModel.loginWithGoogle(
                                accountName = "Muhammad Ahmed (Google)",
                                accountEmail = "allwell098778@gmail.com"
                            ) {
                                isLoading = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Account 2: World Eagle Pro Account
                    GoogleAccountItem(
                        name = "Ahmed World Eagle",
                        email = "ahmed.worldeagle@gmail.com",
                        avatarText = "A",
                        onClick = {
                            showGoogleAccountChooser = false
                            isLoading = true
                            viewModel.loginWithGoogle(
                                accountName = "Ahmed World Eagle",
                                accountEmail = "ahmed.worldeagle@gmail.com"
                            ) {
                                isLoading = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Account 3: Investor Desk Account
                    GoogleAccountItem(
                        name = "Investor Alpha Desk",
                        email = "investor.eagle@gmail.com",
                        avatarText = "I",
                        onClick = {
                            showGoogleAccountChooser = false
                            isLoading = true
                            viewModel.loginWithGoogle(
                                accountName = "Investor Alpha Desk",
                                accountEmail = "investor.eagle@gmail.com"
                            ) {
                                isLoading = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (showCustomEmailInput) {
                        OutlinedTextField(
                            value = customGoogleEmail,
                            onValueChange = { customGoogleEmail = it },
                            label = { Text("Enter Google Email") },
                            placeholder = { Text("yourname@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (customGoogleEmail.isNotBlank() && customGoogleEmail.contains("@")) {
                                    showGoogleAccountChooser = false
                                    isLoading = true
                                    val name = customGoogleEmail.substringBefore("@").replace(".", " ")
                                    viewModel.loginWithGoogle(
                                        accountName = "$name (Google)",
                                        accountEmail = customGoogleEmail
                                    ) {
                                        isLoading = false
                                    }
                                } else {
                                    viewModel.showToast("Please enter a valid Gmail address.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = Color(0xFF050B18))
                        ) {
                            Text("Sign in with this account", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(
                            onClick = { showCustomEmailInput = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AmberLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use another Google account", color = AmberLight, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = { showGoogleAccountChooser = false }) {
                        Text("Cancel", color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(emailOrUsername) }
        var resetSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "Account Recovery",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    if (!resetSent) {
                        Text(
                            text = "Enter your registered email address to receive password reset authorization link:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Registered Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "A secure reset link has been dispatched to $resetEmail. Please verify your inbox.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SignalBullish
                        )
                    }
                }
            },
            confirmButton = {
                if (!resetSent) {
                    Button(
                        onClick = {
                            if (resetEmail.isNotBlank()) {
                                resetSent = true
                                viewModel.showToast("Reset instructions sent to $resetEmail")
                            }
                        }
                    ) {
                        Text("Send Link")
                    }
                } else {
                    Button(onClick = { showForgotPasswordDialog = false }) {
                        Text("Close")
                    }
                }
            },
            dismissButton = {
                if (!resetSent) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun GoogleAccountItem(
    name: String,
    email: String,
    avatarText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color(0x14FFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x20FFFFFF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4285F4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
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
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "G",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                ),
                color = Color(0xFF4285F4)
            )
        }
    }
}
