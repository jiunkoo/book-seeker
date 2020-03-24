package com.example.bookseeker.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.MyPreferenceContract
import com.example.bookseeker.presenter.MyPreferencePresenter
import kotlinx.android.synthetic.main.activity_mypreference.*
import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class MyPreferenceActivity : BaseActivity(), MyPreferenceContract.View {
    // MyPreferenceActivity와 함께 생성될 MyEvaluationPresenter를 지연 초기화
    private lateinit var myPreferencePresenter: MyPreferencePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypreference)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        myPreferencePresenter.takeView(this)

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // barchart 이벤트 처리
        setBarChart()

        // wordcloud 이벤트 처리
        setWordCloud()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        myPreferencePresenter = MyPreferencePresenter()
    }

    // switchBottomNavigationView : RatingActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        mypreference_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btmnavmenu_itm_search -> {
                    val nextIntent = Intent(baseContext, SearchActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_recommend -> {
                    val nextIntent = Intent(baseContext, RecommendActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_rating -> {
                    val nextIntent = Intent(baseContext, RatingActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                R.id.btmnavmenu_itm_mypage -> {
                    val nextIntent = Intent(baseContext, MyPageActivity::class.java)
                    nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(nextIntent)
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
            false
        }
        mypreference_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_mypage)?.setChecked(true)
    }

    fun setBarChart(){
        val xAxis = mypreference_chart_bar.xAxis
        xAxis.textSize = 12f
        xAxis.setLabelsToSkip(0)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        val leftYAxis = mypreference_chart_bar.axisLeft
        leftYAxis.textSize = 12f
        leftYAxis.setDrawAxisLine(false)
        leftYAxis.setDrawGridLines(false)

        val rightYAxis = mypreference_chart_bar.axisRight
        rightYAxis.setDrawAxisLine(false)
        rightYAxis.setDrawGridLines(false)
        rightYAxis.isEnabled = false

        val averageRating = ArrayList<BarEntry>()

        averageRating.add(BarEntry(10f, 0))
        averageRating.add(BarEntry(30f, 1))
        averageRating.add(BarEntry(25f, 2))
        averageRating.add(BarEntry(0f, 3))
        averageRating.add(BarEntry(40f, 4))
        averageRating.add(BarEntry(5f, 5))
        averageRating.add(BarEntry(15f, 6))
        averageRating.add(BarEntry(5f, 7))
        averageRating.add(BarEntry(20f, 8))
        averageRating.add(BarEntry(45f, 9))

        val year = ArrayList<String>()

        year.add("0.5")
        year.add("1.0")
        year.add("1.5")
        year.add("2.0")
        year.add("2.5")
        year.add("3.0")
        year.add("3.5")
        year.add("4.0")
        year.add("4.5")
        year.add("5.0")

        val barDataSet = BarDataSet(averageRating, "평가 개수")
        mypreference_chart_bar.animateY(2000)
        val data = BarData(year, barDataSet)
        barDataSet.color = Color.parseColor("#ffbe50")
        barDataSet.valueTextSize = 12f
        mypreference_chart_bar.data = data
    }

    fun setWordCloud() {
        val wordCloud = arrayOf("Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
            "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop", "Marshmallow")
//        var frequency_list = [{"text":"study","size":40},{"text":"motion","size":15},
//            {"text":"forces","size":10},{"text":"electricity","size":15}, {"text":"movement","size":10},
//            {"text":"relation","size":5}, {"text":"things","size":10},{"text":"force","size":5},
//            {"text":"ad","size":5},{"text":"energy","size":85}, {"text":"living","size":5}]

        var jsonArray = JsonArray()
        var random = Random()
        for(i in 0..30){
            var text = "text"+i
            var size = 10 + random.nextInt(35)
            var jsonObject = JsonObject()
            jsonObject.addProperty("text", text)
            jsonObject.addProperty("size", size)

            jsonArray.add(jsonObject)
        }

        val webSettings = mypreference_webv_wordcloud.settings
        webSettings.javaScriptEnabled = true
        mypreference_webv_wordcloud.loadUrl("file:///android_asset/d3.html")
        mypreference_webv_wordcloud.webViewClient = object : WebViewClient() {
            override fun onPageFinished(webView: WebView, url: String) {
                super.onPageFinished(webView, url)
                val stringBuffer = StringBuffer()
                stringBuffer.append("wordCloud(")
                stringBuffer.append(jsonArray.toString())
                stringBuffer.append(")")
                /*
                stringBuffer.append("wordCloud([")
                for (i in 0 until wordCloud.size) {
                    stringBuffer.append("'").append(wordCloud[i]).append("'")
                    if (i < wordCloud.size - 1) {
                        stringBuffer.append(",")
                    }
                }
                stringBuffer.append("])")
                */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mypreference_webv_wordcloud.evaluateJavascript(stringBuffer.toString(), null)
                } else {
                    mypreference_webv_wordcloud.loadUrl("javascript:$stringBuffer")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // View가 Delete(Unbind) 되었다는 걸 Presenter에 전달
        myPreferencePresenter.dropView()
    }

    // setProgressON :  공통으로 사용하는 Progress Bar의 시작을 정의하는 함수
    override fun setProgressON(msg: String) {
        progressON(msg)
    }

    // setProgressOFF() : 공통으로 사용하는 Progress Bar의 종료를 정의하는 함수
    override fun setProgressOFF() {
        progressOFF()
    }

    // showMessage : 공통으로 사용하는 messsage 출력 부분을 생성하는 함수
    override fun showMessage(msg: String) {
        Toast.makeText(this@MyPreferenceActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}