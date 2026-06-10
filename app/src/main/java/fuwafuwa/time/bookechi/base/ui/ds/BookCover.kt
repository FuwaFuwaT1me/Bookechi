package fuwafuwa.time.bookechi.base.ui.ds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fuwafuwa.time.bookechi.ui.theme.BookechiTheme

/** Набор тёплых матовых оттенков для типографских плейсхолдеров обложек. */
private val WarmCoverTints = listOf(
    Color(0xFFA85636),
    Color(0xFF9E4A2C),
    Color(0xFF7C8A6E),
    Color(0xFFA08B7C),
    Color(0xFFC97A53),
    Color(0xFF7E3A22),
    Color(0xFF8C6E54),
)

private fun coverTintFor(title: String): Color {
    val index = ((title.hashCode() % WarmCoverTints.size) + WarmCoverTints.size) % WarmCoverTints.size
    return WarmCoverTints[index]
}

/**
 * Обложка книги со скруглением 12.dp (соотношение 2:3).
 * Если coverPath задан — Coil AsyncImage; иначе матовый типографский плейсхолдер:
 * сплошной тёплый фон (детерминированный по hashCode(title)), название по центру
 * (titleSmall, светлый текст, ellipsis, maxLines 3) и автор капсом внизу.
 */
@Composable
fun BookCover(
    coverPath: String?,
    title: String,
    author: String,
    modifier: Modifier = Modifier,
    width: Dp? = 96.dp,
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier)
            .aspectRatio(2f / 3f)
            .clip(DsShapes.cover),
    ) {
        if (coverPath != null) {
            AsyncImage(
                model = coverPath,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(coverTintFor(title))
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = author.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(name = "BookCover Light", showBackground = true, backgroundColor = 0xFFF4ECE1)
@Composable
private fun BookCoverPreviewLight() {
    BookechiTheme(darkTheme = false) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                BookCover(coverPath = null, title = "Норвежский лес", author = "Харуки Мураками")
                BookCover(coverPath = null, title = "Думай медленно… решай быстро", author = "Даниэль Канеман")
            }
        }
    }
}

@Preview(name = "BookCover Dark", showBackground = true, backgroundColor = 0xFF1C1611)
@Composable
private fun BookCoverPreviewDark() {
    BookechiTheme(darkTheme = true) {
        Surface(color = BookechiTheme.colors.canvas) {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                BookCover(coverPath = null, title = "1984", author = "Джордж Оруэлл")
                BookCover(coverPath = null, title = "Маленькая жизнь", author = "Ханья Янагихара")
            }
        }
    }
}
