package com.jaborzodafayzali.qurantj

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val Emerald=Color(0xFF0B6B4F)
private val Deep=Color(0xFF031B13)
private val Gold=Color(0xFFD7B86A)
private val Cream=Color(0xFFF7F2E7)

@Composable
fun QuranTJApp(quran:QuranViewModel){
    val prayer:PrayerViewModel=viewModel()
    MaterialTheme(
        colorScheme=if(quran.dark) darkColorScheme(primary=Color(0xFF62D5AC),secondary=Gold,background=Deep,surface=Color(0xFF0A241B))
        else lightColorScheme(primary=Emerald,secondary=Color(0xFF9A7129),background=Cream,surface=Color.White)
    ){
        if(quran.selectedSurah!=null) Reader(quran,quran.selectedSurah!!)
        else MainShell(quran,prayer)
    }
}

@Composable
private fun MainShell(q:QuranViewModel,p:PrayerViewModel){
    var tab by remember{mutableIntStateOf(0)}
    Scaffold(bottomBar={
        NavigationBar{
            NavigationBarItem(selected=tab==0,onClick={tab=0},icon={Text("⌂")},label={Text(UiTexts.of(q.language.code).home)})
            NavigationBarItem(selected=tab==1,onClick={tab=1},icon={Text("☾")},label={Text(UiTexts.of(q.language.code).quran)})
            NavigationBarItem(selected=tab==2,onClick={tab=2},icon={Text("🧭")},label={Text(UiTexts.of(q.language.code).qibla)})
        }
    }){pad->
        when(tab){
            0->Dashboard(q,p,Modifier.padding(pad))
            1->QuranHome(q,Modifier.padding(pad))
            else->Qibla(p,Modifier.padding(pad))
        }
    }
}

@Composable
private fun Dashboard(q:QuranViewModel,p:PrayerViewModel,modifier:Modifier){
    val context=LocalContext.current
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){r->
        if(r[Manifest.permission.ACCESS_FINE_LOCATION]==true||r[Manifest.permission.ACCESS_COARSE_LOCATION]==true) locate(context,p)
    }
    LaunchedEffect(Unit){
        val fine=ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
        val coarse=ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        if(fine||coarse) locate(context,p)
    }
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=28.dp)){
        item{
            Hero(q,p)
            if(p.location==null){
                Card(Modifier.fillMaxWidth().padding(16.dp),shape=RoundedCornerShape(24.dp)){
                    Column(Modifier.padding(18.dp)){
                        Text("Вақтҳои намоз барои шаҳри шумо",fontWeight=FontWeight.Bold,fontSize=18.sp)
                        Spacer(Modifier.height(5.dp))
                        Text("Ҷойгиршавиро иҷозат диҳед. Барнома шаҳр, вақти маҳаллӣ ва самти қибларо муайян мекунад.",fontSize=13.sp)
                        Spacer(Modifier.height(12.dp))
                        Button({launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))}){Text("Муайян кардани ҷой")}
                    }
                }
            }
        }
        item{PrayerCard(q,p)}
        item{CityPicker(p)}
        item{QiblaMini(p)}
        item{
            Card(Modifier.fillMaxWidth().padding(16.dp).clickable{q.open(surahs.first())},shape=RoundedCornerShape(25.dp)){
                Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(Gold.copy(.18f)),contentAlignment=Alignment.Center){Text("☾",fontSize=31.sp,color=Emerald)}
                    Column(Modifier.padding(start=14.dp)){
                        Text(UiTexts.of(q.language.code).quran,fontSize=18.sp,fontWeight=FontWeight.Bold)
                        Text("114 сура • "+UiTexts.of(q.language.code).translation,fontSize=12.sp)
                    }
                }
            }
        }
    }
}

private fun locate(context:Context,p:PrayerViewModel){
    MainScope().launch{LocationHelper.current(context)?.let{p.load(it.latitude,it.longitude)}}
}

