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
import com.github.mikephil.charting.components.XAxis
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MyPreferenceActivity : BaseActivity(), MyPreferenceContract.View {
    // MyPreferenceActivity와 함께 생성될 MyEvaluationPresenter를 지연 초기화
    private lateinit var myPreferencePresenter: MyPreferencePresenter
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypreference)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        myPreferencePresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

        // MypageActivity에서 데이터 받아오기
        val intent = intent
        val nickname = intent.getStringExtra("nickname")

        // 화면에 nickname 설정
        mypreference_txtv_title.text = nickname + "님의 취향 분석"

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // barchart 이벤트 처리
        getCountRatingSubscribe()

        // wordcloud 이벤트 처리
        getKeywordSubscribe()
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

    fun setBarChart(jsonObject: JsonObject){
        val rating_05 = jsonObject.get("rating_05").toString().replace("\"", "").toFloat()
        val rating_10 = jsonObject.get("rating_10").toString().replace("\"", "").toFloat()
        val rating_15 = jsonObject.get("rating_15").toString().replace("\"", "").toFloat()
        val rating_20 = jsonObject.get("rating_20").toString().replace("\"", "").toFloat()
        val rating_25 = jsonObject.get("rating_25").toString().replace("\"", "").toFloat()
        val rating_30 = jsonObject.get("rating_30").toString().replace("\"", "").toFloat()
        val rating_35 = jsonObject.get("rating_35").toString().replace("\"", "").toFloat()
        val rating_40 = jsonObject.get("rating_40").toString().replace("\"", "").toFloat()
        val rating_45 = jsonObject.get("rating_45").toString().replace("\"", "").toFloat()
        val rating_50 = jsonObject.get("rating_50").toString().replace("\"", "").toFloat()

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

        averageRating.add(BarEntry(rating_05, 0))
        averageRating.add(BarEntry(rating_10, 1))
        averageRating.add(BarEntry(rating_15, 2))
        averageRating.add(BarEntry(rating_20, 3))
        averageRating.add(BarEntry(rating_25, 4))
        averageRating.add(BarEntry(rating_30, 5))
        averageRating.add(BarEntry(rating_35, 6))
        averageRating.add(BarEntry(rating_40, 7))
        averageRating.add(BarEntry(rating_45, 8))
        averageRating.add(BarEntry(rating_50, 9))

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

    fun setWordCloud(jsonArray: JsonArray) {
        var replaceJsonArray = JsonArray()
        for (i in 0 until jsonArray.size()) {
            var jsonObject = jsonArray[i].asJsonObject
            var replaceJsonObject = JsonObject()

            // 데이터 가공 처리(큰따옴표 제거)
            var text = jsonObject.get("keyword").toString().replace("\"", "")
            var size = jsonObject.get("size").toString().replace("\"", "").toInt()
            size = size + 10
            replaceJsonObject.addProperty("text", text)
            replaceJsonObject.addProperty("size", size.toString())

            replaceJsonArray.add(replaceJsonObject)
        }

        val webSettings = mypreference_webv_wordcloud.settings
        webSettings.javaScriptEnabled = true
        mypreference_webv_wordcloud.loadUrl("file:///android_asset/d3.html")
        mypreference_webv_wordcloud.webViewClient = object : WebViewClient() {
            override fun onPageFinished(webView: WebView, url: String) {
                super.onPageFinished(webView, url)
                val stringBuffer = StringBuffer()
                stringBuffer.append("wordCloud(")
                stringBuffer.append(replaceJsonArray.toString())
                stringBuffer.append(")")
                println(stringBuffer)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mypreference_webv_wordcloud.evaluateJavascript(stringBuffer.toString(), null)
                } else {
                    mypreference_webv_wordcloud.loadUrl("javascript:$stringBuffer")
                }
            }
        }
    }

    // getCountRatingSubscribe : 도서 키워드 조회 관찰자를 구독하는 함수
    private fun getCountRatingSubscribe() {
        val subscription =
            myPreferencePresenter
                .getCountRatingObservable(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var jsonObject = (result.get("data")).asJsonObject

                            // 변경된 평점 반영
                            setBarChart(jsonObject)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Create evaluation error!")
                    }
                )
        disposables.add(subscription)
    }

    // getKeywordSubscribe : 도서 키워드 조회 관찰자를 구독하는 함수
    private fun getKeywordSubscribe() {
        val subscription =
            myPreferencePresenter
                .getKeywordObservable(this, 40)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var jsonArray = (result.get("data")).asJsonArray

                            // 변경된 평점 반영
                            setWordCloud(jsonArray)
                        }
                        // 설정 끝낸 후 프로그래스 바 종료
                        showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Create evaluation error!")
                    }
                )
        disposables.add(subscription)
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