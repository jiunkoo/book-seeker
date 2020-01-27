package com.example.bookseeker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookseeker.R
import com.example.bookseeker.presenter.SearchDetailPresenter

class SearchWordFragment(searchDetailPresenter: SearchDetailPresenter) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        val rootView = inflater.inflate(R.layout.fragment_searchword, container, false)

        return rootView
    }
}