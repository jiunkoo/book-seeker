package com.example.bookseeker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.R
import com.example.bookseeker.adapter.SearchAdapter
import com.example.bookseeker.adapter.listener.InfiniteScrollListener
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.presenter.SearchDetailPresenter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import com.example.bookseeker.adapter.SearchDelegateAdapter
import com.example.bookseeker.model.data.BookData

class SearchResultFragment(searchDetailPresenter: SearchDetailPresenter, searchWord: String)
    : Fragment(), SearchDelegateAdapter.onViewSelectedListener {
    // 부모에게서 전달받은 변수를 초기화
    private var searchDetailPresenter: SearchDetailPresenter = searchDetailPresenter
    private var searchWord: String = searchWord
    //recyclerview를 담을 빈 데이터 리스트 변수를 초기화
    private var searchResultBookList: BookList? = null
    // Disposable 객체 지정
    private var subscriptions = CompositeDisposable()
    // RecyclerView Adapter 설정
    private val searchAdapter by lazy { SearchAdapter(this) }
    // Spinner Item Change Flag 설정
    private var category = 0
    private var spinnerFlag = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_searchresult, container, false)
        val recyclerView = rootView.findViewById(R.id.searchresult_recyclerview) as RecyclerView
        val spinner = rootView.findViewById(R.id.searchresult_spinner) as Spinner

        // spinner 설정
        setSpinner(spinner, recyclerView, savedInstanceState)

        return rootView
    }

    // setSpinner : SearchResultFragment에서 평가할 도서 목록에 대한 Spinner를 초기화 및 정의하는 함수
    fun setSpinner(spinner: Spinner, recyclerView: RecyclerView, savedInstanceState: Bundle?){
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 이전 카테고리와 현재 포지션과 다른 경우 변경
                if(category != position) {
                    category = position
                    spinnerFlag = true
                    searchResultBookList?.page = null
                }
                else{
                    spinnerFlag = false
                }
                setRecyclerView(recyclerView, savedInstanceState)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onItemSelected(bookData: BookData) {
        // 아이템을 선택하면 해당 데이터를 가지고 BookInfoFragment로 이동하게 함
        val bookInfoFragment = BookInfoFragment(searchDetailPresenter, bookData)
        searchDetailPresenter.replaceFragment(bookInfoFragment)
    }

    // setRecyclerView : ComicFragment에서 평가할 도서 목록에 대한 RecyclerView를 초기화 및 정의하는 함수
    fun setRecyclerView(recyclerView: RecyclerView, savedInstanceState: Bundle?){
        // Layout Manager 설정
        recyclerView.setHasFixedSize(true)
        val linearLayout = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayout

        recyclerView.clearOnScrollListeners() // 아이템 끝까지 도달되었을 때 클리어
        recyclerView.addOnScrollListener(InfiniteScrollListener({requestBookData(recyclerView)}, linearLayout)) // 다시 갱신

        // Adapter 설정
        if (recyclerView.adapter == null) {
            recyclerView.adapter = searchAdapter
        }

        if (savedInstanceState == null) {
            requestBookData(recyclerView)
        }
    }

    // requestBookData : 관찰자에게서 발행된 데이터를 가져오는 함수
    private fun requestBookData(recyclerView: RecyclerView){
        val subscription = searchDetailPresenter.getAllSearchResultBookList(category, searchResultBookList?.page ?: 1, 10, searchWord)
            .subscribeOn(Schedulers.io()).subscribe(
                { retrivedBookList ->
                    searchResultBookList = retrivedBookList
                    if(spinnerFlag == true){
                        (recyclerView.adapter as SearchAdapter).clearAndAddBookList(retrivedBookList.results)
                        spinnerFlag = false
                    }else{
                        (recyclerView.adapter as SearchAdapter).addBookList(retrivedBookList.results)
                    }
                },
                { e ->
                    Snackbar.make(recyclerView, e.message ?: "", Snackbar.LENGTH_LONG).show()
                }
            )
        subscriptions.add(subscription)
    }
}