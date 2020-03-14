package com.example.bookseeker.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookseeker.model.data.RecommendData

class RecommendAdapter(listener: RecommendDelegateAdapter.onViewSelectedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.BOOKS, RecommendDelegateAdapter(listener))
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

    fun addBookList(recommendData: List<RecommendData>) {
        // first remove loading and notify
        val initPosition = items.size - 1
        items.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        // insert book and the loading at the end of the list
        items.addAll(recommendData)
        items.add(loadingItem)
        notifyItemRangeChanged(initPosition, items.size + 1 /* plus loading item */)
    }

    fun modifyBookList(recommendData: RecommendData, position: Int) {
        items[position] = recommendData
        notifyItemChanged(position)
    }

    fun clearAndAddBookList(recommendData: List<RecommendData>) {
        items.clear()
        notifyItemRangeRemoved(0, getLastPosition())

        items.addAll(recommendData)
        items.add(loadingItem)
//        notifyItemRangeInserted(0, items.size)
        notifyItemRangeChanged(0, items.size + 1 /* plus loading item */)
    }

    fun getBookList(): List<RecommendData> =
        items.filter { it.getViewType() == AdapterConstants.BOOKS }.map { it as RecommendData }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex
}