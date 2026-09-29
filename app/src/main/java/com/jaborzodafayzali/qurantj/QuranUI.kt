package com.jaborzodafayzali.qurantj

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val dark=vm.dark
    MaterialTheme(
        colorScheme = if(dark) darkColorScheme(
            primary=Color(0xFF63D7AD), secondary=Gold, background=Deep, surface=Color(0xFF0B241B)
        ) else lightColorScheme(
            primary=Emerald, secondary=Color(0xFF9A7129), background=Cream, surface=Color.White
        )
    ) {
        AnimatedContent(targetState=vm.selectedSurah, label="screen") { selected ->
            if(selected==null) HomeScreen(vm) else ReaderScreen(vm, selected)
        }
    }
}

@Composable
private fun HomeScreen(vm:QuranViewModel) {
    var query by remember{mutableStateOf("")}
    val list=surahs.filter { query.isBlank() || it.tajik.contains(query,true) || it.arabic.contains(query) || it.number.toString()==query }
    Scaffold(containerColor=MaterialTheme.colorScheme.background) { pad ->
        LazyColumn(
            modifier=Modifier.fillMaxSize().padding(pad),
            contentPadding=PaddingValues(bottom=32.dp)
        ) {
            item {
                HeroHeader(vm)
                OutlinedTextField(
                    value=query,onValueChange={query=it},
                    modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp,vertical=14.dp),
                    shape=RoundedCornerShape(20.dp),
                    singleLine=true,
                    label={Text("Ҷустуҷӯи сура")},
                    placeholder={Text("Номи сура ё рақам")},
                    trailingIcon={Text("⌕",fontSize=24.sp)}
                )
                Text(
                    "СУРАҲО • 114",
                    modifier=Modifier.padding(horizontal=20.dp,vertical=8.dp),
                    fontWeight=FontWeight.Bold, letterSpacing=1.4.sp,
                    color=MaterialTheme.colorScheme.primary
                )
            }
            items(list,key={it.number}) { s ->
                SurahCard(s){vm.open(s)}
            }
        }
    }
}

@Composable
private fun HeroHeader(vm:QuranViewModel) {
    Box(
        Modifier.fillMaxWidth().height(280.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF052D21),Color(0xFF0B5B45),Color(0xFF0E6B50))))
    ) {
        Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
                TextButton(onClick={vm.dark=!vm.dark}) {
                    Text(if(vm.dark) "☀" else "☾",fontSize=25.sp,color=Color.White)
                }
            }
            Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth()) {
                Box(
                    Modifier.size(86.dp).clip(RoundedCornerShape(28.dp))
                        .background(Brush.linearGradient(listOf(Gold,Color(0xFF9B762F)))),
                    contentAlignment=Alignment.Center
                ){ Text("☪",fontSize=42.sp,color=Deep) }
                Spacer(Modifier.height(14.dp))
                Text("القرآن الكريم",fontSize=30.sp,color=Color.White,fontWeight=FontWeight.SemiBold)
                Text("ҚУРЪОНИ КАРИМ",fontSize=18.sp,color=Color.White.copy(alpha=.9f),fontWeight=FontWeight.Bold,letterSpacing=2.sp)
                Text("бо тарҷумаи тоҷикӣ",fontSize=14.sp,color=Color.White.copy(alpha=.78f))
            }
            Text("JABORZODA FAYZALI",modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.Center,color=Gold,fontSize=11.sp,letterSpacing=2.sp)
        }
    }
}

@Composable
private fun SurahCard(s:Surah,onClick:()->Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=5.dp).clickable{onClick()},
        shape=RoundedCornerShape(22.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
        elevation=CardDefaults.cardElevation(2.dp)
    ){
        Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha=.1f)),
                contentAlignment=Alignment.Center
            ){Text("%02d".format(s.number),fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary)}
            Column(Modifier.weight(1f).padding(horizontal=14.dp)) {
                Text(s.tajik,fontWeight=FontWeight.Bold,fontSize=16.sp)
                Text("${s.ayahs} оят",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f))
            }
            Text(s.arabic,fontSize=22.sp,fontWeight=FontWeight.Medium,textAlign=TextAlign.Right)
        }
    }
}

