package com.artemklymenko.cards.adaptar


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

        btnListen.setOnClickListener {
            speakOut(currentWord.origin)
        }

        return initConvertView
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun initView(initConvertView: View) {
        frontText = initConvertView.findViewById(R.id.tvCardOriginal)
        backText = initConvertView.findViewById(R.id.tvCardTranslated)
        btnListen = initConvertView.findViewById(R.id.ibListen)
    }

    fun addSwiped(words: Words) {
        listWords.add(words)
        notifyDataSetChanged()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                btnListen.isEnabled = true
            }
        }
    }
}