@Composable
private fun Hero(q:QuranViewModel,p:PrayerViewModel){
    Box(Modifier.fillMaxWidth().height(275.dp).background(Brush.verticalGradient(listOf(Color(0xFF02150E),Color(0xFF07533D),Emerald)))){
        Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.SpaceBetween,horizontalAlignment=Alignment.CenterHorizontally){
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                Text("QURAN",color=Gold,fontWeight=FontWeight.ExtraBold,letterSpacing=2.sp)
                TextButton({q.dark=!q.dark}){Text(if(q.dark)"☀" else "☾",color=Color.White,fontSize=22.sp)}
            }
            Column(horizontalAlignment=Alignment.CenterHorizontally){
                Box(Modifier.size(82.dp).clip(RoundedCornerShape(29.dp)).background(Brush.linearGradient(listOf(Gold,Color(0xFF8B6729)))),contentAlignment=Alignment.Center){Text("☾",fontSize=42.sp,color=Deep)}
                Spacer(Modifier.height(10.dp))
                Text("القرآن الكريم",fontSize=30.sp,color=Color.White,fontWeight=FontWeight.SemiBold)
                Text("ҚУРЪОНИ КАРИМ",color=Color.White,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
                Text("Quran • Namaz • Qibla",color=Color.White.copy(.75f),fontSize=12.sp)
            }
            Text(p.location?.let{"📍 "+it.city+", "+it.country}?:"📍 Ҷойгиршавӣ муайян нашудааст",color=Gold,fontSize=12.sp)
        }
    }
}