@Composable
private fun ReaderScreen(vm:QuranViewModel,s:Surah) {
    Scaffold(
        containerColor=MaterialTheme.colorScheme.background,
        topBar={
            Surface(shadowElevation=4.dp,color=MaterialTheme.colorScheme.surface) {
                Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal=10.dp,vertical=10.dp),verticalAlignment=Alignment.CenterVertically) {
                    TextButton(onClick={vm.back()}){Text("‹  Бозгашт")}
                    Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally){
                        Text(s.tajik,fontWeight=FontWeight.Bold)
                        Text(s.arabic,fontSize=17.sp)
                    }
                    TextButton(onClick={
                        vm.fontScale=when(vm.fontScale){1f->1.15f;1.15f->1.3f;else->1f}
                    }){Text("A⁺",fontWeight=FontWeight.Bold)}
                }
            }
        }
    ){pad ->
        when {
            vm.loading -> Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    CircularProgressIndicator(color=MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(14.dp))
                    Text("Матни сура бор мешавад…")
                }
            }
            vm.error!=null -> Box(Modifier.fillMaxSize().padding(pad).padding(24.dp),contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Text("⚠",fontSize=42.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(vm.error!!,textAlign=TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick={vm.open(s)}){Text("Дубора кӯшиш кардан")}
                }
            }
            else -> LazyColumn(
                Modifier.fillMaxSize().padding(pad),
                contentPadding=PaddingValues(start=14.dp,end=14.dp,top=18.dp,bottom=40.dp)
            ){
                item{
                    Column(Modifier.fillMaxWidth().padding(bottom=12.dp),horizontalAlignment=Alignment.CenterHorizontally){
                        Text("سُورَةُ ${s.arabic}",fontSize=28.sp,color=MaterialTheme.colorScheme.primary,textAlign=TextAlign.Center)
                        Text("${s.tajik} • ${s.ayahs} оят",fontSize=13.sp,color=MaterialTheme.colorScheme.onSurface.copy(alpha=.6f))
                    }
                }
                items(vm.ayahs,key={it.number}){a->AyahCard(a,vm.fontScale)}
                item{
                    Text(
                        "Матни арабӣ: Tanzil Project, Uthmani v1.1. Манбаи тарҷума: QuranEnc.com — тарҷумаи тоҷикӣ, Rowwad Translation Center. Матнҳо бетағйир истифода мешаванд.",
                        modifier=Modifier.padding(12.dp),fontSize=10.sp,lineHeight=15.sp,
                        color=MaterialTheme.colorScheme.onSurface.copy(alpha=.55f),textAlign=TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun AyahCard(a:Ayah,scale:Float) {
    Card(
        Modifier.fillMaxWidth().padding(vertical=7.dp),
        shape=RoundedCornerShape(24.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),
        elevation=CardDefaults.cardElevation(1.dp)
    ){
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(
                a.arabic,
                Modifier.fillMaxWidth(),
                fontSize=(27*scale).sp,
                lineHeight=(50*scale).sp,
                textAlign=TextAlign.Right,
                fontFamily=FontFamily.Serif
            )
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color=MaterialTheme.colorScheme.primary.copy(alpha=.12f))
            Spacer(Modifier.height(12.dp))
            Text(
                a.translation,
                Modifier.fillMaxWidth(),
                fontSize=(17*scale).sp,
                lineHeight=(29*scale).sp,
                textAlign=TextAlign.Start
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "﴿ ${a.number} ﴾",
                Modifier.fillMaxWidth(),
                textAlign=TextAlign.End,
                color=MaterialTheme.colorScheme.primary,
                fontSize=12.sp,
                fontWeight=FontWeight.Bold
            )
        }
    }
}
