package com.example.bookseeker.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.model.data.BookData

class MyEvaluationAdapter(listener: MyEvaluationDelegateAdapter.onViewSelectedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.BOOKS, MyEvaluationDelegateAdapter(listener))
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        items = ArrayList()
        items.add(loadingItem)
    }

    override fun getItemCount(): Int{
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder, items[position], position)
    }

    override fun getItemViewType(position: Int) = items[position].getViewType()

    fun addBookList(bookData: List<BookData>) {
        println("도서 목록을 추가하였습니다.")
        println("bookdata : $bookData")
        // first remove loading and notify
        val initPosition = items.size - 1
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        // insert book and the loading at the end of the list
        items.addAll(bookData)
        items.add(loadingItem)
        notifyItemRangeChanged(initPosition, items.size + 1 /* plus loading item */)
    }

    fun modifyBookList(bookData: BookData, position: Int) {
        items[position] = bookData
        println("수정된 아이템 : " + items[position])
    }

    fun clearAndAddBookList(bookData: List<BookData>) {
        println("도서 목록을 삭제하고 추가하였습니다.")
        items.clear()
        notifyItemRangeRemoved(0, getLastPosition())

        items.addAll(bookData)
        items.add(loadingItem)
//        notifyItemRangeInserted(0, items.size)
        notifyItemRangeChanged(0, items.size + 1 /* plus loading item */)
    }

    fun getBookList(): List<BookData> =
        items.filter { it.getViewType() == AdapterConstants.BOOKS }.map { it as BookData }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex
}