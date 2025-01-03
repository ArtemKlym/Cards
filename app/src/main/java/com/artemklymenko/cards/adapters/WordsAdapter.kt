package com.artemklymenko.cards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.artemklymenko.cards.databinding.ItemWordsBinding
import com.artemklymenko.cards.db.Words

class WordsAdapter (
    private val onItemClick: (Words) -> Unit
):
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
        fun bind(word: Words){
            binding.apply {
                tvOrigin.text = word.origin
                tvTranslated.text = word.translated
                itemView.setOnClickListener {
                    onItemClick(word)
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