package com.simats.multimodaltransportationrecommendation.ui.payment

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData

@Composable
fun PaymentMethodScreen(
    viewModel: PaymentMethodViewModel = viewModel(),
    route: RouteOption? = null,
    origin: String = "Ferry Building",
    destination: String = "California Academy of Sciences",
    isManagementOnly: Boolean = false,
    onBack: () -> Unit = {},
    onPaySuccess: (BookedJourneyData) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPaymentMethods(context)
        if (route != null) {
            viewModel.setBookingDetails(route.fareText, "1 adult · ${route.title} · Today")
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
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
                            text = if (isManagementOnly) "PROFILE / WALLET" else "14 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = if (isManagementOnly) "Payment Methods" else "Checkout & Payment",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Amount Header (if booking)
                if (!isManagementOnly) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF101D17))
                                .border(1.dp, Color(0xFF1E3F2A), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🛡️", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "MOVESMART SECURE CHECKOUT",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Text(
                            text = uiState.amount,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = uiState.subtitle,
                            color = Color(0xFF8A99AD),
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "Saved payment methods",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFC6FF00), modifier = Modifier.size(32.dp))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.paymentMethods) { method ->
                            val isSelected = uiState.selectedMethodId == method.paymentId
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) Color(0xFF112017) else Color(0xFF0F172A))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF1E4D2B) else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { viewModel.selectMethod(method.paymentId) }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF162238)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val icon = when (method.methodType.lowercase()) {
                                                "upi" -> "⚡"
                                                "wallet" -> "📱"
                                                else -> "💳"
                                            }
                                            Text(text = icon, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = method.title,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (method.isDefault) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFF1A3824))
                                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(text = "DEFAULT", color = Color(0xFFC6FF00), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            method.subtitle?.let { sub ->
                                                Text(text = sub, color = Color(0xFF8A99AD), fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Delete icon
                                        Text(
                                            text = "🗑️",
                                            fontSize = 13.sp,
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .clickable { viewModel.deletePaymentMethod(context, method.paymentId) }
                                        )

                                        // Selection check
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Text(text = "✓", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Add Payment Method Button
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                    .clickable { showAddDialog = true }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF162238)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "➕", fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "Add a payment method", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Credit card, Debit card, or UPI", color = Color(0xFF8A99AD), fontSize = 10.sp)
                                    }
                                }
                                Text(text = "›", color = Color(0xFF8A99AD), fontSize = 14.sp)
                            }
                        }

                        // Security & PCI-DSS Notice
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔒", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = uiState.securityNotice,
                                        color = Color(0xFF8A99AD),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom CTA Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (!isManagementOnly) {
                    Text(
                        text = "Encrypted transaction · 256-bit SSL",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.payAndBook(
                                context = context,
                                route = route,
                                origin = origin,
                                destination = destination,
                                onSuccess = onPaySuccess
                            )
                        },
                        enabled = !uiState.isBooking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(color = Color(0xFF070B14), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Authorizing...", color = Color(0xFF070B14), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "Pay ${uiState.amount} & Start Journey  →",
                                color = Color(0xFF070B14),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { onBack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Text(text = "Back to Profile", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Add Payment Method Dialog
        if (showAddDialog) {
            AddPaymentMethodDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { type, title, subtitle ->
                    viewModel.addPaymentMethod(context, type, title, subtitle) {
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun AddPaymentMethodDialog(
    onDismiss: () -> Unit,
    onAdd: (methodType: String, title: String, subtitle: String?) -> Unit
) {
    var selectedType by remember { mutableStateOf("card") } // "card" or "upi"
    var cardName by remember { mutableStateOf("") }
    var cardLast4 by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Text(text = "Add Payment Method", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Type Selector (Card vs UPI)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF162238))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedType == "card") Color(0xFF1E293B) else Color.Transparent)
                            .clickable { selectedType = "card" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💳 Card", color = if (selectedType == "card") Color(0xFFC6FF00) else Color(0xFF8A99AD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedType == "upi") Color(0xFF1E293B) else Color.Transparent)
                            .clickable { selectedType = "upi" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡ UPI", color = if (selectedType == "upi") Color(0xFFC6FF00) else Color(0xFF8A99AD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (selectedType == "card") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Cardholder Name", color = Color(0xFF8A99AD), fontSize = 11.sp)
                        BasicTextField(
                            value = cardName,
                            onValueChange = { cardName = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = SolidColor(Color(0xFFC6FF00)),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF162238))
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    if (cardName.isEmpty()) Text(text = "e.g. Alex Morgan", color = Color(0xFF64748B), fontSize = 13.sp)
                                    inner()
                                }
                            }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1.3f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Last 4 Digits", color = Color(0xFF8A99AD), fontSize = 11.sp)
                            BasicTextField(
                                value = cardLast4,
                                onValueChange = { if (it.length <= 4) cardLast4 = it },
                                singleLine = true,
                                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                cursorBrush = SolidColor(Color(0xFFC6FF00)),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF162238))
                                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        if (cardLast4.isEmpty()) Text(text = "4242", color = Color(0xFF64748B), fontSize = 13.sp)
                                        inner()
                                    }
                                }
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Expiry (MM/YY)", color = Color(0xFF8A99AD), fontSize = 11.sp)
                            BasicTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it },
                                singleLine = true,
                                textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                cursorBrush = SolidColor(Color(0xFFC6FF00)),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF162238))
                                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        if (cardExpiry.isEmpty()) Text(text = "08/29", color = Color(0xFF64748B), fontSize = 13.sp)
                                        inner()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "UPI Virtual Payment Address", color = Color(0xFF8A99AD), fontSize = 11.sp)
                        BasicTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            cursorBrush = SolidColor(Color(0xFFC6FF00)),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF162238))
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    if (upiId.isEmpty()) Text(text = "e.g. alex@okaxis", color = Color(0xFF64748B), fontSize = 13.sp)
                                    inner()
                                }
                            }
                        )
                    }
                }

                Text(
                    text = "🛡️ PCI-DSS Compliant: Card data is tokenized securely.",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedType == "card") {
                        val num = if (cardLast4.isNotBlank()) cardLast4 else "1234"
                        val holder = if (cardName.isNotBlank()) cardName else "Cardholder"
                        val exp = if (cardExpiry.isNotBlank()) cardExpiry else "12/28"
                        onAdd("card", "Card •••• $num", "$holder · Exp $exp")
                    } else {
                        val id = if (upiId.isNotBlank()) upiId else "user@upi"
                        onAdd("upi", "UPI ($id)", "Verified Virtual Address")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
            ) {
                Text(text = "Add Method", color = Color(0xFF070B14), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(text = "Cancel", color = Color(0xFF8A99AD))
            }
        }
    )
}
