package com.example.bookseeker.presenter

<<<<<<< HEAD
import android.util.Log
=======
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
import com.example.bookseeker.contract.RatingContract

class RatingPresenter : RatingContract.Presenter {
    private var ratingView: RatingContract.View? = null

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RatingContract.View) {
        ratingView = view
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        ratingView = null
    }
<<<<<<< HEAD

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String){
        Log.e(tag, msg)
    }
=======
>>>>>>> 17feb1f3afe9a5d4ca132a30ace1d53c1d8d1cae
}