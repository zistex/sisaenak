package com.example.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.TextMuted
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
        targetValue = if (startAnim) 1f else 0.84f,
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
            delay(1300L)
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
            .background(BgWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 28.dp, vertical = 32.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Clean Minimalist Center Brand Emblem on Pure White
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(logoScale)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = BgWhite,
                    border = BorderStroke(1.dp, SurfaceBorder),
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(104.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sisa_enak_fg),
                            contentDescription = "Logo LUDES",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "LUDES",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Makanan Enak Harga Hemat, Sebelum Ludes",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }

            // Minimalist Progress & Subtle Tag
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LinearProgressIndicator(
                    color = FreshGreen,
                    trackColor = SurfaceBorder.copy(alpha = 0.6f),
                    modifier = Modifier
                        .width(96.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                )

                Text(
                    text = "Ambil Sendiri di Warung • Tanpa Ongkir",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextMuted
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
            .background(BgWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top Clean Brand Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BgWhite,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_sisa_enak_fg),
                                    contentDescription = "Logo LUDES",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LUDES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp
                            ),
                            color = TextBlack
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = FreshGreenSoft,
                        border = BorderStroke(1.dp, FreshGreenBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Storefront,
                                contentDescription = null,
                                tint = FreshGreenDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Ambil Sendiri",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = FreshGreenDark
                            )
                        }
                    }
                }

                // Editorial Headline & Subtitle directly on clean white
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = when {
                            isVerifyingEmail -> "Verifikasi email kamu"
                            authTab == 0 -> "Buat akun baru"
                            else -> "Selamat datang kembali"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp
                        ),
                        color = TextBlack
                    )
                    Text(
                        text = if (isVerifyingEmail) {
                            "Masukkan kode 6 digit yang dikirim ke ${regEmail.trim()}."
                        } else {
                            "Makanan Enak Harga Hemat, Sebelum Ludes"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }

                // Sleek Segmented Switcher (Daftar | Masuk)
                if (!isVerifyingEmail) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = BgLightGray,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Daftar Akun", "Masuk").forEachIndexed { idx, label ->
                                val selected = authTab == idx
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) BgWhite else Color.Transparent,
                                    shadowElevation = if (selected) 2.dp else 0.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            authTab = idx
                                            errorMessage = null
                                        }
                                        .testTag(if (idx == 0) "auth_tab_register" else "auth_tab_login")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (selected) TextBlack else TextGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Inline Validation / Error Notice
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LiveRedSoft,
                            border = BorderStroke(1.dp, LiveRed.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = LiveRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = LiveRed
                                )
                            }
                        }
                    }
                }

                // Clean Borderless Form Content directly on white
                AnimatedContent(
                    targetState = Triple(isVerifyingEmail, authTab, Unit),
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "authFormContent"
                ) { (verifying, tab, _) ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (verifying) {
                            // Email Verification Step
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FreshGreenSoft,
                                border = BorderStroke(1.dp, FreshGreenBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        otpInput = generatedOtp
                                        errorMessage = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = BgWhite,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.MarkEmailRead,
                                                contentDescription = "Verifikasi Email",
                                                tint = FreshGreenDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = otpInfoMessage ?: "Kode verifikasi terkirim",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FreshGreenDark
                                        )
                                        Text(
                                            text = "Kode: $generatedOtp • Tap untuk isi otomatis",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = FreshGreenDark
                                        )
                                    }
                                }
                            }

                            AuthLabeledField(
                                label = "Kode Verifikasi (6 Digit)",
                                value = otpInput,
                                onValueChange = { input ->
                                    otpInput = input.filter { it.isDigit() }.take(6)
                                    errorMessage = null
                                },
                                placeholder = "Masukkan 6 digit angka",
                                leadingIcon = Icons.Filled.CheckCircle,
                                keyboardType = KeyboardType.Number,
                                testTag = "verify_otp_input"
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            CleanPrimaryAuthButton(
                                text = "Verifikasi & Masuk",
                                onClick = { validateAndCompleteVerification() },
                                testTag = "verify_submit_button"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        generatedOtp = (100000..999999).random().toString()
                                        otpInfoMessage = "Kode baru dikirim ke ${regEmail.trim()}"
                                        errorMessage = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = FreshGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Kirim Ulang Kode",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = FreshGreenDark
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        isVerifyingEmail = false
                                        errorMessage = null
                                    }
                                ) {
                                    Text(
                                        text = "Ubah Email",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = TextGray
                                    )
                                }
                            }
                        } else if (tab == 0) {
                            // Register Form
                            AuthLabeledField(
                                label = "Nama Lengkap",
                                value = regName,
                                onValueChange = {
                                    regName = it
                                    errorMessage = null
                                },
                                placeholder = "Contoh: Siti Rahma",
                                leadingIcon = Icons.Filled.Person,
                                testTag = "register_name_input"
                            )

                            AuthLabeledField(
                                label = "Alamat Email",
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    errorMessage = null
                                },
                                placeholder = "nama@email.com",
                                leadingIcon = Icons.Filled.Email,
                                keyboardType = KeyboardType.Email,
                                testTag = "register_email_input"
                            )

                            AuthLabeledField(
                                label = "Password",
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    errorMessage = null
                                },
                                placeholder = "Minimal 6 karakter",
                                leadingIcon = Icons.Filled.Lock,
                                keyboardType = KeyboardType.Password,
                                isPassword = true,
                                passwordVisible = showRegPassword,
                                onTogglePassword = { showRegPassword = !showRegPassword },
                                testTag = "register_password_input"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            CleanPrimaryAuthButton(
                                text = "Daftar & Verifikasi Email",
                                onClick = { validateAndStartEmailVerification() },
                                testTag = "register_submit_button"
                            )
                        } else {
                            // Login Form
                            AuthLabeledField(
                                label = "Alamat Email",
                                value = loginEmail,
                                onValueChange = {
                                    loginEmail = it
                                    errorMessage = null
                                },
                                placeholder = "siti.rahma@gmail.com",
                                leadingIcon = Icons.Filled.Email,
                                keyboardType = KeyboardType.Email,
                                testTag = "login_email_input"
                            )

                            AuthLabeledField(
                                label = "Password",
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    errorMessage = null
                                },
                                placeholder = "Masukkan password akun",
                                leadingIcon = Icons.Filled.Lock,
                                keyboardType = KeyboardType.Password,
                                isPassword = true,
                                passwordVisible = showLoginPassword,
                                onTogglePassword = { showLoginPassword = !showLoginPassword },
                                testTag = "login_password_input"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            CleanPrimaryAuthButton(
                                text = "Masuk Sekarang",
                                onClick = { validateAndLogin() },
                                testTag = "login_submit_button"
                            )
                        }
                    }
                }

                // Alternative Auth Methods
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
                                text = "atau lanjutkan dengan",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceBorder)
                        }

                        // Google Sign-In Button
                        OutlinedButton(
                            onClick = { showGoogleNoticeDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, SurfaceBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BgWhite,
                                contentColor = TextBlack
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("google_auth_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ActionOrangeSoft,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = ActionOrangeDark
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Lanjutkan dengan Google",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextBlack
                            )
                        }

                        // Subtle Tonal Quick Demo Button
                        OutlinedButton(
                            onClick = onQuickDemoLogin,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, FreshGreenBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = FreshGreenSoft,
                                contentColor = FreshGreenDark
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("quick_demo_login_button")
                        ) {
                            Text(
                                text = "Masuk Cepat Akun Demo (Siti Rahma)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = FreshGreenDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = FreshGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Clean Footer Guarantee
            Text(
                text = "Ambil Sendiri di warung terdekat • Tanpa ongkir • Pembayaran aman di LUDES",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp)
            )
        }
    }

    if (showGoogleNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleNoticeDialog = false },
            containerColor = BgWhite,
            title = { Text("Login dengan Google Belum Aktif", color = TextBlack) },
            text = {
                Text(
                    "Daftar & Login dengan Google memerlukan integrasi Firebase yang saat ini belum diaktifkan. " +
                        "Silakan daftar dengan Nama, Email & Password di layar ini atau masuk cepat dengan akun Siti Rahma.",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGoogleNoticeDialog = false
                        authTab = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenDark)
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
                    Text("Masuk Akun Demo", color = ActionOrangeDark)
                }
            }
        )
    }
}

@Composable
private fun AuthLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    testTag: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = TextBlack
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Sembunyikan password" else "Lihat password",
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                null
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextBlack,
                unfocusedTextColor = TextBlack,
                focusedContainerColor = BgWhite,
                unfocusedContainerColor = BgLightGray,
                focusedBorderColor = FreshGreenDark,
                unfocusedBorderColor = SurfaceBorder,
                cursorColor = FreshGreenDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun CleanPrimaryAuthButton(
    text: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FreshGreenDark,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
