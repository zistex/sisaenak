package com.example.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PrimaryOrangeButton
import com.example.ui.components.SecondaryGreenButton
import com.example.ui.theme.ActionOrange
import com.example.ui.theme.ActionOrangeDark
import com.example.ui.theme.ActionOrangeSoft
import com.example.ui.theme.BgLightGray
import com.example.ui.theme.BgWhite
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.FreshGreenBorder
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenSoft
import com.example.ui.theme.LiveRed
import com.example.ui.theme.LiveRedSoft
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextBlack
import com.example.ui.theme.TextGray
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isAuthReady: Boolean,
    isLoggedIn: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var startAnim by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.72f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splashScale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "splashAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
    }

    LaunchedEffect(isAuthReady) {
        if (isAuthReady) {
            delay(1350L)
            if (isLoggedIn) {
                onNavigateToHome()
            } else {
                onNavigateToRegister()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FreshGreenDark, FreshGreen, Color(0xFF16A34A))
                )
            )
    ) {
        // Decorative atmospheric circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.minDimension * 0.45f,
                center = Offset(size.width * 0.9f, size.height * 0.14f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.07f),
                radius = size.minDimension * 0.36f,
                center = Offset(size.width * 0.08f, size.height * 0.84f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 28.dp, vertical = 32.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Center Brand Emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(logoScale)
            ) {
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = BgWhite,
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(118.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sisa_enak_fg),
                            contentDescription = "Logo Sisa Enak",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Sisa Enak",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Makan Enak, Hemat 70%, Anti Mubazir",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.94f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Trust Pill & Loader
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ambil Sendiri di Warung • Gratis Ongkir",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }

                LinearProgressIndicator(
                    color = ActionOrange,
                    trackColor = Color.White.copy(alpha = 0.28f),
                    modifier = Modifier
                        .width(120.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                )
            }
        }
    }
}

