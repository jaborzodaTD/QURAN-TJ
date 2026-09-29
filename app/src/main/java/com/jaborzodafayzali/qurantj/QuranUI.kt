package com.jaborzodafayzali.qurantj

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Emerald = Color(0xFF0E6B50)
private val Deep = Color(0xFF061A13)
private val Gold = Color(0xFFD6B56A)
private val Cream = Color(0xFFF7F2E7)

@Composable
fun QuranTJApp(vm: QuranViewModel) {
    val dark = vm.dark
    MaterialTheme(
        colorScheme = if (dark) darkColorScheme(
            primary = Color(0xFF63D7AD),
            secondary = Gold,
            background = Deep,
            surface = Color(0xFF0B241B)
        ) else lightColorScheme(
            primary = Emerald,
            secondary = Color(0xFF9A7129),
            background = Cream,
            surface = Color.White
        )
    ) {
        AnimatedContent(targetState = vm.selectedSurah, label = "screen") { selected: Surah? ->
            if (selected == null) HomeScreen(vm) else ReaderScreen(vm, selected)
        }
    }
}

@Composable
private fun HomeScreen(vm: QuranViewModel) {
    var query by remember { mutableStateOf("") }
    val list = surahs.filter {
        query.isBlank() ||
            it.tajik.contains(query, true) ||
            it.arabic.contains(query) ||
            it.number.toString() == query
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                PremiumHero(vm)
                SectionSwitcher()

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    label = { Text("Ҷустуҷӯи сура") },
                    placeholder = { Text("Номи сура ё рақам") },
                    leadingIcon = {
                        Text("⌕", fontSize = 25.sp, color = MaterialTheme.colorScheme.primary)
                    }
                )

                Text(
                    "СУРАҲО • ${list.size} / 114",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(list, key = { it.number }) { s ->
                SurahCard(s) { vm.open(s) }
            }
        }
    }
}

@Composable
private fun PremiumHero(vm: QuranViewModel) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF031B13), Color(0xFF07553F), Color(0xFF0E6B50))
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val gold = Gold.copy(alpha = .14f)
            for (i in 0..7) {
                drawCircle(
                    color = gold,
                    radius = 90f + i * 35f,
                    center = Offset(size.width * .78f, size.height * .18f)
                )
            }
        }

        Column(
            Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "QURAN TJ",
                    color = Gold,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.2.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Surface(
                    color = Color.White.copy(alpha = .10f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    TextButton(onClick = { vm.dark = !vm.dark }) {
                        Text(if (vm.dark) "☀" else "☾", fontSize = 23.sp, color = Color.White)
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(92.dp),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.Transparent,
                    shadowElevation = 14.dp
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Gold, Color(0xFF8C6727))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        QuranEmblem()
                    }
                }
                Spacer(Modifier.height(13.dp))
                Text(
                    "القرآن الكريم",
                    fontSize = 31.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "ҚУРЪОНИ КАРИМ",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = .94f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "бо тарҷумаи тоҷикӣ",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = .76f)
                )
            }

            Text(
                "JABORZODA FAYZALI",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Gold,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun SectionSwitcher() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PremiumFeatureCard(
            modifier = Modifier.weight(1f),
            title = "Сураҳо",
            subtitle = "114 сура",
            icon = { SurahIcon() }
        )
        PremiumFeatureCard(
            modifier = Modifier.weight(1f),
            title = "Тарҷума",
            subtitle = "Тоҷикӣ · оят ба оят",
            icon = { TranslationIcon() }
        )
    }
}

@Composable
private fun PremiumFeatureCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = modifier.height(94.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            Modifier.fillMaxSize().padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(54.dp).clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = .10f)),
                contentAlignment = Alignment.Center
            ) { icon() }

            Column(Modifier.padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .58f)
                )
            }
        }
    }
}

@Composable
private fun SurahIcon() {
    Canvas(Modifier.size(31.dp)) {
        val c = MaterialTheme.colorScheme.primary
        drawRoundRect(
            color = c,
            topLeft = Offset(size.width * .20f, size.height * .12f),
            size = androidx.compose.ui.geometry.Size(size.width * .58f, size.height * .76f),
            cornerRadius = CornerRadius(5.dp.toPx())
        )
        drawLine(c.copy(alpha = .30f), Offset(size.width * .32f, size.height * .36f), Offset(size.width * .66f, size.height * .36f), 2.dp.toPx())
        drawLine(c.copy(alpha = .30f), Offset(size.width * .32f, size.height * .50f), Offset(size.width * .66f, size.height * .50f), 2.dp.toPx())
        drawLine(c.copy(alpha = .30f), Offset(size.width * .32f, size.height * .64f), Offset(size.width * .58f, size.height * .64f), 2.dp.toPx())
    }
}

