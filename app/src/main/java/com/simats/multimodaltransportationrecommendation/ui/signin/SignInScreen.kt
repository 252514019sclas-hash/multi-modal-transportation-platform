package com.simats.multimodaltransportationrecommendation.ui.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
    onCreateAccount: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF111C2E))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "03 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Sign in",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logo Box
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF111E16))
                        .border(1.5.dp, Color(0xFFC6FF00), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color(0xFFC6FF00),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome back, traveler.",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your next great journey starts here.",
                    color = Color(0xFF8A99AD),
                    fontSize = 13.sp
                )
            }

            // Form Inputs Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Error Message Banner
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x22EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    }
                }

                // Email field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Email address",
                        color = Color(0xFF8A99AD),
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✉️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(Color(0xFFC6FF00)),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            decorationBox = { innerTextField ->
                                if (uiState.email.isEmpty()) {
                                    Text(
                                        text = "alex.morgan@example.com",
                                        color = Color(0xFF475569),
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // Password field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Password",
                        color = Color(0xFF8A99AD),
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🔒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = uiState.password,
                                onValueChange = { viewModel.onPasswordChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFFC6FF00)),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        viewModel.signIn(context = context, onSuccess = onSignInSuccess)
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    if (uiState.password.isEmpty()) {
                                        Text(
                                            text = "••••••••••",
                                            color = Color(0xFF475569),
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.togglePasswordVisibility() }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = if (uiState.isPasswordVisible) "🙈" else "👁️",
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Forgot password
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot password?",
                        color = Color(0xFFC6FF00),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { /* Handle forgot password */ }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sign In Button
                Button(
                    onClick = {
                        viewModel.signIn(context = context, onSuccess = onSignInSuccess)
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00),
                        disabledContainerColor = Color(0x66C6FF00)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF070B14),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign in  →",
                            color = Color(0xFF070B14),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Footer Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "— or continue with —",
                    color = Color(0xFF475569),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.clickable { onCreateAccount() },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "New to MoveSmart? ",
                        color = Color(0xFF8A99AD),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Create an account",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Privacy Trust Notice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🛡️", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your data stays private. Your city opens up.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun SignInScreenPreview() {
    SignInScreen()
}
