package com.example.bookseeker.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.item.RatingRecvItem
import kotlinx.android.synthetic.main.item_recv_rating.view.*

class RatingRecvAdapter(val bookRatingRecvList: ArrayList<RatingRecvItem>): RecyclerView.Adapter<RatingRecvAdapter.ViewHolder>() {
    // onCreateViewHolder : ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingRecvAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_rating, parent, false)
        return ViewHolder(v)
    }

    // onBindViewHolder : 데이터를 arraylist에 binding하는 함수
    override fun onBindViewHolder(holder: RatingRecvAdapter.ViewHolder, position: Int) {
        holder.bindItems(bookRatingRecvList[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : RatingRecvItem){
//            //이미지표시
//            Glide.with(itemView.context).load(data.photo)
//                .into(itemView.imageView_photo)
//            //itemView.imageView_photo.setImageBitmap(data.photo)
            itemView.recv_rating_item_txtv_booktitle.text = data.bookTitle
            itemView.recv_rating_item_txtv_author.text = data.author
            itemView.recv_rating_item_txtv_publisher.text = data.publisher
            itemView.recv_rating_item_ratingbar_bookrating.rating = data.bookRating

            // 각각의 Item에 onClickListener 설정
            itemView.setOnClickListener({
                Toast.makeText(itemView.context, "아이템 '${data.bookTitle}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
            })
        }
    }

    // getItemCount : RecyclerView의 Item 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return bookRatingRecvList.size
    }
}


