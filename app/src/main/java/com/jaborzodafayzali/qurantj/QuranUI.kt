package com.jaborzodafayzali.qurantj

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.math.roundToInt

private val E=Color(0xFF0B6B4F)
private val D=Color(0xFF031B13)
private val G=Color(0xFFD7B86A)
private val C=Color(0xFFF7F2E7)

private val cities=listOf(
 PrayerLocation("Душанбе","Тоҷикистон",38.5598,68.7870),PrayerLocation("Хуҷанд","Тоҷикистон",40.2833,69.6333),
 PrayerLocation("Бохтар","Тоҷикистон",37.8364,68.7803),PrayerLocation("Кӯлоб","Тоҷикистон",37.9146,69.7845),
 PrayerLocation("Алматы","Казахстан",43.2389,76.8897),PrayerLocation("Астана","Казахстан",51.1694,71.4491),
 PrayerLocation("Шымкент","Казахстан",42.3417,69.5901),PrayerLocation("Караганда","Казахстан",49.8060,73.0850),
 PrayerLocation("Москва","Россия",55.7558,37.6173),PrayerLocation("Санкт-Петербург","Россия",59.9343,30.3351),
 PrayerLocation("Казань","Россия",55.7961,49.1064),PrayerLocation("Махачкала","Россия",42.9849,47.5047),
 PrayerLocation("Грозный","Россия",43.3178,45.6989),PrayerLocation("Ташкент","Узбекистан",41.2995,69.2401),
 PrayerLocation("Самарканд","Узбекистан",39.6542,66.9597),PrayerLocation("Бишкек","Кыргызстан",42.8746,74.5698),
 PrayerLocation("Ош","Кыргызстан",40.5139,72.8161),PrayerLocation("Стамбул","Турция",41.0082,28.9784),
 PrayerLocation("Анкара","Турция",39.9334,32.8597),PrayerLocation("Баку","Азербайджан",40.4093,49.8671),
 PrayerLocation("Тбилиси","Грузия",41.7151,44.8271),PrayerLocation("Ашхабад","Туркменистан",37.9601,58.3261)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun QuranTJApp(q:QuranViewModel){
 val p:PrayerViewModel=viewModel(); var tab by remember{mutableIntStateOf(0)}; var settings by remember{mutableStateOf(false)}
 MaterialTheme(if(q.dark)darkColorScheme(primary=Color(0xFF62D5AC),secondary=G,background=D,surface=Color(0xFF0A241B)) else lightColorScheme(primary=E,secondary=G,background=C,surface=Color.White)){
  if(q.selectedSurah!=null) Reader(q,q.selectedSurah!!) else Scaffold(
   topBar={CenterAlignedTopAppBar(title={Text("QURAN",fontWeight=FontWeight.ExtraBold,letterSpacing=2.sp)},actions={IconButton({settings=true}){Text("⚙")}})},
   bottomBar={NavigationBar{
    NavigationBarItem(selected=tab==0,onClick={tab=0},icon={Text("⌂")},label={Text(UiTexts.of(q.language.code).home)})
    NavigationBarItem(selected=tab==1,onClick={tab=1},icon={Text("☾")},label={Text(UiTexts.of(q.language.code).quran)})
    NavigationBarItem(selected=tab==2,onClick={tab=2},icon={Text("🤲")},label={Text("Дуа")})
    NavigationBarItem(selected=tab==3,onClick={tab=3},icon={Text("🧭")},label={Text(UiTexts.of(q.language.code).qibla)})
   }}
  ){pad->when(tab){0->Dashboard(q,p,Modifier.padding(pad));1->QuranHome(q,Modifier.padding(pad));2->DuaHome(q,Modifier.padding(pad));else->Qibla(p,Modifier.padding(pad))}}
  if(settings)SettingsDialog(q,p){settings=false}
 }
}

@Composable private fun Dashboard(q:QuranViewModel,p:PrayerViewModel,m:Modifier){
 val ctx=LocalContext.current
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){r->if(r.values.any{it})locate(ctx,p)}
 LaunchedEffect(Unit){val a=ContextCompat.checkSelfPermission(ctx,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;val b=ContextCompat.checkSelfPermission(ctx,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED;if(a||b)locate(ctx,p)}
 LazyColumn(m.fillMaxSize(),contentPadding=PaddingValues(bottom=24.dp)){
  item{Hero(q,p)}
  if(p.location==null)item{Card(Modifier.fillMaxWidth().padding(16.dp),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(18.dp)){Text("Вақти намоз ва қибла",fontSize=18.sp,fontWeight=FontWeight.Bold);Text("Ҷойгиршавӣ диҳед ё шаҳрро аз танзимот интихоб кунед.",fontSize=13.sp);Spacer(Modifier.height(10.dp));Button({launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))}){Text("Муайян кардани ҷой")}}}}
  item{PrayerCard(q,p)};item{CityPicker(p)};item{QiblaMini(p)}
  item{QuickCard("🤲","Дуоҳо","Дуоҳои Қуръонӣ"){}};item{QuickCard("☾","Қуръон","114 сура"){q.open(surahs.first())}}
 }
}

