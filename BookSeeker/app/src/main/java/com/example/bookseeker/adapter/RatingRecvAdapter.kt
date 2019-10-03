package com.example.bookseeker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.adapter.contract.RatingAdapterContract
import com.example.bookseeker.model.data.BookData

class RatingRecvAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    RatingAdapterContract.View, RatingAdapterContract.Model {
    override lateinit var loginUserEmail: String
    private var VIEW_TYPE_ITEM = 0
    private var VIEW_TYPE_LOADING = 1
    private var bookRatingRecvList = ArrayList<BookData>()

    // onCreateViewHolder : ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_rating, parent, false)
            return RatingViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recv_loading, parent, false)
            return LoadingViewHolder(v)
        }
    }

    // onBindViewHolder : 데이터를 arraylist에 binding하는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RatingViewHolder) {
            val bookData = bookRatingRecvList[position]
            holder.bindRating(bookData)
        } else if (holder is LoadingViewHolder) {
            holder.bindLoading(holder, position)
        }
//        val bookData = bookRatingRecvList[position]
//        holder.onBind(bookData)
    }

    inner class RatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var image: ImageView
        internal var title: TextView
        internal var author: TextView
        internal var publisher: TextView
        internal var rating: RatingBar

        init {
            image = itemView.findViewById(R.id.recv_rating_item_imgv_book)
            title = itemView.findViewById(R.id.recv_rating_item_txtv_booktitle)
            author = itemView.findViewById(R.id.recv_rating_item_txtv_author)
            publisher = itemView.findViewById(R.id.recv_rating_item_txtv_publisher)
            rating = itemView.findViewById(R.id.recv_rating_item_ratingbar_bookrating)
        }

        fun bindRating(bookData: BookData) {
//            Glide.with(itemView.context).load(data.photo).into(itemView.imageView_photo)
            Glide.with(itemView.context).load(bookData.image).into(image)
            title.text = bookData.title
            author.text = bookData.author
            publisher.text = bookData.publisher
            rating.rating = 0.0f

            // 각각의 Item에 onClickListener 설정
            itemView.setOnClickListener({
                Toast.makeText(itemView.context, "아이템 '${title}'를 클릭했습니다.", Toast.LENGTH_LONG).show()
            })
        }
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }

        fun bindLoading(holder: LoadingViewHolder, position: Int) {
            //ProgressBar would be displayed
        }
    }

    // getItemCount : RecyclerView의 Item 개수를 반환하는 함수
    override fun getItemCount() = bookRatingRecvList.size

    // notifyDataChange : 데이터 변경을 알리는 함수
    override fun notifyDataChange() {
        notifyDataSetChanged()
    }

//    // addBookData : 도서 데이터를 추가하는 함수
//    override fun addBookData(bookData: ArrayList<BookData>) {
//        // 초기 위치 제거 및 알리기
//        val initPosition = bookRatingRecvList.size - 1
//        bookRatingRecvList.removeAt(initPosition)
//        // 특정 Position 데이터 제거할 때 이벤트 알림
//        notifyItemRemoved(initPosition)
//
//        // 모든 목록 추가하고 마지막은 로딩용 아이템 추가
//        bookRatingRecvList.addAll(bookData)
//        bookRatingRecvList.add(null)
//        this.bookRatingRecvList = bookData
//    }
//
//    // getOneBookData : 하나의 도서 정보를 가져오는 함수
//    override fun getOneBookData(position: Int): BookData = bookRatingRecvList.get(position)
//
//    // clearAllBookData : 모든 도서 정보를 삭제하는 함수
//    override fun clearAllBookData() {
//        bookRatingRecvList.clear()
//    }
}