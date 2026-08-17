package com.example.data.auth

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.SessionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

sealed class AuthResult {
    data class Success(val user: UserEntity, val message: String = "") : AuthResult()
    data class Error(val error: String, val suggestedUsernames: List<String> = emptyList()) : AuthResult()
}

class AuthManager(private val db: AppDatabase) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _activeSessionToken = MutableStateFlow<String?>(null)
    val activeSessionToken: StateFlow<String?> = _activeSessionToken.asStateFlow()

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    suspend fun checkUsernameAvailability(username: String): Pair<Boolean, List<String>> = withContext(Dispatchers.IO) {
        val clean = username.trim().lowercase()
        if (clean.length < 3) return@withContext Pair(false, emptyList())
        val existing = db.userDao().getUserByUsername(clean)
        if (existing == null) {
            Pair(true, emptyList())
        } else {
            val suggestions = listOf(
                "${clean}_pro",
                "${clean}_pk",
                "${clean}_${(10..99).random()}",
                "real_${clean}"
            )
            Pair(false, suggestions)
        }
    }

    suspend fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String,
        confirmPass: String,
        dpUrl: String,
        bio: String,
        country: String,
        timezone: String,
        phone: String = "",
        dob: String = ""
    ): AuthResult = withContext(Dispatchers.IO) {
        if (fullName.isBlank()) return@withContext AuthResult.Error("Full Name is required.")
        if (username.isBlank() || username.length < 3) return@withContext AuthResult.Error("Username must be at least 3 characters.")
        if (email.isBlank() || !email.contains("@")) return@withContext AuthResult.Error("Valid email address is required.")
        if (password.length < 6) return@withContext AuthResult.Error("Password must be at least 6 characters.")
        if (password != confirmPass) return@withContext AuthResult.Error("Passwords do not match.")

        val cleanUsername = username.trim()
        val cleanEmail = email.trim().lowercase()

        // Check unique username
        val existingUserByUsername = db.userDao().getUserByUsername(cleanUsername)
        if (existingUserByUsername != null) {
            val suggestions = listOf(
                "${cleanUsername}_pro",
                "${cleanUsername}_pk",
                "${cleanUsername}_${(10..99).random()}",
                "real_${cleanUsername}"
            )
            return@withContext AuthResult.Error("Username already taken", suggestions)
        }

        // Check unique email
        val existingUserByEmail = db.userDao().getUserByEmail(cleanEmail)
        if (existingUserByEmail != null) {
            return@withContext AuthResult.Error("An account with this email already exists.")
        }

        // Check if this is the FIRST user in the database -> becomes OWNER / ADMIN
        val userCount = db.userDao().getUserCount()
        val role = if (userCount == 0) "ADMIN" else "USER"

        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val joined = sdf.format(Date())

        val newUser = UserEntity(
            fullName = fullName.trim(),
            username = cleanUsername,
            email = cleanEmail,
            passwordHash = hashPassword(password),
            dpUrl = dpUrl,
            bio = if (bio.isBlank()) "World Eagle market intelligence member" else bio.trim(),
            country = if (country.isBlank()) "Pakistan" else country.trim(),
            timezone = if (timezone.isBlank()) "UTC+5 (PKT)" else timezone.trim(),
            phone = phone.trim(),
            dob = dob.trim(),
            role = role,
            joinedDate = joined,
            isLeaderboardVisible = true,
            isOnline = true
        )

        val newId = db.userDao().insertUser(newUser)
        // Per Rule #5: DO NOT automatically login! Redirect to /login.
        AuthResult.Success(newUser.copy(id = newId), "Account created successfully! Please sign in with your credentials.")
    }

    suspend fun login(emailOrUsername: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanInput = emailOrUsername.trim()
        if (cleanInput.isBlank() || password.isBlank()) {
            return@withContext AuthResult.Error("Please enter your email/username and password.")
        }

        val user = if (cleanInput.contains("@")) {
            db.userDao().getUserByEmail(cleanInput.lowercase())
        } else {
            db.userDao().getUserByUsername(cleanInput)
        }

        if (user == null) {
            return@withContext AuthResult.Error("No account found with this credential.")
        }

        if (user.isBanned) {
            return@withContext AuthResult.Error("This account has been suspended by the administrator.")
        }

        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return@withContext AuthResult.Error("Invalid password. Please verify and retry.")
        }

        // Create active session
        val token = "WE_SESSION_" + UUID.randomUUID().toString().replace("-", "")
        val session = SessionEntity(
            token = token,
            userId = user.id,
            deviceName = "Android Device",
            ipAddress = "192.168.1.100"
        )
        db.sessionDao().insertSession(session)

        _activeSessionToken.value = token
        _currentUser.value = user
        AuthResult.Success(user, "Welcome to World Eagle, ${user.fullName}!")
    }

    suspend fun loginWithGoogle(accountName: String = "Muhammad Ahmed (Google)", accountEmail: String = "ahmed.worldeagle@gmail.com"): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = accountEmail.trim().lowercase()
        var user = db.userDao().getUserByEmail(cleanEmail)

        if (user == null) {
            val userCount = db.userDao().getUserCount()
            val role = if (userCount == 0) "ADMIN" else "USER"
            val baseUsername = cleanEmail.substringBefore("@").replace(".", "_")
            var finalUsername = baseUsername
            if (db.userDao().getUserByUsername(finalUsername) != null) {
                finalUsername = "${baseUsername}_${(10..99).random()}"
            }

            val newUser = UserEntity(
                fullName = accountName,
                username = finalUsername,
                email = cleanEmail,
                passwordHash = hashPassword(UUID.randomUUID().toString()),
                dpUrl = "",
                bio = "World Eagle Verified Google Member",
                country = "Pakistan",
                timezone = "UTC+5 (PKT)",
                role = role,
                joinedDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                isLeaderboardVisible = true,
                isOnline = true
            )
            val newId = db.userDao().insertUser(newUser)
            user = newUser.copy(id = newId)
        }

        if (user.isBanned) {
            return@withContext AuthResult.Error("This account has been suspended by the administrator.")
        }

        val token = "WE_GOOGLE_SESSION_" + UUID.randomUUID().toString().replace("-", "")
        db.sessionDao().insertSession(
            SessionEntity(
                token = token,
                userId = user.id,
                deviceName = "Google OAuth Android",
                ipAddress = "10.0.0.1"
            )
        )

        _activeSessionToken.value = token
        _currentUser.value = user
        AuthResult.Success(user, "Signed in via Google OAuth successfully.")
    }

    suspend fun updateProfile(
        fullName: String,
        newUsername: String,
        bio: String,
        country: String,
        timezone: String,
        dpUrl: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext AuthResult.Error("Not authenticated.")

        val cleanUsername = newUsername.trim()
        if (cleanUsername != current.username) {
            val existing = db.userDao().getUserByUsername(cleanUsername)
            if (existing != null && existing.id != current.id) {
                val suggestions = listOf(
                    "${cleanUsername}_pro",
                    "${cleanUsername}_pk",
                    "${cleanUsername}_${(10..99).random()}"
                )
                return@withContext AuthResult.Error("Username already taken", suggestions)
            }
        }

        val updated = current.copy(
            fullName = fullName.trim(),
            username = cleanUsername,
            bio = bio.trim(),
            country = country.trim(),
            timezone = timezone.trim(),
            dpUrl = dpUrl
        )

        db.userDao().updateUser(updated)
        _currentUser.value = updated
        AuthResult.Success(updated, "Profile updated successfully.")
    }

    suspend fun updateChatPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext false
        val hashedPin = if (pin.isBlank()) "" else hashPassword(pin)
        val updated = current.copy(chatPin = hashedPin)
        db.userDao().updateUser(updated)
        _currentUser.value = updated
        true
    }

    suspend fun verifyChatPin(enteredPin: String): Boolean = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext false
        if (current.chatPin.isBlank()) return@withContext true
        val enteredHash = hashPassword(enteredPin)
        return@withContext (current.chatPin == enteredHash)
    }

    suspend fun toggleLeaderboardVisibility(visible: Boolean) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val updated = current.copy(isLeaderboardVisible = visible)
        db.userDao().updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val token = _activeSessionToken.value
        if (token != null) {
            db.sessionDao().invalidateSession(token)
        }
        _currentUser.value = null
        _activeSessionToken.value = null
    }

    suspend fun logoutAllDevices() = withContext(Dispatchers.IO) {
        val current = _currentUser.value
        if (current != null) {
            db.sessionDao().invalidateAllUserSessions(current.id)
        }
        _currentUser.value = null
        _activeSessionToken.value = null
    }
}
