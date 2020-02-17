package com.example.bookseeker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookseeker.R
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.presenter.SearchDetailPresenter

class BookInfoFragment(searchDetailPresenter: SearchDetailPresenter, bookData: BookData) : Fragment() {
    // 부모에게서 상속받은 변수를 초기화
    private var searchDetailPresenter: SearchDetailPresenter = searchDetailPresenter
    private var bookData: BookData = bookData
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_bookinfo, container, false)

        setBookData(rootView)

        return rootView
    }

    fun setBookData(rootView: View){
        val image = rootView.findViewById(R.id.bookinfo_imgv_book) as ImageView
        val title = rootView.findViewById(R.id.bookinfo_txtv_booktitle) as TextView
        val author = rootView.findViewById(R.id.bookinfo_txtv_author) as TextView
        val publisher = rootView.findViewById(R.id.bookinfo_txtv_publisher) as TextView
        val date = rootView.findViewById(R.id.bookinfo_txtv_date) as TextView
        val introduction = rootView.findViewById(R.id.bookinfo_txtv_introduction) as TextView
        Glide.with(rootView.context).load(bookData.cover).into(image)
        title.text = bookData.title
        author.text = bookData.author
        publisher.text = bookData.publisher
        date.text = bookData.publication_date
        introduction.text = bookData.introduction
    }
}