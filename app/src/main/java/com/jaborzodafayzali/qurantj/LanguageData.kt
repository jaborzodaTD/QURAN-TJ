package com.jaborzodafayzali.qurantj

import android.content.Context
import java.util.Locale

data class QuranLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val translationKey: String?,
    val rtl: Boolean = false,
    val source: String
)

object QuranLanguages {
    // Only translations with a known, attributable source are enabled.
    // Languages without a verified redistribution source stay visible as planned
    // options instead of silently substituting another language.
    val supported = listOf(
        QuranLanguage("tg","Тоҷикӣ","Tajik","tajik_arifi",source="QuranEnc • key resolved from API"),
        QuranLanguage("ru","Русский","Russian","russian_abu_adel",source="QuranEnc • key resolved from API"),
        QuranLanguage("kk","Қазақша","Kazakh","kazakh_altai",source="QuranEnc • key resolved from API"),
        QuranLanguage("uz","O‘zbekcha","Uzbek","uzbek_rwwad",source="QuranEnc • key resolved from API"),
        QuranLanguage("ky","Кыргызча","Kyrgyz","kyrgyz_hakimov",source="QuranEnc • key resolved from API"),
        QuranLanguage("tr","Türkçe","Turkish","turkish_rwwad",source="QuranEnc / Rowwad Translation Center"),
        QuranLanguage("az","Azərbaycanca","Azerbaijani","azeri_musayev",source="QuranEnc • key resolved from API"),
        QuranLanguage("ka","ქართული","Georgian","georgian_rwwad",source="QuranEnc / Rowwad Translation Center"),
        QuranLanguage("en","English","English","english_rwwad",source="QuranEnc / Rowwad Translation Center"),
        QuranLanguage("de","Deutsch","German","german_rwwad",source="QuranEnc / Rowwad Translation Center"),
        QuranLanguage("fr","Français","French","french_rashid",source="QuranEnc • key resolved from API"),
        QuranLanguage("es","Español","Spanish","spanish_garcia",source="QuranEnc • key resolved from API"),
        QuranLanguage("id","Bahasa Indonesia","Indonesian","indonesian_affairs",source="QuranEnc • key resolved from API"),
        QuranLanguage("ar","العربية","Arabic",null,rtl=true,source="Arabic Quran text; no translation selected")
    )

    val planned = listOf(
        QuranLanguage("tk","Türkmençe","Turkmen",null,source="Translation source/licence verification required"),
        QuranLanguage("fa","فارسی","Persian",null,rtl=true,source="Translation source/licence verification required"),
        QuranLanguage("ur","اردو","Urdu",null,rtl=true,source="Translation source/licence verification required"),
        QuranLanguage("ps","پښتو","Pashto",null,rtl=true,source="Translation source/licence verification required")
    )

    fun all() = supported + planned

    fun default(context: Context): QuranLanguage {
        val saved = context.getSharedPreferences("quran_settings", Context.MODE_PRIVATE)
            .getString("language", null)
        return all().firstOrNull { it.code == saved }
            ?: all().firstOrNull { it.code == Locale.getDefault().language }
            ?: supported.first()
    }

    fun save(context: Context, language: QuranLanguage) {
        context.getSharedPreferences("quran_settings", Context.MODE_PRIVATE)
            .edit().putString("language", language.code).apply()
    }
}

data class UiText(
    val home: String,
    val quran: String,
    val qibla: String,
    val settings: String,
    val prayerTimes: String,
    val search: String,
    val location: String,
    val chooseLanguage: String,
    val translation: String,
    val source: String,
    val back: String
)

object UiTexts {
    private val map = mapOf(
        "tg" to UiText("Асосӣ","Қуръон","Қибла","Танзимот","Вақтҳои намоз","Ҷустуҷӯ","Ҷойгиршавӣ","Забони барнома","Тарҷума","Манбаъ","Бозгашт"),
        "ru" to UiText("Главная","Коран","Кыбла","Настройки","Время намаза","Поиск","Местоположение","Язык приложения","Перевод","Источник","Назад"),
        "kk" to UiText("Басты бет","Құран","Құбыла","Баптаулар","Намаз уақыттары","Іздеу","Орналасу","Қолданба тілі","Аударма","Дереккөз","Артқа"),
        "uz" to UiText("Bosh sahifa","Qur'on","Qibla","Sozlamalar","Namoz vaqtlari","Qidirish","Joylashuv","Ilova tili","Tarjima","Manba","Orqaga"),
        "ky" to UiText("Башкы бет","Куран","Кыбыла","Жөндөөлөр","Намаз убакыттары","Издөө","Жайгашкан жер","Колдонмо тили","Котормо","Булак","Артка"),
        "tr" to UiText("Ana Sayfa","Kur'an","Kıble","Ayarlar","Namaz vakitleri","Ara","Konum","Uygulama dili","Çeviri","Kaynak","Geri"),
        "az" to UiText("Əsas","Quran","Qiblə","Parametrlər","Namaz vaxtları","Axtarış","Məkan","Tətbiq dili","Tərcümə","Mənbə","Geri"),
        "ka" to UiText("მთავარი","ყურანი","ქიბლა","პარამეტრები","ლოცვის დროები","ძებნა","მდებარეობა","აპის ენა","თარგმანი","წყარო","უკან"),
        "en" to UiText("Home","Quran","Qibla","Settings","Prayer times","Search","Location","App language","Translation","Source","Back"),
        "de" to UiText("Start","Koran","Qibla","Einstellungen","Gebetszeiten","Suche","Standort","App-Sprache","Übersetzung","Quelle","Zurück"),
        "fr" to UiText("Accueil","Coran","Qibla","Réglages","Heures de prière","Recherche","Localisation","Langue de l’app","Traduction","Source","Retour"),
        "es" to UiText("Inicio","Corán","Qibla","Ajustes","Horarios de oración","Buscar","Ubicación","Idioma de la app","Traducción","Fuente","Atrás"),
        "id" to UiText("Beranda","Al-Qur'an","Kiblat","Pengaturan","Waktu salat","Cari","Lokasi","Bahasa aplikasi","Terjemahan","Sumber","Kembali"),
        "ar" to UiText("الرئيسية","القرآن","القبلة","الإعدادات","أوقات الصلاة","بحث","الموقع","لغة التطبيق","الترجمة","المصدر","رجوع")
    )
    fun of(code: String): UiText = map[code] ?: map.getValue("en")
}
