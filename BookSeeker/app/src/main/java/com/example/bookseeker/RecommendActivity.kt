package com.example.bookseeker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecommendActivity : AppCompatActivity() {
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.btmnavmenu_itm_search -> {
                val nextIntent = Intent(baseContext, SearchActivity::class.java)
                startActivity(nextIntent)
                overridePendingTransition(0, 0)
            }
            R.id.btmnavmenu_itm_recommend -> {
                val nextIntent = Intent(baseContext, RecommendActivity::class.java)
                startActivity(nextIntent)
                overridePendingTransition(0, 0)
            }
            R.id.btmnavmenu_itm_rating -> {
                val nextIntent = Intent(baseContext, RatingActivity::class.java)
                startActivity(nextIntent)
                overridePendingTransition(0, 0)
            }
            R.id.btmnavmenu_itm_mypage -> {
                val nextIntent = Intent(baseContext, MypageActivity::class.java)
                startActivity(nextIntent)
                overridePendingTransition(0, 0)
            }
        }
        false
    }
    internal fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)

        val navView: BottomNavigationView = findViewById(R.id.recommend_btmnavview_menu)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        navView.checkItem(R.id.btmnavmenu_itm_recommend)
    }
}