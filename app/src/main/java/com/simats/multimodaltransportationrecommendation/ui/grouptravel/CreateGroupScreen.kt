package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.group.TravelGroup
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun CreateGroupScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    onBack: () -> Unit,
    onGroupCreated: (TravelGroup) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdGroupObj by remember { mutableStateOf<TravelGroup?>(null) }

    val popularDestinations = listOf("Ooty", "Mahabalipuram", "Kodaikanal", "Rameswaram", "Madurai", "Yercaud", "Kanyakumari")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SMART FAMILY TRIP",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Create Group Trip",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Error Banner
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardPeach)
                            .border(1.dp, WarmOrangeAccent, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "⚠️ ${uiState.errorMessage}", color = WarmOrangeDark, fontSize = 11.sp)
                    }
                }

                // Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Group Name
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "GROUP NAME", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = uiState.formGroupName,
                                onValueChange = { viewModel.onFormGroupNameChange(it) },
                                singleLine = true,
                                textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                cursorBrush = SolidColor(SkyBluePrimary),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BackgroundSoftCream)
                                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (uiState.formGroupName.isEmpty()) {
                                            Text(text = "e.g., Nilgiri Family Monsoon Trip", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    }
                                }
                            )
                        }

                        // 2. Common Destination
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "COMMON DESTINATION", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = uiState.formDestination,
                                onValueChange = { viewModel.onFormDestinationChange(it) },
                                singleLine = true,
                                textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                cursorBrush = SolidColor(SkyBluePrimary),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BackgroundSoftCream)
                                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (uiState.formDestination.isEmpty()) {
                                            Text(text = "Enter destination (e.g. Ooty, Mahabalipuram)", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    }
                                }
                            )

                            // Quick Suggestions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                popularDestinations.take(4).forEach { dest ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PastelCardBlue)
                                            .clickable { viewModel.onFormDestinationChange(dest) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = dest, color = SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // 3. Creator's Starting Location
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "YOUR STARTING LOCATION", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = uiState.formStartLocation,
                                onValueChange = { viewModel.onFormStartLocationChange(it) },
                                singleLine = true,
                                textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                cursorBrush = SolidColor(SkyBluePrimary),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BackgroundSoftCream)
                                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (uiState.formStartLocation.isEmpty()) {
                                            Text(text = "Your departure city / transit hub", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    }
                                }
                            )
                        }

                        // 4. Date & Time Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "TRAVEL DATE", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                BasicTextField(
                                    value = uiState.formTravelDate,
                                    onValueChange = { viewModel.onFormTravelDateChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BackgroundSoftCream)
                                                .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (uiState.formTravelDate.isEmpty()) {
                                                Text(text = "2026-11-15", color = TextLight, fontSize = 12.sp)
                                            }
                                            inner()
                                        }
                                    }
                                )
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "DEPARTURE TIME", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                BasicTextField(
                                    value = uiState.formTravelTime,
                                    onValueChange = { viewModel.onFormTravelTimeChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BackgroundSoftCream)
                                                .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (uiState.formTravelTime.isEmpty()) {
                                                Text(text = "08:00 AM", color = TextLight, fontSize = 12.sp)
                                            }
                                            inner()
                                        }
                                    }
                                )
                            }
                        }

                        // 5. Trip Description
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "TRIP DESCRIPTION / NOTES", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = uiState.formDescription,
                                onValueChange = { viewModel.onFormDescriptionChange(it) },
                                textStyle = TextStyle(color = TextNavyDark, fontSize = 12.sp),
                                cursorBrush = SolidColor(SkyBluePrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundSoftCream)
                                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                decorationBox = { inner ->
                                    if (uiState.formDescription.isEmpty()) {
                                        Text(text = "Plan details, family notes, packing reminders...", color = TextLight, fontSize = 12.sp)
                                    }
                                    inner()
                                }
                            )
                        }

                        // 6. Group Privacy
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "GROUP PRIVACY", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf("invite_only" to "Invite Code Only", "private" to "Private (Requires Approval)").forEach { (key, label) ->
                                    val isSel = uiState.formPrivacy == key
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSel) PastelCardTeal else BackgroundSoftCream)
                                            .border(1.dp, if (isSel) TealSecondary else BorderLight, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.onFormPrivacyChange(key) }
                                            .padding(vertical = 8.dp, horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSel) TealDark else TextNavyBody,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Create Submit Button
                Button(
                    onClick = {
                        viewModel.createGroup(context) { group ->
                            createdGroupObj = group
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Text(
                                text = "Create & Generate Invitation Code ➔",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Success Dialog with Code & Share
    if (showSuccessDialog && createdGroupObj != null) {
        val group = createdGroupObj!!
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onGroupCreated(group)
            },
            title = {
                Text(text = "🎉 Group Created!", color = TextNavyDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Your family/friends group '${group.groupName}' to ${group.destination} is ready!",
                        color = TextNavyBody,
                        fontSize = 13.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealLight, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "INVITATION CODE", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = group.invitationCode,
                                color = TealDark,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    Text(
                        text = "Share this code with your traveling members. They can enter it in 'Join Group' from Chennai, Madurai, Salem, Coimbatore, etc. and choose their starting point.",
                        color = TextMutedSlate,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(group.invitationCode))
                        showSuccessDialog = false
                        onGroupCreated(group)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Text(text = "Copy Code & Open Trip", color = Color.White, fontSize = 12.sp)
                }
            }
        )
    }
}

