package com.simats.multimodaltransportationrecommendation.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onSignOut: () -> Unit = {},
    onOpenTravelPreferences: () -> Unit = {},
    onOpenSavedPlaces: () -> Unit = {},
    onOpenPaymentMethods: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Activity result launcher for picking a new profile picture from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadSelectedImage(context, it) }
    }

    // Initialize user profile from session / database
    LaunchedEffect(Unit) {
        viewModel.initProfile(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "20 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Profile & settings",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Refresh button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), CircleShape)
                                .clickable { viewModel.fetchProfile(context = context) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔄", fontSize = 13.sp)
                        }

                        // Edit / Cancel toggle button
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (uiState.isEditMode) Color(0x33EF4444) else Color(0xFF0F172A)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isEditMode) Color(0xFFEF4444) else Color(0xFF1E293B),
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    if (uiState.isEditMode) viewModel.cancelEditMode() else viewModel.enterEditMode()
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (uiState.isEditMode) "✕ " else "✏️ ",
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = if (uiState.isEditMode) "Cancel" else "Edit",
                                    color = if (uiState.isEditMode) Color(0xFFFF8B8B) else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Status Alerts
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFFC6FF00),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Syncing profile with database...",
                                color = Color(0xFF8A99AD),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                if (!uiState.successMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22C6FF00))
                            .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✓", color = Color(0xFFC6FF00), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.successMessage!!,
                                    color = Color(0xFFC6FF00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "✕",
                                color = Color(0xFFC6FF00),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.clearMessages() }
                            )
                        }
                    }
                }

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚠️", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = Color(0xFFFF8B8B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "✕",
                                color = Color(0xFFFF8B8B),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.clearMessages() }
                            )
                        }
                    }
                }

                // User Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile Avatar with Camera Upload Badge
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.size(76.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(74.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF112017))
                                    .border(2.dp, Color(0xFFC6FF00), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!uiState.profileImageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(uiState.profileImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "User profile picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = uiState.initials,
                                        color = Color(0xFFC6FF00),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (uiState.isUploadingImage) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0x99000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color(0xFFC6FF00),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }

                            // Camera / Upload badge button
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC6FF00))
                                    .border(2.dp, Color(0xFF070B14), CircleShape)
                                    .clickable {
                                        imagePickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "📷", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = uiState.fullName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✉️ ", fontSize = 11.sp)
                                Text(
                                    text = uiState.email,
                                    color = Color(0xFF8A99AD),
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📞 ", fontSize = 11.sp)
                                Text(
                                    text = if (uiState.phone.isNotBlank()) uiState.phone else "No phone set",
                                    color = if (uiState.phone.isNotBlank()) Color(0xFFC6FF00) else Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = uiState.userMeta,
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Personal Details Card (View Mode OR Edit Mode)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.isEditMode) "✏️ EDIT PERSONAL DETAILS" else "PERSONAL DETAILS",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            if (!uiState.isEditMode) {
                                Text(
                                    text = "Edit ✎",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.enterEditMode() }
                                )
                            }
                        }

                        if (!uiState.isEditMode) {
                            // View Mode: Clean informative layout
                            DetailRow(icon = "👤", label = "Full name", value = uiState.fullName)
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.8.dp)

                            DetailRow(
                                icon = "✉️",
                                label = "Email address",
                                value = uiState.email,
                                badge = "Verified"
                            )
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.8.dp)

                            DetailRow(
                                icon = "📞",
                                label = "Phone number",
                                value = if (uiState.phone.isNotBlank()) uiState.phone else "Not provided"
                            )
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.8.dp)

                            DetailRow(
                                icon = "📍",
                                label = "Home city / Address",
                                value = if (uiState.address.isNotBlank()) uiState.address else "Not specified"
                            )
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.8.dp)

                            DetailRow(
                                icon = "📝",
                                label = "Bio / Note",
                                value = if (uiState.bio.isNotBlank()) uiState.bio else "Smart transit traveler"
                            )
                            HorizontalDivider(color = Color(0xFF1E293B), thickness = 0.8.dp)

                            DetailRow(
                                icon = "⚧️",
                                label = "Gender",
                                value = if (uiState.gender.isNotBlank()) uiState.gender else "Not specified"
                            )
                        } else {
                            // Edit Mode: Interactive Input Fields
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ProfileInputField(
                                    label = "Full Name",
                                    value = uiState.editFullName,
                                    placeholder = "Enter your full name",
                                    icon = "👤",
                                    onValueChange = { viewModel.onEditFullNameChange(it) }
                                )

                                // Email (Read-only Account ID)
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Email Address (Account ID)",
                                        color = Color(0xFF8A99AD),
                                        fontSize = 11.sp
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF070B14))
                                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🔒", fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.email,
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                ProfileInputField(
                                    label = "Phone Number",
                                    value = uiState.editPhone,
                                    placeholder = "+1 555-019-2834",
                                    icon = "📞",
                                    keyboardType = KeyboardType.Phone,
                                    onValueChange = { viewModel.onEditPhoneChange(it) }
                                )

                                ProfileInputField(
                                    label = "Home City / Address",
                                    value = uiState.editAddress,
                                    placeholder = "San Francisco, CA",
                                    icon = "📍",
                                    onValueChange = { viewModel.onEditAddressChange(it) }
                                )

                                ProfileInputField(
                                    label = "Bio / Commute Preference",
                                    value = uiState.editBio,
                                    placeholder = "Eco-conscious commuter",
                                    icon = "📝",
                                    onValueChange = { viewModel.onEditBioChange(it) }
                                )

                                ProfileInputField(
                                    label = "Gender",
                                    value = uiState.editGender,
                                    placeholder = "e.g. Female, Male, Non-binary",
                                    icon = "⚧️",
                                    onValueChange = { viewModel.onEditGenderChange(it) }
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Save Changes and Cancel Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.cancelEditMode() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(23.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1E293B)
                                        )
                                    ) {
                                        Text(text = "Cancel", color = Color.White, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.saveProfileChanges(context) },
                                        enabled = !uiState.isSaving,
                                        modifier = Modifier
                                            .weight(1.4f)
                                            .height(46.dp),
                                        shape = RoundedCornerShape(23.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFC6FF00),
                                            disabledContainerColor = Color(0x66C6FF00)
                                        )
                                    ) {
                                        if (uiState.isSaving) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color(0xFF070B14),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Save Changes ✓",
                                                color = Color(0xFF070B14),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Security & Change Password Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "SECURITY & CREDENTIALS",
                            color = Color(0xFF8A99AD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF070B14))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .clickable { viewModel.openChangePasswordDialog() }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔑", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Change Password",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Update your login security credentials",
                                        color = Color(0xFF8A99AD),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Text(
                                text = "Update ›",
                                color = Color(0xFFC6FF00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Impact Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF112017))
                        .border(1.5.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌿", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.co2Impact,
                                color = Color(0xFFC6FF00),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.co2Subtitle,
                                color = Color(0xFF8A99AD),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Account Preferences Shortcuts
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val settingsItems = listOf(
                        Triple("Travel preferences", uiState.travelPreferences, "⚙️"),
                        Triple("Notifications", uiState.notifications, "🔔"),
                        Triple("Saved places", uiState.savedPlacesCount, "📍"),
                        Triple("Payment methods", uiState.paymentMethod, "💳")
                    )

                    settingsItems.forEach { (title, subtitle, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .clickable {
                                    when (title) {
                                        "Travel preferences" -> onOpenTravelPreferences()
                                        "Notifications" -> viewModel.openNotificationDialog()
                                        "Saved places" -> onOpenSavedPlaces()
                                        "Payment methods" -> onOpenPaymentMethods()
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = subtitle, color = Color(0xFF8A99AD), fontSize = 10.sp)
                                }
                            }
                            Text(text = "›", color = Color(0xFF8A99AD), fontSize = 14.sp)
                        }
                    }
                }

                // Logout Button Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1F1215))
                        .border(1.dp, Color(0xFF4A1F25), RoundedCornerShape(14.dp))
                        .clickable { viewModel.promptLogout() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🚪", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Log Out",
                                    color = Color(0xFFFF6B6B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sign out of your MoveSmart account",
                                    color = Color(0xFF8A99AD),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "Exit ›",
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Version text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${uiState.versionText} · Connected to MoveSmart AI Backend",
                        color = Color(0xFF475569),
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B111E))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    "🗺️\nExplore" to false,
                    "🎫\nMy trips" to false,
                    "🔖\nSaved" to false,
                    "👤\nProfile" to true
                )
                tabs.forEach { (tabInfo, isActive) ->
                    val lines = tabInfo.split("\n")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onNavigateTab(lines[1])
                        }
                    ) {
                        Text(text = lines[0], fontSize = 16.sp)
                        Text(
                            text = lines[1],
                            color = if (isActive) Color(0xFFC6FF00) else Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    // Notification Settings Dialog
    if (uiState.showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeNotificationDialog() },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notification Settings",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Master Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF142018))
                            .border(1.dp, Color(0xFF1E4028), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Allow Notifications", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Enable all notifications from MoveSmart", color = Color(0xFF8A99AD), fontSize = 10.sp)
                        }
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = { viewModel.toggleMasterNotification(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070B14),
                                checkedTrackColor = Color(0xFFC6FF00),
                                uncheckedThumbColor = Color(0xFF8A99AD),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }

                    if (uiState.notificationsEnabled) {
                        // Sub items
                        val toggles = listOf(
                            Triple("Journey Reminders", "Alerts before scheduled trips", uiState.journeyReminders) to { v: Boolean -> viewModel.toggleJourneyReminders(v) },
                            Triple("Booking Updates", "Ticket and fare confirmations", uiState.bookingUpdates) to { v: Boolean -> viewModel.toggleBookingUpdates(v) },
                            Triple("Tourist Recommendations", "Explore new places & attractions", uiState.touristRecommendations) to { v: Boolean -> viewModel.toggleTouristRecommendations(v) },
                            Triple("Travel Alerts", "Transit delays and green rerouting", uiState.travelAlerts) to { v: Boolean -> viewModel.toggleTravelAlerts(v) }
                        )

                        toggles.forEach { (item, onToggle) ->
                            val (title, sub, isChecked) = item
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(text = sub, color = Color(0xFF8A99AD), fontSize = 10.sp)
                                }
                                Switch(
                                    checked = isChecked,
                                    onCheckedChange = onToggle,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF070B14),
                                        checkedTrackColor = Color(0xFFC6FF00),
                                        uncheckedThumbColor = Color(0xFF8A99AD),
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveNotificationSettings(context) },
                    enabled = !uiState.isSavingNotifications,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00),
                        disabledContainerColor = Color(0x66C6FF00)
                    )
                ) {
                    if (uiState.isSavingNotifications) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF070B14),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Settings",
                            color = Color(0xFF070B14),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.closeNotificationDialog() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "Cancel", color = Color.White, fontSize = 12.sp)
                }
            }
        )
    }

    // Change Password Dialog
    if (uiState.showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeChangePasswordDialog() },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Change Password",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!uiState.passwordErrorMessage.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22EF4444))
                                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "⚠️ ${uiState.passwordErrorMessage}",
                                color = Color(0xFFFF8B8B),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Current Password
                    PasswordField(
                        label = "Current Password",
                        value = uiState.currentPassword,
                        isVisible = uiState.isCurrentPasswordVisible,
                        onValueChange = { viewModel.onCurrentPasswordChange(it) },
                        onToggleVisibility = { viewModel.toggleCurrentPasswordVisibility() }
                    )

                    // New Password
                    PasswordField(
                        label = "New Password (min 8 chars)",
                        value = uiState.newPassword,
                        isVisible = uiState.isNewPasswordVisible,
                        onValueChange = { viewModel.onNewPasswordChange(it) },
                        onToggleVisibility = { viewModel.toggleNewPasswordVisibility() }
                    )

                    // Confirm New Password
                    PasswordField(
                        label = "Confirm New Password",
                        value = uiState.confirmNewPassword,
                        isVisible = uiState.isConfirmPasswordVisible,
                        onValueChange = { viewModel.onConfirmNewPasswordChange(it) },
                        onToggleVisibility = { viewModel.toggleConfirmPasswordVisibility() }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitChangePassword() },
                    enabled = !uiState.isChangingPassword,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00),
                        disabledContainerColor = Color(0x66C6FF00)
                    )
                ) {
                    if (uiState.isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF070B14),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Update Password",
                            color = Color(0xFF070B14),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.closeChangePasswordDialog() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "Cancel", color = Color.White, fontSize = 12.sp)
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (uiState.showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLogoutPrompt() },
            containerColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Sign out of MoveSmart?",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Your current session will be cleared on this device. You will need to sign in again to access personalized routes and profiles.",
                    color = Color(0xFF8A99AD),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.logout(context, onSignOut) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(text = "Log Out", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.dismissLogoutPrompt() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "Stay Signed In", color = Color.White, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    icon: String,
    label: String,
    value: String,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    color = Color(0xFF8A99AD),
                    fontSize = 10.sp
                )
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF112017))
                    .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = Color(0xFFC6FF00),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    placeholder: String,
    icon: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = Color(0xFF8A99AD),
            fontSize = 11.sp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF070B14))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(Color(0xFFC6FF00)),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Next
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFF475569),
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, color = Color(0xFF8A99AD), fontSize = 11.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF070B14))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "🔒", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    cursorBrush = SolidColor(Color(0xFFC6FF00)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    decorationBox = { inner ->
                        if (value.isEmpty()) {
                            Text(text = "••••••••", color = Color(0xFF475569), fontSize = 12.sp)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = if (isVisible) "🙈" else "👁️",
                fontSize = 13.sp,
                modifier = Modifier.clickable { onToggleVisibility() }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
