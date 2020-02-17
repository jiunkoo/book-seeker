package com.example.bookseeker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.BookData
import kotlinx.android.synthetic.main.item_recv_searching.view.*


class SearchDelegateAdapter(val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {
    interface onViewSelectedListener {
        fun onItemSelected(bookData: BookData)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_searching, parent, false)
        return RatingDelegateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as RatingDelegateViewHolder
        holder.bind(item as BookData)
    }

    inner class RatingDelegateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(bookData: BookData) = with(itemView) {
            var splitUrl = bookData.cover.split("/")
            var coverUrl:String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xlarge"

            Glide.with(itemView.context).load(coverUrl).into(recv_searching_item_imgv_book)
            recv_searching_item_txtv_booktitle.text = bookData.title
            recv_searching_item_txtv_author.text = bookData.author
            recv_searching_item_txtv_publisher.text = bookData.publisher
            recv_searching_item_txtv_introduction.text = bookData.introduction

            super.itemView.setOnClickListener { viewActions.onItemSelected(bookData)}
        }
    }
}