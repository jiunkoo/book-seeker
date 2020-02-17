package com.example.bookseeker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.RatingAdapter
import com.example.bookseeker.adapter.listener.InfiniteScrollListener
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.presenter.RatingPresenter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RomanceFragment(ratingPresenter: RatingPresenter) : Fragment() {
    // 부모에게서 상속받을 RatingPresenter를 초기화
    private var ratingPresenter: RatingPresenter = ratingPresenter
    //recyclerview를 담을 빈 데이터 리스트 변수를 초기화
    private var romanceBookList: BookList? = null
    // Disposable 객체 지정
    var subscriptions = CompositeDisposable()
    // Spinner Item Change Flag 설정
    private var category = 0
    private var spinnerFlag = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_tabcontent, container, false)
        val recyclerView = rootView.findViewById(R.id.tabcontent_recyclerview) as RecyclerView
        val spinner = rootView.findViewById(R.id.tabcontent_spinner) as Spinner

        // spinner 설정
        setSpinner(spinner, recyclerView, savedInstanceState)

        return rootView
    }

    // setSpinner : RomanceFragment에서 평가할 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
    fun setSpinner(spinner: Spinner, recyclerView: RecyclerView, savedInstanceState: Bundle?){
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 이전 카테고리와 현재 포지션과 다른 경우 변경
                if(category != position) {
                    category = position
                    spinnerFlag = true
                }
                else{
                    spinnerFlag = false
                }
                setRecyclerView(recyclerView, savedInstanceState)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // setRecyclerView : RomanceFragment에서 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    fun setRecyclerView(recyclerView: RecyclerView, savedInstanceState: Bundle?){
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val linearLayout = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(InfiniteScrollListener({requestBookData(recyclerView)}, linearLayout)) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = RatingAdapter()
        }

        if (savedInstanceState == null) {
            requestBookData(recyclerView)
        }
    }

    // requestBookData : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun requestBookData(recyclerView: RecyclerView){
//        val subscription = ratingPresenter.getAllRomanceBookList(romanceBookList?.page ?: 1)
//            .subscribeOn(Schedulers.io()).subscribe(
//                { retrivedBookList ->
//                    romanceBookList = retrivedBookList
//                    if(spinnerFlag == true){
//                        (recyclerView.adapter as RatingAdapter).clearAndAddBookList(retrivedBookList.results)
//                        spinnerFlag = false
//                    }else{
//                        (recyclerView.adapter as RatingAdapter).addBookList(retrivedBookList.results)
//                    }
//                },
//                { e ->
//                    Snackbar.make(recyclerView, e.message ?: "", Snackbar.LENGTH_LONG).show()
//                }
//            )
//        subscriptions.add(subscription)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }
}