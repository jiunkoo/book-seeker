//package com.example.bookseeker.adapter
//
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.bookseeker.R
//import com.example.bookseeker.model.data.BookData
//import kotlinx.android.synthetic.main.item_recv_recommend.view.*
//
//
//class RecommendDelegateAdapter(val viewActions: onViewSelectedListener) : ViewTypeDelegateAdapter {
//    interface onViewSelectedListener {
//        fun onItemSelected(bookData: BookData, position: Int)
////        fun onItemLongSelected(view: View, bookData: BookData)
//        fun boringIbtnSelected(bookData: BookData, position: Int)
//        fun interestingIbtnSelected(bookData: BookData, position: Int)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_recommend, parent, false)
//        return RecommendDelegateViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, position: Int) {
//        holder as RecommendDelegateViewHolder
//        holder.bind(item as BookData, position)
//    }
//
//    inner class RecommendDelegateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        fun bind(bookData: BookData, position: Int) = with(itemView) {
//            var splitUrl = bookData.cover.split("/")
//            var coverUrl: String = "https://img.ridicdn.net/cover/" + splitUrl[4] + "/large"
//
//            Glide.with(itemView.context).load(coverUrl).into(recv_recommend_item_imgv_image)
//            recv_recommend_item_txtv_title.text = bookData.title
//            recv_recommend_item_txtv_author.text = bookData.author
//            recv_recommend_item_txtv_publisher.text = bookData.publisher
//            recv_recommend_item_txtv_rating.text = bookData.rating.toString()
//
//            when(bookData.state){
//                // 아무것도 아닌 경우
//                -2 -> {
//                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#ffffff"))
//                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting_border)
//                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#ffffff"))
//                }
//                // '관심 없어요'인 경우
//                0 -> {
//                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#e02947"))
//                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting_border)
//                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#ffffff"))
//                }
//                // '관심 있어요'인 경우
//                1 -> {
//                    recv_recommend_item_ibtn_boring.setColorFilter(Color.parseColor("#ffffff"))
//                    recv_recommend_item_ibtn_interesting.setImageResource(R.drawable.icon_interesting)
//                    recv_recommend_item_ibtn_interesting.setColorFilter(Color.parseColor("#e02947"))
//                }
//            }
//
//            super.itemView.setOnClickListener { viewActions.onItemSelected(bookData, position) }
////            super.itemView.setOnLongClickListener { view ->
////                viewActions.onItemLongSelected(view, bookData)
////                true
////            }
//            super.itemView.recv_recommend_item_ibtn_boring.setOnClickListener{
//                viewActions.boringIbtnSelected(bookData, position)
//            }
//            super.itemView.recv_recommend_item_ibtn_interesting.setOnClickListener{
//                viewActions.interestingIbtnSelected(bookData, position)
//            }
//        }
//    }
//}