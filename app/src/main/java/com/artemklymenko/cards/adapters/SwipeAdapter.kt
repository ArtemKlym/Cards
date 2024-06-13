package com.artemklymenko.cards.adapters


import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.artemklymenko.cards.R
import com.artemklymenko.cards.db.Words
import com.wajahatkarim3.easyflipview.EasyFlipView
import java.util.Locale

class SwipeAdapter(
    list: List<Words>
) : BaseAdapter(), TextToSpeech.OnInitListener {

    private var listWords: ArrayList<Words> = ArrayList()

    private var tts: TextToSpeech? = null
    init {
        listWords.addAll(list)
    }

    private lateinit var frontText: TextView
    private lateinit var backText: TextView
    private lateinit var btnListen: ImageButton
    private lateinit var flipView: EasyFlipView

    private var origin = true

    override fun getCount(): Int = listWords.size

    override fun getItem(position: Int): Any = listWords[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val initConvertView: View = convertView ?:
        LayoutInflater.from(parent!!.context).inflate(R.layout.card_items, parent, false)
        val currentWord = listWords[position]

        initView(initConvertView)

        frontText.text = currentWord.origin
        backText.text = currentWord.translated

        tts = TextToSpeech(parent!!.context,this)

        flipView.setOnFlipListener { easyFlipView, _ ->
            origin = easyFlipView.isFrontSide
        }

        btnListen.setOnClickListener {
            if(origin){
                speakOut(currentWord.origin, currentWord.sourceLangCode)
            }else{
                speakOut(currentWord.translated, currentWord.targetLangCode)
            }
            origin = true
        }

        return initConvertView
    }

    private fun speakOut(text: String, languageCode: String) {
        val locale = getLocale(languageCode)
        val result = tts!!.setLanguage(locale)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language $languageCode is not supported!")
        } else {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    private fun initView(initConvertView: View) {
        frontText = initConvertView.findViewById(R.id.tvCardOriginal)
        backText = initConvertView.findViewById(R.id.tvCardTranslated)
        btnListen = initConvertView.findViewById(R.id.ibListen)
        flipView = initConvertView.findViewById(R.id.easyFlipView)
    }

    fun addSwiped(words: Words) {
        listWords.add(words)
        notifyDataSetChanged()
    }

    override fun onInit(status: Int) {}

    private fun getLocale(languageCode: String): Locale {
        return when (languageCode) {
            "af" -> Locale("af")
            "ar" -> Locale("ar")
            "be" -> Locale("be")
            "bg" -> Locale("bg")
            "bn" -> Locale("bn")
            "ca" -> Locale("ca")
            "cs" -> Locale("cs")
            "cy" -> Locale("cy")
            "da" -> Locale("da")
            "de" -> Locale.GERMANY
            "el" -> Locale("el")
            "en" -> Locale.ENGLISH
            "eo" -> Locale("eo")
            "es" -> Locale("es", "ES")
            "et" -> Locale("et")
            "fa" -> Locale("fa")
            "fi" -> Locale("fi")
            "fr" -> Locale.FRANCE
            "ga" -> Locale("ga")
            "gl" -> Locale("gl")
            "gu" -> Locale("gu")
            "he" -> Locale("he")
            "hi" -> Locale("hi")
            "hr" -> Locale("hr")
            "ht" -> Locale("ht")
            "hu" -> Locale("hu")
            "id" -> Locale("id")
            "is" -> Locale("is")
            "it" -> Locale.ITALY
            "ja" -> Locale.JAPAN
            "ka" -> Locale("ka")
            "kn" -> Locale("kn")
            "ko" -> Locale.KOREA
            "lt" -> Locale("lt")
            "lv" -> Locale("lv")
            "mk" -> Locale("mk")
            "mr" -> Locale("mr")
            "ms" -> Locale("ms")
            "mt" -> Locale("mt")
            "nl" -> Locale("nl")
            "no" -> Locale("no")
            "pl" -> Locale("pl")
            "pt" -> Locale("pt")
            "ro" -> Locale("ro")
            "ru" -> Locale("ru")
            "sk" -> Locale("sk")
            "sl" -> Locale("sl")
            "sq" -> Locale("sq")
            "sv" -> Locale("sv")
            "sw" -> Locale("sw")
            "ta" -> Locale("ta")
            "te" -> Locale("te")
            "th" -> Locale("th")
            "tl" -> Locale("tl")
            "tr" -> Locale("tr")
            "uk" -> Locale("uk")
            "ur" -> Locale("ur")
            "vi" -> Locale("vi")
            "zh" -> Locale.CHINA
            else -> Locale.ENGLISH
        }
    }
}