@Composable private fun Hero(q:QuranViewModel,p:PrayerViewModel)=Box(Modifier.fillMaxWidth().height(245.dp).background(Brush.verticalGradient(listOf(D,Color(0xFF07533D),E)))){
 Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.SpaceBetween,horizontalAlignment=Alignment.CenterHorizontally){
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("QURAN",color=G,fontWeight=FontWeight.ExtraBold,letterSpacing=2.sp);TextButton({q.dark=!q.dark}){Text(if(q.dark)"☀" else "☾",color=Color.White)}}
  Column(horizontalAlignment=Alignment.CenterHorizontally){Text("القرآن الكريم",fontSize=30.sp,color=Color.White);Text("ҚУРЪОНИ КАРИМ",color=Color.White,fontWeight=FontWeight.Bold);Text("Quran • Namaz • Qibla • Dua",color=Color.White.copy(.75f),fontSize=12.sp)}
  Text(p.location?.let{"📍 ${it.city}, ${it.country}"}?:"📍 Ҷойгиршавӣ муайян нашудааст",color=G,fontSize=12.sp)
 }
}

@Composable private fun QuickCard(icon:String,title:String,sub:String,click:()->Unit)=Card(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=6.dp).clickable{click()},shape=RoundedCornerShape(23.dp)){
 Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){Text(icon,fontSize=28.sp);Column(Modifier.padding(start=14.dp)){Text(title,fontSize=17.sp,fontWeight=FontWeight.Bold);Text(sub,fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(.58f))}}
}
private fun locate(c:Context,p:PrayerViewModel){MainScope().launch{LocationHelper.current(c)?.let{p.load(it.latitude,it.longitude)}}}

private fun label(code:String,key:String)=when(code){
 "tg"->mapOf("Fajr" to "Бомдод","Sunrise" to "Тулӯи офтоб","Dhuhr" to "Пешин","Asr" to "Аср","Maghrib" to "Шом","Isha" to "Хуфтан")
 "ru"->mapOf("Fajr" to "Фаджр","Sunrise" to "Восход","Dhuhr" to "Зухр","Asr" to "Аср","Maghrib" to "Магриб","Isha" to "Иша")
 "kk"->mapOf("Fajr" to "Бамдат","Sunrise" to "Күн шығуы","Dhuhr" to "Бесін","Asr" to "Екінті","Maghrib" to "Ақшам","Isha" to "Құптан")
 else->mapOf("Fajr" to "Fajr","Sunrise" to "Sunrise","Dhuhr" to "Dhuhr","Asr" to "Asr","Maghrib" to "Maghrib","Isha" to "Isha")
}[key]?:key
private fun methodName(v:Int)=when(v){0->"Jafari";1->"Karachi";2->"ISNA";3->"Muslim World League";4->"Umm Al-Qura";5->"Egypt";7->"Tehran";8->"Gulf";9->"Kuwait";10->"Qatar";11->"Singapore";12->"France";13->"Diyanet";14->"Russia";15->"Moonsighting";16->"Dubai";17->"Malaysia";18->"Tunisia";19->"Algeria";20->"Indonesia";21->"Morocco";22->"Lisbon";23->"Jordan";else->"Custom"}