@Composable
private fun LanguageCard(q: QuranViewModel){
    var open by remember { mutableStateOf(false) }
    val t=UiTexts.of(q.language.code)
    Card(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=8.dp),shape=RoundedCornerShape(22.dp)){
        Row(Modifier.fillMaxWidth().clickable{open=true}.padding(16.dp),verticalAlignment=Alignment.CenterVertically){
            Text("文",fontSize=22.sp,color=Emerald)
            Column(Modifier.weight(1f).padding(start=12.dp)){
                Text(t.chooseLanguage,fontWeight=FontWeight.Bold)
                Text(q.language.nativeName,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))
            }
            Text("⌄",fontSize=20.sp,color=Emerald)
        }
    }
    if(open){
        AlertDialog(
            onDismissRequest={open=false},
            title={Text(t.chooseLanguage,fontWeight=FontWeight.ExtraBold)},
            text={
                LazyColumn(Modifier.heightIn(max=430.dp)){
                    items(QuranLanguages.all(),key={it.code}){lang->
                        Row(
                            Modifier.fillMaxWidth().clickable{q.setLanguage(lang);open=false}.padding(vertical=12.dp),
                            verticalAlignment=Alignment.CenterVertically
                        ){
                            Column(Modifier.weight(1f)){
                                Text(lang.nativeName,fontWeight=FontWeight.SemiBold)
                                Text(lang.englishName,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))
                            }
                            if(lang.code==q.language.code) Text("✓",color=Emerald,fontWeight=FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton={TextButton({open=false}){Text(t.back)}}
        )
    }
}

@Composable
private fun PrayerCard(q:QuranViewModel,p:PrayerViewModel){
    val t=p.times
    Column(Modifier.padding(16.dp)){
        Text(UiTexts.of(q.language.code).prayerTimes,fontSize=21.sp,fontWeight=FontWeight.ExtraBold)
        Text(UiTexts.of(q.language.code).location,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))
        Spacer(Modifier.height(10.dp))
        if(t==null){
            Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp)){Text("Аввал шаҳр ё ҷойгиршавиро интихоб кунед.",Modifier.padding(18.dp))}
        }else{
            Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(Emerald)){
                Column(Modifier.padding(17.dp)){
                    Text("НАМОЗҲОИ ИМРӮЗ",color=Gold,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.3.sp)
                    Text(nextPrayer(t),color=Color.White,fontSize=25.sp,fontWeight=FontWeight.ExtraBold)
                    Text("Аср бо усули Ҳанафӣ ҳисоб мешавад.",color=Color.White.copy(.72f),fontSize=11.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        t.items().forEach{item->
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(13.dp)).background(Color.White.copy(.09f)).padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){
                                Text(item.first,fontSize=8.sp,color=Color.White.copy(.7f),maxLines=1)
                                Text(item.second,fontSize=11.sp,color=Color.White,fontWeight=FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun nextPrayer(t:PrayerTimes):String{
    val now=LocalTime.now()
    val xs=t.items().filter{it.first!="Тулӯи офтоб"}.map{it.first to LocalTime.parse(it.second)}
    return (xs.firstOrNull{it.second.isAfter(now)}?:xs.first()).first
}

@Composable
private fun CityPicker(p:PrayerViewModel){
    Column(Modifier.padding(horizontal=16.dp)){
        Text("Ҷойгиршавӣ",fontWeight=FontWeight.Bold,fontSize=16.sp)
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
            AssistChip({p.useCity(PrayerLocation("Душанбе","Тоҷикистон",38.5598,68.7870))},{Text("Душанбе")})
            AssistChip({p.useCity(PrayerLocation("Бохтар","Тоҷикистон",37.8364,68.7803))},{Text("Бохтар")})
        }
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
            AssistChip({p.useCity(PrayerLocation("Москва","Россия",55.7558,37.6173))},{Text("Москва")})
            AssistChip({p.useCity(PrayerLocation("Алматы","Казахстан",43.2389,76.8897))},{Text("Алматы")})
        }
    }
}

@Composable
private fun QiblaMini(p:PrayerViewModel){
    Card(Modifier.fillMaxWidth().padding(16.dp),shape=RoundedCornerShape(25.dp)){
        Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){
            Text("🧭",fontSize=32.sp)
            Column(Modifier.padding(start=14.dp)){
                Text("Самти қибла",fontWeight=FontWeight.Bold,fontSize=17.sp)
                Text(p.qibla.bearing.roundToInt().toString()+"° аз шимол",fontSize=13.sp)
                Text("Дар бахши «Қибла» компаси зинда фаъол аст.",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))
            }
        }
    }
}

@Composable
private fun Qibla(p:PrayerViewModel,modifier:Modifier){
    Column(modifier.fillMaxSize().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Text("Қибла",fontSize=30.sp,fontWeight=FontWeight.ExtraBold)
        Text(p.location?.city?:"Ҷойгиршавӣ лозим аст",color=MaterialTheme.colorScheme.onSurface.copy(.55f))
        Spacer(Modifier.height(20.dp))
        if(p.location==null) Text("Аз саҳифаи асосӣ ҷойгиршавиро фаъол кунед.")
        else{
            Text(p.qibla.bearing.roundToInt().toString()+"°",fontSize=23.sp,color=Emerald,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            QiblaCompass(p.qibla.bearing)
            Spacer(Modifier.height(16.dp))
            Text("Телефонро ҳамвор нигоҳ доред. Агар компас нодуруст нишон диҳад, онро калибр кунед.",textAlign=TextAlign.Center,fontSize=13.sp)
        }
    }
}

@Composable
private fun QiblaCompass(target:Double){
    val context=LocalContext.current
    var azimuth by remember{mutableFloatStateOf(0f)}
    DisposableEffect(target){
        val sm=context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor=sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val listener=object:SensorEventListener{
            val rotation=FloatArray(9);val orientation=FloatArray(3)
            override fun onSensorChanged(e:SensorEvent){
                SensorManager.getRotationMatrixFromVector(rotation,e.values)
                SensorManager.getOrientation(rotation,orientation)
                azimuth=Math.toDegrees(orientation[0].toDouble()).toFloat()
            }
            override fun onAccuracyChanged(s:Sensor?,a:Int){}
        }
        if(sensor!=null)sm.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_UI)
        onDispose{sm.unregisterListener(listener)}
    }
    val relative=target.toFloat()-azimuth
    Box(Modifier.size(290.dp),contentAlignment=Alignment.Center){
        Canvas(Modifier.fillMaxSize()){
            drawCircle(Emerald.copy(.07f))
            drawCircle(Emerald,style=Stroke(4.dp.toPx()))
            drawCircle(Gold.copy(.22f),radius=size.minDimension*.35f,style=Stroke(2.dp.toPx()))
            drawLine(Emerald,center,Offset(center.x,size.height*.10f),6.dp.toPx(),cap=StrokeCap.Round)
        }
        Text("ҚИБЛА",color=Emerald,fontWeight=FontWeight.ExtraBold,letterSpacing=2.sp)
        Text("КАЪБА",Modifier.offset(y=42.dp),color=Gold,fontWeight=FontWeight.Bold)
    }
}

@Composable
private fun QuranHome(q:QuranViewModel,modifier:Modifier){
    var query by remember{mutableStateOf("")}
    val list=surahs.filter{query.isBlank()||it.tajik.contains(query,true)||it.arabic.contains(query)||it.number.toString()==query}
    LazyColumn(modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=25.dp)){
        item{Column(Modifier.padding(20.dp)){Text(UiTexts.of(q.language.code).quran,fontSize=29.sp,fontWeight=FontWeight.ExtraBold);Text("114 • "+UiTexts.of(q.language.code).translation,color=MaterialTheme.colorScheme.onSurface.copy(.55f));Spacer(Modifier.height(12.dp));OutlinedTextField(query,{query=it},Modifier.fillMaxWidth(),singleLine=true,shape=RoundedCornerShape(20.dp),label={Text(UiTexts.of(q.language.code).search)})}}
        items(list,key={it.number}){s->SurahCard(s){q.open(s)}}
    }
}

@Composable
private fun SurahCard(s:Surah,onClick:()->Unit){
    Card(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=5.dp).clickable{onClick()},shape=RoundedCornerShape(22.dp)){
        Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Emerald.copy(.1f)),contentAlignment=Alignment.Center){Text("%02d".format(s.number),color=Emerald,fontWeight=FontWeight.Bold)}
            Column(Modifier.weight(1f).padding(horizontal=14.dp)){Text(s.tajik,fontWeight=FontWeight.Bold);Text(s.ayahs.toString()+" оят",fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))}
            Text(s.arabic,fontSize=21.sp)
        }
    }
}