@Composable
private fun TranslationIcon() {
    Canvas(Modifier.size(32.dp)) {
        val c = MaterialTheme.colorScheme.primary
        drawRoundRect(
            color = c,
            topLeft = Offset(size.width * .08f, size.height * .12f),
            size = androidx.compose.ui.geometry.Size(size.width * .84f, size.height * .72f),
            cornerRadius = CornerRadius(7.dp.toPx()),
            style = Stroke(width = 2.6.dp.toPx())
        )
        drawLine(c, Offset(size.width * .22f, size.height * .36f), Offset(size.width * .78f, size.height * .36f), 2.2.dp.toPx())
        drawLine(c, Offset(size.width * .22f, size.height * .55f), Offset(size.width * .62f, size.height * .55f), 2.2.dp.toPx())
        drawLine(c, Offset(size.width * .22f, size.height * .70f), Offset(size.width * .48f, size.height * .70f), 2.2.dp.toPx())
    }
}

@Composable
private fun QuranEmblem() {
    Canvas(Modifier.size(50.dp)) {
        val ink = Deep
        val p = Path().apply {
            moveTo(size.width * .18f, size.height * .72f)
            quadraticBezierTo(size.width * .30f, size.height * .25f, size.width * .50f, size.height * .42f)
            quadraticBezierTo(size.width * .70f, size.height * .25f, size.width * .82f, size.height * .72f)
            quadraticBezierTo(size.width * .66f, size.height * .60f, size.width * .50f, size.height * .75f)
            quadraticBezierTo(size.width * .34f, size.height * .60f, size.width * .18f, size.height * .72f)
        }
        drawPath(p, color = ink, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        drawLine(
            ink,
            Offset(size.width * .50f, size.height * .42f),
            Offset(size.width * .50f, size.height * .75f),
            2.5.dp.toPx()
        )
    }
}

@Composable
private fun SurahCard(s: Surah, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp).clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = .1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("%02d".format(s.number), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(s.tajik, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "${s.ayahs} оят",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f)
                )
            }
            Text(s.arabic, fontSize = 22.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Right)
        }
    }
}

@Composable
private fun ReaderScreen(vm: QuranViewModel, s: Surah) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { vm.back() }) { Text("‹  Бозгашт") }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.tajik, fontWeight = FontWeight.Bold)
                        Text(s.arabic, fontSize = 17.sp)
                    }
                    TextButton(onClick = {
                        vm.fontScale = when (vm.fontScale) {
                            1f -> 1.15f
                            1.15f -> 1.3f
                            else -> 1f
                        }
                    }) { Text("A⁺", fontWeight = FontWeight.Bold) }
                }
            }
        }
    ) { pad ->
        when {
            vm.loading -> Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(14.dp))
                    Text("Матни сура бор мешавад…")
                }
            }
            vm.error != null -> Box(
                Modifier.fillMaxSize().padding(pad).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠", fontSize = 42.sp)
                    Spacer(Modifier.height(10.dp))
                    val message = vm.error ?: ""
                    Text(message, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { vm.open(s) }) { Text("Дубора кӯшиш кардан") }
                }
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 18.dp, bottom = 40.dp)
            ) {
                item {
                    Column(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("سُورَةُ ${s.arabic}", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                        Text("${s.tajik} • ${s.ayahs} оят", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                    }
                }
                items(items = vm.ayahs, key = { ayah: Ayah -> ayah.number }) { a: Ayah -> AyahCard(a, vm.fontScale) }
                item {
                    Text(
                        "Матни арабӣ: Tanzil Project, Uthmani v1.1. Манбаи тарҷума: QuranEnc.com — тарҷумаи тоҷикӣ, Rowwad Translation Center. Матнҳо бетағйир истифода мешаванд.",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AyahCard(a: Ayah, scale: Float) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 7.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(
                a.arabic,
                Modifier.fillMaxWidth(),
                fontSize = (27 * scale).sp,
                lineHeight = (50 * scale).sp,
                textAlign = TextAlign.Right,
                fontFamily = FontFamily.Serif
            )
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = .12f))
            Spacer(Modifier.height(12.dp))
            Text(
                a.translation,
                Modifier.fillMaxWidth(),
                fontSize = (17 * scale).sp,
                lineHeight = (29 * scale).sp,
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "﴿ ${a.number} ﴾",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