@Composable private fun PrayerCard(q:QuranViewModel,p:PrayerViewModel){
 val t=p.times;Column(Modifier.padding(16.dp)){Text(UiTexts.of(q.language.code).prayerTimes,fontSize=21.sp,fontWeight=FontWeight.ExtraBold);Spacer(Modifier.height(8.dp))
 if(t==null)Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp)){Text("Аввал шаҳр ё ҷойгиршавиро интихоб кунед.",Modifier.padding(18.dp))}
 else Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(E)){Column(Modifier.padding(16.dp)){
  Text("НАМОЗҲОИ ИМРӮЗ",color=G,fontSize=11.sp,fontWeight=FontWeight.Bold);Text(next(t,q.language.code),color=Color.White,fontSize=22.sp,fontWeight=FontWeight.ExtraBold)
  Text("Метод: ${methodName(t.method)} • ${if(t.school==1)"Ҳанафӣ" else "Шофеъӣ"}",color=Color.White.copy(.7f),fontSize=10.sp);Spacer(Modifier.height(10.dp))
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(3.dp)){t.items().forEach{v->Column(Modifier.weight(1f).clip(RoundedCornerShape(11.dp)).background(Color.White.copy(.08f)).padding(6.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(label(q.language.code,v.first),fontSize=7.sp,color=Color.White.copy(.7f),maxLines=1);Text(v.second,fontSize=10.sp,color=Color.White,fontWeight=FontWeight.Bold)}}}
 }}}}
private fun next(t:PrayerTimes,code:String):String{val n=LocalTime.now();val x=t.items().filter{it.first!="Sunrise"}.mapNotNull{runCatching{it.first to LocalTime.parse(it.second.take(5))}.getOrNull()};return(x.firstOrNull{it.second.isAfter(n)}?:x.firstOrNull())?.let{"${label(code,it.first)} • ${it.second}"}?:""}

@Composable private fun CityPicker(p:PrayerViewModel){
 var open by remember{mutableStateOf(false)}
 Card(Modifier.fillMaxWidth().padding(horizontal=16.dp),shape=RoundedCornerShape(22.dp)){Row(Modifier.fillMaxWidth().clickable{open=true}.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text("📍",fontSize=24.sp);Column(Modifier.weight(1f).padding(start=12.dp)){Text("Шаҳр",fontWeight=FontWeight.Bold);Text(p.location?.city?:"Интихоб кунед",fontSize=12.sp)};Text("›",fontSize=24.sp,color=E)}}
 if(open)AlertDialog(onDismissRequest={open=false},title={Text("Шаҳрро интихоб кунед")},text={LazyColumn(Modifier.heightIn(max=470.dp)){items(cities){c->Row(Modifier.fillMaxWidth().clickable{p.useCity(c);open=false}.padding(vertical=10.dp)){Column{Text(c.city,fontWeight=FontWeight.SemiBold);Text(c.country,fontSize=11.sp)}}}}},confirmButton={TextButton({open=false}){Text("Бекор кардан")}})
}
@Composable private fun QiblaMini(p:PrayerViewModel)=Card(Modifier.fillMaxWidth().padding(16.dp),shape=RoundedCornerShape(24.dp)){Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){Text("🧭",fontSize=30.sp);Column(Modifier.padding(start=12.dp)){Text("Самти қибла",fontWeight=FontWeight.Bold);Text("${p.qibla.bearing.roundToInt()}° аз шимол",fontSize=12.sp)}}}

