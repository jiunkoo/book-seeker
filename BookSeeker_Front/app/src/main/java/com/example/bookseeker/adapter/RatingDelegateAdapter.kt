package com.example.bookseeker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.BookData
import kotlinx.android.synthetic.main.item_recv_rating.view.*

class RatingDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_rating, parent, false)
        return RatingDelegateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as RatingDelegateViewHolder
        holder.bind(item as BookData)
    }

    inner class RatingDelegateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(bookData: BookData) = with(itemView) {
            Glide.with(itemView.context).load(bookData.image).into(recv_rating_item_imgv_book)
            recv_rating_item_txtv_booktitle.text = bookData.title
            recv_rating_item_txtv_author.text = bookData.author
            recv_rating_item_txtv_publisher.text = bookData.publisher
            recv_rating_item_ratingbar_bookrating.rating = 0.0f

            // 각각의 Item에 onClickListener 설정
            itemView.setOnClickListener({
                Toast.makeText(itemView.context, "아이템 '${bookData.title}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
            })
        }
    }
}