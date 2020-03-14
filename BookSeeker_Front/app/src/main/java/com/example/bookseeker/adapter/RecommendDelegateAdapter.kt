package com.example.bookseeker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.RecommendData
import kotlinx.android.synthetic.main.item_recv_recommend.view.*


class RecommendDelegateAdapter(val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {
    interface onViewSelectedListener {
        fun onItemSelected(recommendData: RecommendData, position: Int)
//        fun onItemLongSelected(view: View, bookData: BookData)
        fun boringIbtnSelected(recommendData: RecommendData, position: Int)
        fun interestingIbtnSelected(recommendData: RecommendData, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_recommend, parent, false)
        return RecommendDelegateViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, position: Int) {
        holder as RecommendDelegateViewHolder
        holder.bind(item as RecommendData, position)
    }

    inner class RecommendDelegateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(recommendData: RecommendData, position: Int) = with(itemView) {
            var splitUrl = recommendData.cover.split("/")
            var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/xlarge"

            Glide.with(itemView.context).load(coverUrl).into(recv_recommend_item_imgv_image)
            recv_recommend_item_txtv_title.text = recommendData.title
            recv_recommend_item_txtv_author.text = recommendData.author
            recv_recommend_item_txtv_publisher.text = recommendData.publisher
            recv_recommend_item_txtv_rating.text = recommendData.expect_rating.toString()

            when(recommendData.state){
                // 아무것도 아닌 경우
                -1 -> {
                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#ffffff"))
                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting_border)
                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#ffffff"))
                }

                // '관심 없어요'인 경우
                0 -> {
                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#e02947"))
                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting_border)
                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#ffffff"))
                }
                // '관심 있어요'인 경우
                1 -> {
                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#ffffff"))
                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting)
                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#e02947"))
                }
            }

            super.itemView.setOnClickListener { viewActions.onItemSelected(recommendData, position) }
//            super.itemView.setOnLongClickListener { view ->
//                viewActions.onItemLongSelected(view, bookData)
//                true
//            }
            super.itemView.recv_recommend_item_ibtn_boring.setOnClickListener{
                viewActions.boringIbtnSelected(recommendData, position)
            }
            super.itemView.recv_recommend_item_ibtn_interesting.setOnClickListener{
                viewActions.interestingIbtnSelected(recommendData, position)
            }
        }
    }
}