@Composable private fun Qibla(p:PrayerViewModel,m:Modifier)=Column(m.fillMaxSize().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){
 Text("Қибла",fontSize=30.sp,fontWeight=FontWeight.ExtraBold);Text(p.location?.city?:"Ҷойгиршавӣ лозим аст",color=MaterialTheme.colorScheme.onSurface.copy(.55f));Spacer(Modifier.height(15.dp))
 if(p.location==null)Text("Шаҳр ё ҷойгиршавиро аз саҳифаи асосӣ интихоб кунед.",textAlign=TextAlign.Center) else {Text("${p.qibla.bearing.roundToInt()}°",fontSize=24.sp,color=E,fontWeight=FontWeight.Bold);QiblaCompass(p.qibla.bearing);Text("Телефонро ҳамвор нигоҳ доред ва компасро калибр кунед.",textAlign=TextAlign.Center,fontSize=13.sp)}
}
@Composable private fun QiblaCompass(target:Double){
 val ctx=LocalContext.current;var az by remember{mutableFloatStateOf(0f)}
 DisposableEffect(Unit){val sm=ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager;val s=sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);val l=object:SensorEventListener{val r=FloatArray(9);val o=FloatArray(3);override fun onSensorChanged(e:SensorEvent){SensorManager.getRotationMatrixFromVector(r,e.values);SensorManager.getOrientation(r,o);az=Math.toDegrees(o[0].toDouble()).toFloat()};override fun onAccuracyChanged(s:Sensor?,a:Int){}};if(s!=null)sm.registerListener(l,s,SensorManager.SENSOR_DELAY_UI);onDispose{sm.unregisterListener(l)}}
 val rel=((target.toFloat()-az+540f)%360f)-180f
 Box(Modifier.size(270.dp),contentAlignment=Alignment.Center){Canvas(Modifier.fillMaxSize()){drawCircle(E.copy(.06f));drawCircle(E,style=Stroke(4.dp.toPx()));drawCircle(G.copy(.22f),radius=size.minDimension*.35f,style=Stroke(2.dp.toPx()));rotate(rel){drawLine(G,center,Offset(center.x,size.height*.1f),7.dp.toPx(),cap=StrokeCap.Round)}};Text("🕋",fontSize=38.sp)}
}

@Composable private fun QuranHome(q:QuranViewModel,m:Modifier){
 var query by remember{mutableStateOf("")};val list=surahs.filter{query.isBlank()||it.tajik.contains(query,true)||it.arabic.contains(query)||it.number.toString()==query}
 LazyColumn(m.fillMaxSize(),contentPadding=PaddingValues(bottom=20.dp)){item{Column(Modifier.padding(20.dp)){Text(UiTexts.of(q.language.code).quran,fontSize=29.sp,fontWeight=FontWeight.ExtraBold);Text("114 сура • ${q.language.nativeName}",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f));Spacer(Modifier.height(10.dp));OutlinedTextField(query,{query=it},Modifier.fillMaxWidth(),singleLine=true,label={Text(UiTexts.of(q.language.code).search)},shape=RoundedCornerShape(20.dp))}};items(list,key={it.number}){s->Card(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=5.dp).clickable{q.open(s)},shape=RoundedCornerShape(21.dp)){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Text("%02d".format(s.number),color=E,fontWeight=FontWeight.Bold);Column(Modifier.weight(1f).padding(horizontal=14.dp)){Text(s.tajik,fontWeight=FontWeight.Bold);Text("${s.ayahs} оят",fontSize=11.sp)};Text(s.arabic,fontSize=21.sp)}}}}
}