@Composable
private fun Reader(q:QuranViewModel,s:Surah){
    Scaffold(topBar={Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),verticalAlignment=Alignment.CenterVertically){TextButton({q.back()}){Text("‹ "+UiTexts.of(q.language.code).back)};Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally){Text(s.tajik,fontWeight=FontWeight.Bold);Text(s.arabic,fontSize=16.sp)};TextButton({q.fontScale=if(q.fontScale==1f)1.2f else 1f}){Text("A⁺")}}}){pad->
        when{
            q.loading->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){CircularProgressIndicator()}
            q.error!=null->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){Text(q.error!!)}
            else->LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(14.dp)){
                item{Text("سُورَةُ "+s.arabic,Modifier.fillMaxWidth(),textAlign=TextAlign.Center,fontSize=27.sp,color=Emerald);Text(s.tajik+" • "+s.ayahs+" оят",Modifier.fillMaxWidth(),textAlign=TextAlign.Center,fontSize=12.sp)}
                items(q.ayahs,key={it.number}){a->Card(Modifier.fillMaxWidth().padding(vertical=6.dp),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(18.dp)){Text(a.arabic,Modifier.fillMaxWidth(),fontSize=(27*q.fontScale).sp,lineHeight=(50*q.fontScale).sp,textAlign=TextAlign.Right,fontFamily=FontFamily.Serif);HorizontalDivider(Modifier.padding(vertical=12.dp),color=Emerald.copy(.12f));Text(a.translation,fontSize=(17*q.fontScale).sp,lineHeight=(29*q.fontScale).sp);Text("﴿ "+a.number+" ﴾",Modifier.fillMaxWidth(),textAlign=TextAlign.End,color=Emerald,fontWeight=FontWeight.Bold)}}}
                item{Text(UiTexts.of(q.language.code).source+": "+q.language.source,Modifier.padding(12.dp),fontSize=10.sp,textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurface.copy(.55f))}
            }
        }
    }
}
