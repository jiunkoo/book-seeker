package com.example.bookseeker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.BookData
import kotlinx.android.synthetic.main.item_recv_myevaluation.view.*


class MyEvaluationDelegateAdapter(val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {
    interface onViewSelectedListener {
        fun onItemSelected(bookData: BookData)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_myevaluation, parent, false)
        return MyEvaluationDelegateAdapter(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, position: Int) {
        holder as MyEvaluationDelegateAdapter
        holder.bind(item as BookData, position)
    }

    inner class MyEvaluationDelegateAdapter(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(bookData: BookData, position: Int) = with(itemView) {
            var splitUrl = bookData.cover.split("/")
            var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xlarge"

            Glide.with(itemView.context).load(coverUrl).into(recv_myevaluation_item_imgv_book)
            recv_myevaluation_item_txtv_title.text = bookData.title
            if(bookData.rating < 0){
                recv_myevaluation_item_txtv_rating.text = "★ 0.0"
            } else {
                recv_myevaluation_item_txtv_rating.text = "★" + bookData.rating.toString()
            }

            super.itemView.setOnClickListener { viewActions.onItemSelected(bookData) }
        }
    }
}