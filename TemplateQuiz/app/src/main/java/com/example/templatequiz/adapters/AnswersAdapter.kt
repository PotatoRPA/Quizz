package com.example.templatequiz.adapters

import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.templatequiz.R
import com.example.templatequiz.additional.AnswerState
import com.example.templatequiz.databinding.ItemAnswerBubbleBinding
import com.example.templatequiz.models.AnswerItem

class AnswersAdapter(
    private val items: List<AnswerItem>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(
        private val binding: ItemAnswerBubbleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnswerItem) = with(binding) {
            answerNumberText.text = item.questionNumber.toString()
            answerNumberText.setResultState(item.answerState)
            root.setOnClickListener { onClick(item.questionNumber - 1) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnswerBubbleBinding.inflate(inflater, parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

fun TextView.setResultState(state: AnswerState) {
    when (state) {
        AnswerState.DEFAULT -> {
            setBackgroundResource(R.drawable.bg_answer_neutral)
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        AnswerState.CORRECT -> {
            setBackgroundResource(R.drawable.bg_answer_correct)
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        AnswerState.WRONG -> {
            setBackgroundResource(R.drawable.bg_answer_wrong)
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }
}