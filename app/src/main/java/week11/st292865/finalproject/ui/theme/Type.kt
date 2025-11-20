package week11.st292865.finalproject.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlack
    ),
    headlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextBlack
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        color = TextBlack
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        color = TextGray
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = BackgroundWhite
    )
)