@Composable private fun DuaHome(q:QuranViewModel,m:Modifier){
 val cats=listOf("Ҳама","Тахаджуд","Пас аз хӯрок","Пеш аз хоб")
 var selected by remember{mutableStateOf("Ҳама")}
 val data=if(selected=="Ҳама")DuaData.items else DuaData.items.filter{it.category==selected}
 LazyColumn(m.fillMaxSize(),contentPadding=PaddingValues(16.dp)){
  item{
   Text("Дуоҳо",fontSize=30.sp,fontWeight=FontWeight.ExtraBold)
   Text("Дуоҳои Қуръонӣ ва зикрҳои саҳеҳ",fontSize=12.sp,color=MaterialTheme.colorScheme.onSurface.copy(.58f))
   Spacer(Modifier.height(12.dp))
   LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(cats){cat->FilterChip(selected=selected==cat,onClick={selected=cat},label={Text(cat)})}}
   Spacer(Modifier.height(8.dp))
  }
  items(data,key={it.id}){d->Card(Modifier.fillMaxWidth().padding(vertical=6.dp),shape=RoundedCornerShape(24.dp)){
   Column(Modifier.padding(18.dp)){
    Text(d.category.uppercase(),fontSize=10.sp,color=G,fontWeight=FontWeight.Bold)
    Text(d.title,fontSize=18.sp,fontWeight=FontWeight.ExtraBold)
    Spacer(Modifier.height(12.dp))
    if(d.arabic.isNotBlank()) Text(d.arabic,Modifier.fillMaxWidth(),textAlign=TextAlign.Right,fontSize=24.sp,lineHeight=42.sp,fontFamily=FontFamily.Serif)
    Spacer(Modifier.height(10.dp));HorizontalDivider()
    Spacer(Modifier.height(10.dp));Text(d.tajik,fontSize=16.sp,lineHeight=25.sp)
    Spacer(Modifier.height(9.dp));Text(d.source,fontSize=10.sp,color=MaterialTheme.colorScheme.onSurface.copy(.5f))
   }
  }}
 }
}
@Composable private fun Reader(q:QuranViewModel,s:Surah){
 Scaffold(topBar={Row(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),verticalAlignment=Alignment.CenterVertically){TextButton({q.back()}){Text("‹ "+UiTexts.of(q.language.code).back)};Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally){Text(s.tajik,fontWeight=FontWeight.Bold);Text(s.arabic,fontSize=15.sp)};TextButton({q.fontScale=if(q.fontScale>=1.5f)1f else q.fontScale+.25f}){Text("A⁺")}}}){pad->when{q.loading->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){CircularProgressIndicator()};q.error!=null->Box(Modifier.fillMaxSize().padding(pad),contentAlignment=Alignment.Center){Text(q.error!!,Modifier.padding(24.dp),textAlign=TextAlign.Center)};else->LazyColumn(Modifier.fillMaxSize().padding(pad),contentPadding=PaddingValues(14.dp)){item{Text("سُورَةُ "+s.arabic,Modifier.fillMaxWidth(),textAlign=TextAlign.Center,fontSize=27.sp,color=E);Text(s.tajik+" • "+s.ayahs+" оят",Modifier.fillMaxWidth(),textAlign=TextAlign.Center,fontSize=12.sp)};items(q.ayahs,key={it.number}){a->Card(Modifier.fillMaxWidth().padding(vertical=6.dp),shape=RoundedCornerShape(23.dp)){Column(Modifier.padding(18.dp)){Text(a.arabic,Modifier.fillMaxWidth(),fontSize=(27*q.fontScale).sp,lineHeight=(50*q.fontScale).sp,textAlign=TextAlign.Right,fontFamily=FontFamily.Serif);HorizontalDivider(Modifier.padding(vertical=12.dp));if(a.translation.isNotBlank())Text(a.translation,fontSize=(17*q.fontScale).sp,lineHeight=(29*q.fontScale).sp);Text("﴿ ${a.number} ﴾",Modifier.fillMaxWidth(),textAlign=TextAlign.End,color=E,fontWeight=FontWeight.Bold)}}};item{Text(UiTexts.of(q.language.code).source+": "+q.language.source,Modifier.padding(12.dp),fontSize=10.sp,textAlign=TextAlign.Center)}}}}
}

