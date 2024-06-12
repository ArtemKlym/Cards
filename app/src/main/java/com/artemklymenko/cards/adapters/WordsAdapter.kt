package com.artemklymenko.cards.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.artemklymenko.cards.databinding.ItemWordsBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.view.activities.UpdateWordsActivity
import javax.inject.Inject

class WordsAdapter @Inject constructor():
    RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

        private lateinit var binding: ItemWordsBinding
        private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        binding = ItemWordsBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class ViewHolder: RecyclerView.ViewHolder(binding.root){
        fun bind(words: Words){
            binding.apply {
                tvOrigin.text = words.origin
                tvTranslated.text = words.translated
                root.setOnClickListener {
                    val intent = Intent(context, UpdateWordsActivity::class.java)
                    intent.putExtra("wordsId", words.wordsId)
                    intent.putExtra("sid", words.sid)
                    intent.putExtra("sourceCode", words.sourceLangCode)
                    intent.putExtra("targetCode", words.targetLangCode)
                    context.startActivity(intent)
                }
            }
        }
    }

    private val differCallBack = object:
    DiffUtil.ItemCallback<Words>(){
        override fun areItemsTheSame(oldItem: Words, newItem: Words): Boolean {
            return oldItem.wordsId == newItem.wordsId
        }

        override fun areContentsTheSame(oldItem: Words, newItem: Words): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)
}