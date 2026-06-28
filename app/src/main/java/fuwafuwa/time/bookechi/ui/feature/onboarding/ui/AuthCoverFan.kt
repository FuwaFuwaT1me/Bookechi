package fuwafuwa.time.bookechi.ui.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import fuwafuwa.time.bookechi.ui.theme.LoraFontFamily

private data class FanItem(
    val title: String,
    val author: String,
    val bg: Color,
    val fg: Color,
    val rot: Float,
    val x: Float,
    val y: Float,
    val z: Float,
)

/** Реальная классика из public domain — безопасно показывать, тёплая знакомая полка. */
private val FanItems = listOf(
    FanItem("Анна Каренина", "Л. Толстой", Color(0xFF7C8A6E), Color(0xFFFBF6EF), rot = -15f, x = -58f, y = 6f, z = 1f),
    FanItem("Преступление и наказание", "Ф. Достоевский", Color(0xFF9E4A2C), Color(0xFFFFF1E6), rot = 15f, x = 58f, y = 6f, z = 1f),
    FanItem("Евгений Онегин", "А. Пушкин", Color(0xFFBE5E3B), Color(0xFFFFF6EE), rot = 0f, x = 0f, y = 0f, z = 3f),
)

/** Веер из трёх обложек книг (герой welcome-варианта «Обложки»). 196×150. */
@Composable
fun CoverFan(modifier: Modifier = Modifier) {
    Box(modifier = modifier.width(196.dp).height(150.dp)) {
        FanItems.forEach { item ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(item.z)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        rotationZ = item.rot
                        translationX = item.x.dp.toPx()
                        translationY = item.y.dp.toPx()
                    },
            ) {
                FanCover(title = item.title, author = item.author, bg = item.bg, fg = item.fg)
            }
        }
    }
}

@Composable
private fun FanCover(title: String, author: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(133.dp)
            .shadow(18.dp, RoundedCornerShape(11.dp), clip = false)
            .clip(RoundedCornerShape(11.dp))
            .background(bg),
    ) {
        // вертикальная «прошивка» корешка слева
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 7.dp)
                .width(2.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.14f)),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, top = 11.dp, bottom = 11.dp, end = 9.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                color = fg,
                fontFamily = LoraFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                lineHeight = 14.5.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = author.uppercase(),
                color = fg.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
