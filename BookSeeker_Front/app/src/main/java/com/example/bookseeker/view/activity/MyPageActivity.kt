package com.example.bookseeker.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookseeker.R
import com.example.bookseeker.contract.MypageContract
import com.example.bookseeker.presenter.MyPagePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mypage.*

class MyPageActivity : BaseActivity(), MypageContract.View {
    // MypageActivity와 함께 생성될 MypagePresenter를 지연 초기화
    private lateinit var myPagePresenter: MyPagePresenter
    // Disposable 객체 지연 초기화
    private lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // View가 Create(Bind) 되었다는 걸 Presenter에 전달
        myPagePresenter.takeView(this)

        // Disposable 객체 지정
        disposables = CompositeDisposable()

        // BottomNavigationView 이벤트 처리
        switchBottomNavigationView()

        // Cardview 이벤트 처리
        setCardviewEventListener()

        // 서버에서 데이터 받아오기
        getMineSubscribe()
        getCountGenreSubscribe()
        getCountStateSubscribe()
    }

    // initPresenter : View와 상호작용할 Presenter를 주입하기 위한 함수
    override fun initPresenter() {
        myPagePresenter = MyPagePresenter()
    }

    // switchBottomNavigationView : RatingActivity에서 BottomNavigationView 전환 이벤트를 처리하는 함수
    override fun switchBottomNavigationView() {
        mypage_btmnavview_menu.setOnNavigationItemSelectedListener { item ->
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
        mypage_btmnavview_menu.menu.findItem(R.id.btmnavmenu_itm_mypage)?.setChecked(true)
    }

    // setCardviewEventListener : Cardview Event를 처리하는 함수
    fun setCardviewEventListener() {
        // Comic Category Event를 처리하는 함수
        mypage_layout_linear_comic.setOnClickListener {
            startMyEvaluationActivity()
        }
        // Romance Category Event를 처리하는 함수
        mypage_layout_linear_romance.setOnClickListener {
            startMyEvaluationActivity()
        }
        // Fantasy Category Event를 처리하는 함수
        mypage_layout_linear_fantasy.setOnClickListener {
            startMyEvaluationActivity()
        }

        //Preference Event를 처리하는 함수
        mypage_cardview_preference.setOnClickListener {
            startMyPreferenceActivity()
        }
    }

    // startMyEvaluationActivity : MyEvaluationActivity로 넘어가는 함수
    fun startMyEvaluationActivity() {
        val nextIntent = Intent(this, MyEvaluationActivity::class.java)
        startActivity(nextIntent)
    }

    // startMyPreferenceActivity : MyPreferenceActivity로 넘어가는 함수
    fun startMyPreferenceActivity() {
        val nextIntent = Intent(this, MyPreferenceActivity::class.java)
        startActivity(nextIntent)
    }

    // getMineSubscribe : 하나의 내 정보 조회 데이터 생성 관찰자를 구독하는 함수
    private fun getMineSubscribe() {
        val subscription =
            myPagePresenter
                .getMineObservable(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            var jsonObject = (result.get("data")).asJsonObject
                            var email = jsonObject.get("email").toString().replace("\"", "")
                            var nickname = jsonObject.get("nickname").toString().replace("\"", "")

                            mypage_txtv_email.text = (email).toString()
                            mypage_txtv_nickname.text = (nickname).toString()
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

    // getCountGenreSubscribe : 장르별 도서 평가 개수 조회 데이터 생성 관찰자를 구독하는 함수
    private fun getCountGenreSubscribe() {
        val subscription =
            myPagePresenter
                .getCountGenreObservable(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            val jsonObject = (result.get("data")).asJsonObject
                            val countComic = jsonObject.get("count_comic").toString().replace("\"", "")
                            val countRomance = jsonObject.get("count_romance").toString().replace("\"", "")
                            val countFantasy = jsonObject.get("count_fantasy").toString().replace("\"", "")

                            mypage_txtv_comic_genre.text = countComic
                            mypage_txtv_romance_genre.text = countRomance
                            mypage_txtv_fantasy_genre.text = countFantasy
                        }
                        // 설정 끝낸 후 메세지 보여주기
                        showMessage(result.get("message").toString())
                    },
                    { e ->
                        showMessage("Create evaluation error!")
                    }
                )
        disposables.add(subscription)
    }

    // getCountStateSubscribe : 장르별 도서 상태 개수 조회 조회 데이터 생성 관찰자를 구독하는 함수
    private fun getCountStateSubscribe() {
        val subscription =
            myPagePresenter
                .getCountStateObservable(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if ((result.get("success").toString()).equals("true")) {
                            val jsonObject = (result.get("data")).asJsonObject
                            val comicBoring = jsonObject.get("comic_boring").toString().replace("\"", "")
                            val comicInteresting = jsonObject.get("comic_interesting").toString().replace("\"", "")
                            val comicReading = jsonObject.get("comic_reading").toString().replace("\"", "")
                            val comicRead = jsonObject.get("comic_read").toString().replace("\"", "")
                            val romanceBoring = jsonObject.get("romance_boring").toString().replace("\"", "")
                            val romanceInteresting = jsonObject.get("romance_interesting").toString().replace("\"", "")
                            val romanceReading = jsonObject.get("romance_reading").toString().replace("\"", "")
                            val romanceRead = jsonObject.get("romance_read").toString().replace("\"", "")
                            val fantasyBoring = jsonObject.get("fantasy_boring").toString().replace("\"", "")
                            val fantasyInteresting = jsonObject.get("fantasy_interesting").toString().replace("\"", "")
                            val fantasyReading = jsonObject.get("fantasy_reading").toString().replace("\"", "")
                            val fantasyRead = jsonObject.get("fantasy_read").toString().replace("\"", "")

                            val comicState = "관심없어요" + comicBoring + "\n" +
                                    "읽고 싶어요" + comicInteresting + "\n" +
                                    "읽고 있어요" + comicReading + "\n" +
                                    "완독 했어요" + comicRead

                            val romanceState = "관심없어요" + romanceBoring + "\n" +
                                    "읽고 싶어요" + romanceInteresting + "\n" +
                                    "읽고 있어요" + romanceReading + "\n" +
                                    "완독 했어요" + romanceRead

                            val fantasyState = "관심없어요" + fantasyBoring + "\n" +
                                    "읽고 싶어요" + fantasyInteresting + "\n" +
                                    "읽고 있어요" + fantasyReading + "\n" +
                                    "완독 했어요" + fantasyRead

                            mypage_txtv_comic_state.text = comicState
                            mypage_txtv_romance_state.text = romanceState
                            mypage_txtv_fantasy_state.text = fantasyState
                        }
                        // 설정 끝낸 후 메세지 보여주기
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
        myPagePresenter.dropView()

        println("마이페이지 disposable 객체 해제 전 : [ONDESTROY]" + disposables.isDisposed)

        // Disposable 객체 전부 해제
        if (!disposables.isDisposed) {
            disposables.dispose()
        }

        println("마이페이지 disposable 객체 해제 후 : [ONDESTROY]" + disposables.isDisposed)
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
        Toast.makeText(this@MyPageActivity, msg, Toast.LENGTH_SHORT).show()
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}