@Composable private fun SettingsDialog(q:QuranViewModel,p:PrayerViewModel,close:()->Unit){
 var lang by remember{mutableStateOf(false)};var city by remember{mutableStateOf(false)};var method by remember{mutableStateOf(false)}
 AlertDialog(onDismissRequest=close,title={Text("Танзимот",fontWeight=FontWeight.ExtraBold)},text={Column(Modifier.verticalScroll(rememberScrollState())){
  SettingRow("🌐","Забони барнома",q.language.nativeName){lang=true};SettingRow("📍","Шаҳр",p.location?.city?:"Интихоб кунед"){city=true};SettingRow("🧭","Методи намоз",methodName(p.prayerMethod)){method=true};SettingRow("🕌","Мазҳаб",if(p.madhhab==1)"Ҳанафӣ" else "Шофеъӣ"){p.selectMadhhab(if(p.madhhab==1)0 else 1)};SettingRow("🌙","Экран",if(q.dark)"Dark" else "Light"){q.dark=!q.dark};SettingRow("A","Матн",String.format("%.2fx",q.fontScale)){q.fontScale=if(q.fontScale>=1.5f)1f else q.fontScale+.25f};Spacer(Modifier.height(8.dp));Text("JABORZODA FAYZALI",fontWeight=FontWeight.Bold,color=G);Text("QURAN — القرآن الكريم • 114 сура • намоз • қибла • дуо",fontSize=11.sp)
 }},confirmButton={TextButton(close){Text("Готово")}})
 if(lang)LanguageDialog(q){lang=false};if(city)CityDialog(p){city=false};if(method)MethodDialog(p){method=false}
}
@Composable private fun LanguageDialog(q:QuranViewModel,close:()->Unit){AlertDialog(onDismissRequest=close,title={Text("Забони барнома")},text={LazyColumn(Modifier.heightIn(max=450.dp)){items(QuranLanguages.all()){x->Row(Modifier.fillMaxWidth().clickable{if(x.translationKey!=null||x.code=="ar"){q.selectLanguage(x);close()}}.padding(vertical=10.dp)){Column(Modifier.weight(1f)){Text(x.nativeName,fontWeight=FontWeight.SemiBold);Text(x.englishName,fontSize=11.sp)};if(x.code==q.language.code)Text("✓",color=E)}}}},confirmButton={TextButton(close){Text("Назад")}})}
@Composable private fun CityDialog(p:PrayerViewModel,close:()->Unit){AlertDialog(onDismissRequest=close,title={Text("Шаҳрро интихоб кунед")},text={LazyColumn(Modifier.heightIn(max=470.dp)){items(cities){x->Row(Modifier.fillMaxWidth().clickable{p.useCity(x);close()}.padding(vertical=10.dp)){Column{Text(x.city,fontWeight=FontWeight.SemiBold);Text(x.country,fontSize=11.sp)}}}}},confirmButton={TextButton(close){Text("Назад")}})}
@Composable private fun MethodDialog(p:PrayerViewModel,close:()->Unit){
 val xs=listOf(0 to "Jafari / Shia",1 to "Karachi",2 to "ISNA",3 to "Muslim World League",4 to "Umm Al-Qura",5 to "Egypt",7 to "Tehran",8 to "Gulf",9 to "Kuwait",10 to "Qatar",11 to "Singapore",12 to "France",13 to "Diyanet Turkey",14 to "Russia",15 to "Moonsighting",16 to "Dubai",17 to "Malaysia",18 to "Tunisia",19 to "Algeria",20 to "Indonesia",21 to "Morocco",22 to "Lisbon",23 to "Jordan")
 AlertDialog(onDismissRequest=close,title={Text("Методи ҳисоб")},text={LazyColumn(Modifier.heightIn(max=470.dp)){items(xs){x->Row(Modifier.fillMaxWidth().clickable{p.selectMethod(x.first);close()}.padding(vertical=9.dp)){Text(x.second,Modifier.weight(1f));if(p.prayerMethod==x.first)Text("✓",color=E)}}}},confirmButton={TextButton(close){Text("Назад")}})
}
@Composable private fun SettingRow(i:String,t:String,v:String,click:()->Unit)=Row(Modifier.fillMaxWidth().clickable{click()}.padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically){Text(i,fontSize=20.sp,modifier=Modifier.width(36.dp));Column(Modifier.weight(1f)){Text(t,fontWeight=FontWeight.SemiBold);Text(v,fontSize=11.sp,color=MaterialTheme.colorScheme.onSurface.copy(.55f))};Text("›",fontSize=22.sp,color=E)}
