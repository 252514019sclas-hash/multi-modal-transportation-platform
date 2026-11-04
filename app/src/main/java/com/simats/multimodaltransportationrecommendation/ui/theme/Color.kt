package com.simats.multimodaltransportationrecommendation.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Tourism Palette
val SkyBluePrimary = Color(0xFF0284C7)
val SkyBlueDark = Color(0xFF0369A1)
val SkyBlueLight = Color(0xFFE0F2FE)
val SkyBlueSoft = Color(0xFFBAE6FD)

val TealSecondary = Color(0xFF0D9488)
val TealDark = Color(0xFF0F766E)
val TealLight = Color(0xFFCCFBF1)
val TealSoft = Color(0xFF99F6E4)

val WarmOrangeAccent = Color(0xFFF97316)
val WarmOrangeLight = Color(0xFFFFEDD5)
val WarmOrangeDark = Color(0xFFEA580C)

val SunshineGold = Color(0xFFEAB308)
val EmeraldGreen = Color(0xFF10B981)

// Background & Surfaces
val BackgroundSoftCream = Color(0xFFF8FAFC)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceCard = Color(0xFFFFFFFF)

// Pastel Card Tints
val PastelCardBlue = Color(0xFFF0F9FF)
val PastelCardTeal = Color(0xFFF0FDF4)
val PastelCardPeach = Color(0xFFFFF7ED)
val PastelCardPurple = Color(0xFFFAF5FF)

// Text Colors
val TextNavyDark = Color(0xFF0F172A)
val TextNavyBody = Color(0xFF334155)
val TextMuted = Color(0xFF64748B)
val TextMutedSlate = Color(0xFF64748B)
val TextLight = Color(0xFF94A3B8)

// Borders & Dividers
val BorderLight = Color(0xFFE2E8F0)
val BorderSubtle = Color(0xFFF1F5F9)

// Gradients
val PrimaryGradient = Brush.horizontalGradient(
    listOf(SkyBluePrimary, TealSecondary)
)

val WarmAccentGradient = Brush.horizontalGradient(
    listOf(WarmOrangeAccent, Color(0xFFF59E0B))
)

val HeroBannerGradient = Brush.linearGradient(
    listOf(Color(0xFF0284C7), Color(0xFF0D9488), Color(0xFF059669))
)

val CardGlowBlue = Brush.verticalGradient(
    listOf(Color(0xFFE0F2FE), Color(0xFFFFFFFF))
)
