package de.westnordost.luftlinie.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleRecyclerViewAdapter(@LayoutRes val layoutId: Int) : RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(onCreateView(parent, viewType))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindView(holder.itemView, position)
    }

    open fun onCreateView(parent: ViewGroup, viewType: Int): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(layoutId, parent, false)
    }

    abstract fun onBindView(view: View, position: Int)
}