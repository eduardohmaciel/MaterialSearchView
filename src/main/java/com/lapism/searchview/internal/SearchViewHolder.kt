package com.lapism.searchview.internal

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lapism.searchview.R
import com.lapism.searchview.widget.MaterialSearchAdapter


class SearchViewHolder(itemView: View, listener: MaterialSearchAdapter.OnSearchItemClickListener?) :
    RecyclerView.ViewHolder(itemView) {

    val icon1: ImageView = itemView.findViewById(R.id.search_icon_1)
    val icon2: ImageView = itemView.findViewById(R.id.search_icon_2)
    val title: TextView = itemView.findViewById(R.id.search_title)
    val subtitle: TextView = itemView.findViewById(R.id.search_subtitle)

    init {
        itemView.setOnClickListener {
            listener?.onSearchItemClick(layoutPosition, title.text, subtitle.text)
        }
    }

}
