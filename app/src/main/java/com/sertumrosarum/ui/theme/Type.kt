package com.sertumrosarum.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class SertumTypography(
    val headline: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    val title: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    val body: TextStyle = TextStyle(fontSize = 16.sp),
    val label: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
)

val DefaultTypography = SertumTypography()