@Composable
fun RegisterAuthScreen(
    onRegisterVerified: (name: String, email: String, password: String) -> Unit,
    onLoginWithEmail: (email: String, password: String, onError: (String) -> Unit) -> Unit,
    onQuickDemoLogin: () -> Unit
) {
    var authTab by remember { mutableIntStateOf(0) } // 0 = Daftar Akun, 1 = Masuk

    // Registration state
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var showRegPassword by remember { mutableStateOf(false) }

    // Email verification step state
    var isVerifyingEmail by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("482910") }
    var otpInput by remember { mutableStateOf("") }
    var otpInfoMessage by remember { mutableStateOf<String?>(null) }

    // Login state
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showGoogleNoticeDialog by remember { mutableStateOf(false) }

    fun validateAndStartEmailVerification() {
        val cleanName = regName.trim()
        val cleanEmail = regEmail.trim()
        when {
            cleanName.length < 2 -> {
                errorMessage = "Masukkan nama lengkap minimal 2 huruf."
            }
            !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> {
                errorMessage = "Format email belum benar (contoh: budi@gmail.com)."
            }
            regPassword.length < 6 -> {
                errorMessage = "Password minimal 6 karakter agar akun aman."
            }
            else -> {
                errorMessage = null
                generatedOtp = (100000..999999).random().toString()
                otpInput = ""
                otpInfoMessage = "Kode verifikasi dikirim ke $cleanEmail"
                isVerifyingEmail = true
            }
        }
    }

    fun validateAndCompleteVerification() {
        val cleanOtp = otpInput.trim()
        if (cleanOtp != generatedOtp) {
            errorMessage = "Kode verifikasi belum sesuai. Cek kode 6 digit atau tap isi otomatis."
        } else {
            errorMessage = null
            onRegisterVerified(regName.trim(), regEmail.trim(), regPassword)
        }
    }

    fun validateAndLogin() {
        val cleanEmail = loginEmail.trim()
        when {
            !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches() -> {
                errorMessage = "Masukkan alamat email yang valid."
            }
            loginPassword.length < 6 -> {
                errorMessage = "Masukkan password minimal 6 karakter."
            }
            else -> {
                errorMessage = null
                onLoginWithEmail(cleanEmail, loginPassword) { msg ->
                    errorMessage = msg
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLightGray),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Brand Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FreshGreenDark, FreshGreen)
                        )
                    )
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgWhite,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Eco,
                                    contentDescription = "Sisa Enak",
                                    tint = FreshGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sisa Enak",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Ambil Sendiri • Hemat Hingga 70%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isVerifyingEmail) {
                            "Verifikasi Email Kamu"
                        } else if (authTab == 0) {
                            "Buat Akun Sisa Enak"
                        } else {
                            "Masuk ke Sisa Enak"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isVerifyingEmail) {
                            "Satu langkah lagi! Masukkan kode verifikasi agar akun kamu aktif."
                        } else {
                            "Cukup nama, email, password, dan verifikasi email. Langsung bisa pesan!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Switcher: Daftar | Masuk (hidden during email verification step)
                if (!isVerifyingEmail) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = BgWhite,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            listOf("Daftar Akun", "Masuk").forEachIndexed { idx, label ->
                                val selected = authTab == idx
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) ActionOrange else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            authTab = idx
                                            errorMessage = null
                                        }
                                        .testTag(if (idx == 0) "auth_tab_register" else "auth_tab_login")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) Color.White else TextGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Inline Validation / Error Banner
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = LiveRedSoft,
                            border = BorderStroke(1.dp, LiveRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = LiveRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = LiveRed
                                )
                            }
                        }
                    }
                }

                // Main Auth Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isVerifyingEmail) {
                            // Step 2: Verifikasi Email
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = FreshGreenSoft,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.MarkEmailRead,
                                            contentDescription = "Verifikasi Email",
                                            tint = FreshGreenDark,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Cek Email Verifikasi",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextBlack
                                    )
                                    Text(
                                        text = regEmail.trim(),
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = FreshGreenDark
                                    )
                                }
                            }

                            otpInfoMessage?.let { info ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FreshGreenSoft,
                                    border = BorderStroke(1.dp, FreshGreenBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            otpInput = generatedOtp
                                            errorMessage = null
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = info,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FreshGreenDark
                                        )
                                        Text(
                                            text = "Kode Verifikasi Anda: $generatedOtp (Tap untuk isi otomatis)",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = FreshGreenDark
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { input ->
                                    otpInput = input.filter { it.isDigit() }.take(6)
                                    errorMessage = null
                                },
                                label = { Text("Kode Verifikasi 6 Digit") },
                                placeholder = { Text("Masukkan 6 digit angka") },
                                leadingIcon = {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = FreshGreenDark)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("verify_otp_input")
                            )

                            PrimaryOrangeButton(
                                text = "Verifikasi Email & Masuk",
                                onClick = { validateAndCompleteVerification() },
                                testTag = "verify_submit_button",
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        generatedOtp = (100000..999999).random().toString()
                                        otpInfoMessage = "Kode verifikasi baru dikirim ke ${regEmail.trim()}"
                                        errorMessage = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = FreshGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Kirim Ulang Kode", color = FreshGreenDark)
                                }

                                TextButton(
                                    onClick = {
                                        isVerifyingEmail = false
                                        errorMessage = null
                                    }
                                ) {
                                    Text("Ubah Email", color = TextGray)
                                }
                            }
                        } else if (authTab == 0) {
                            // Step 1: Daftar Akun (Nama, Email, Password)
                            OutlinedTextField(
                                value = regName,
                                onValueChange = {
                                    regName = it
                                    errorMessage = null
                                },
                                label = { Text("Nama Lengkap") },
                                placeholder = { Text("Contoh: Siti Rahma") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = FreshGreenDark)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_name_input")
                            )

                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    errorMessage = null
                                },
                                label = { Text("Email") },
                                placeholder = { Text("Contoh: nama@email.com") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = FreshGreenDark)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input")
                            )

                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Minimal 6 karakter") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = FreshGreenDark)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showRegPassword = !showRegPassword }) {
                                        Icon(
                                            imageVector = if (showRegPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (showRegPassword) "Sembunyikan password" else "Lihat password",
                                            tint = TextGray
                                        )
                                    }
                                },
                                visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input")
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            PrimaryOrangeButton(
                                text = "Daftar & Verifikasi Email",
                                onClick = { validateAndStartEmailVerification() },
                                testTag = "register_submit_button",
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // Login Tab (Email & Password)
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = {
                                    loginEmail = it
                                    errorMessage = null
                                },
                                label = { Text("Email") },
                                placeholder = { Text("Contoh: siti.rahma@gmail.com") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = FreshGreenDark)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_input")
                            )

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Password") },
                                placeholder = { Text("Masukkan password") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = FreshGreenDark)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                        Icon(
                                            imageVector = if (showLoginPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (showLoginPassword) "Sembunyikan password" else "Lihat password",
                                            tint = TextGray
                                        )
                                    }
                                },
                                visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ActionOrange,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            PrimaryOrangeButton(
                                text = "Masuk Sekarang",
                                onClick = { validateAndLogin() },
                                testTag = "login_submit_button",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Divider + Google Sign-In Notice / Quick Demo Entry
                AnimatedVisibility(
                    visible = !isVerifyingEmail,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceBorder)
                            Text(
                                text = "  atau  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceBorder)
                        }

                        // Google Sign-In button (shows status when Firebase is not provisioned)
                        OutlinedButton(
                            onClick = { showGoogleNoticeDialog = true },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, SurfaceBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BgWhite,
                                contentColor = TextBlack
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("google_auth_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ActionOrangeSoft,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = ActionOrangeDark
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Daftar / Masuk dengan Google",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextBlack
                            )
                        }

                        SecondaryGreenButton(
                            text = "Masuk Cepat Tanpa Daftar (Akun Siti Rahma)",
                            onClick = onQuickDemoLogin,
                            testTag = "quick_demo_login_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Footer guarantee
                Text(
                    text = "Hanya Ambil Sendiri di warung terdekat • Tidak ada ongkir • Pembayaran aman lewat Sisa Enak",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }

    if (showGoogleNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleNoticeDialog = false },
            title = { Text("Login dengan Google Belum Aktif") },
            text = {
                Text(
                    "Daftar & Login dengan Google memerlukan integrasi Firebase yang saat ini belum diaktifkan. " +
                        "Silakan daftar dengan Nama, Email & Password di layar ini atau masuk cepat dengan akun Siti Rahma."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGoogleNoticeDialog = false
                        authTab = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionOrange)
                ) {
                    Text("Daftar dengan Email")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showGoogleNoticeDialog = false
                        onQuickDemoLogin()
                    }
                ) {
                    Text("Masuk Akun Demo", color = FreshGreenDark)
                }
            }
        )
    }
}
