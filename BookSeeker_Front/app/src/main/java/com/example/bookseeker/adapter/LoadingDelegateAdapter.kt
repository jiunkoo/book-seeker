package com.example.bookseeker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R


class LoadingDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_loading, parent, false)
        return LoadingDelegateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, position: Int) {}

    class LoadingDelegateViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
}