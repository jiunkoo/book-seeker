package com.example.bookseeker.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.bookseeker.presenter.RatingPresenter
import com.example.bookseeker.view.fragment.ComicFragment
import com.example.bookseeker.view.fragment.FantasyFragment
import com.example.bookseeker.view.fragment.RomanceFragment

class RatingTabAdapter(ratingPresenter: RatingPresenter, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var ratingPresenter: RatingPresenter = ratingPresenter
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> { return ComicFragment(ratingPresenter) }
            1 -> { return RomanceFragment(ratingPresenter) }
            2 -> { return FantasyFragment(ratingPresenter) }
            else -> { return null }
        }
    }

    // 생성 할 Fragment 의 개수
    override fun getCount() = 3

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> { return "만화" }
            1 -> { return "로맨스" }
            2 -> { return "판타지" }
            else -> return null
        }
    }

}