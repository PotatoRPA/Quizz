package com.example.templatequiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.templatequiz.databinding.ItemResultStatBinding
import com.example.templatequiz.models.ResultStat

class ResultStatsAdapter(
    private val items: List<ResultStat>
) : RecyclerView.Adapter<ResultStatsAdapter.StatViewHolder>() {

    inner class StatViewHolder(
        private val binding: ItemResultStatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ResultStat) = with(binding) {
            statIcon.setImageResource(item.iconRes)
            statTitle.text = item.title
            statSubtitle.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemResultStatBinding.inflate(inflater, parent, false)
        return StatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}