package com.artemklymenko.cards.adaptar


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.artemklymenko.cards.R
import com.artemklymenko.cards.db.Words

class SwipeAdapter(
    list: List<Words>
) : BaseAdapter() {

    private var listWords: ArrayList<Words> = ArrayList()
    init {
        listWords.addAll(list)
    }

    private lateinit var frontText: TextView
    private lateinit var backText: TextView

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

        return initConvertView
    }

    private fun initView(initConvertView: View) {
        frontText = initConvertView.findViewById(R.id.tvCardOriginal)
        backText = initConvertView.findViewById(R.id.tvCardTranslated)
    }

    fun addSwiped(words: Words) {
        listWords.add(words)
        notifyDataSetChanged()
    }
}