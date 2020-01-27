package com.example.bookseeker.presenter

import android.util.Log
import com.example.bookseeker.adapter.contract.RatingAdapterContract
import com.example.bookseeker.contract.RatingContract
import com.example.bookseeker.model.data.BookList
import com.example.bookseeker.model.data.BookData
import com.example.bookseeker.network.RetrofitClient
import io.reactivex.Observable

class RatingPresenter : RatingContract.Presenter {
    private var ratingView: RatingContract.View? = null
    private var adapterView: RatingAdapterContract.View? = null
    private var adapterModel: RatingAdapterContract.Model? = null
    private val retrofitInterface = RetrofitClient.retrofitInterface

    // takeView : View가 Create, Bind 될 때 Presenter에 전달하는 함수
    override fun takeView(view: RatingContract.View) {
        ratingView = view
    }

    // getAllComicBookList : RatingPresenter에서 모든 ComicInfo 데이터를 가져오는 함수
    fun getAllComicBookList(category: Int, page: Int, page_size: Int = 10): Observable<BookList> {
        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.selectAllComicData(category, page, page_size)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                /*
                val bookListResults = response.body()?.results?.map {
                    BookData(it.title, it.author, it.publisher, it.image, 0.0f)
                }
                */
                val bookListResults: List<BookData> = response.body()!!.results

                if (bookListResults != null) {
                    val responsePage = page + 1 // 페이지 증가
                    val bookList = BookList(responsePage, bookListResults) // page + results
                    subscriber.onNext(bookList) // 구독자(관찰자)에게 데이터의 발행을 알림
                }
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
        /*
        ratingView?.setProgressON("데이터를 가져오고 있습니다...")
        // background thread
        var getAllRomanceBookData = retrofitInterface.selectAllRomanceData()
        getAllRomanceBookData.enqueue(object : Callback<List<BookData>> {
            override fun onResponse(call: Call<List<BookData>>, response: Response<List<BookData>>) {
                executionLog("SELECT", "Get All Romance List Success!")
                if (response.isSuccessful) {
                    var response = response.body()
                    if (response != null) {
                        executionLog("RESPONSE", "$response")
                        for(i in 0 until response.size){
                            val oneBookData = BookData(response.get(i).title, response.get(i).author,
                                response.get(i).publisher, response.get(i).image)
                            bookData.add(oneBookData)
                        }
                        bookData.addAll(response)
                    }
                }
            }
            override fun onFailure(call: Call<List<BookData>>, t: Throwable) {
                executionLog("SELECT", "Get All Romance List Failure!")
            }
        })
        ratingView?.setProgressOFF()
        ratingView?.showMessage("모든 로맨스 장르 데이터를 불러왔습니다.")
        */
    }

    // getAllRomanceBookList : RatingPresenter에서 모든 RomanceInfo 데이터를 가져오는 함수
    fun getAllRomanceBookList(page: Int, page_size: Int = 10): Observable<BookList> {
        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.selectAllRomanceData(page, page_size)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val bookListResults: List<BookData> = response.body()!!.results

                if (bookListResults != null) {
                    val responsePage = page + 1 // 페이지 증가
                    val bookList = BookList(responsePage, bookListResults) // page + results
                    subscriber.onNext(bookList) // 구독자(관찰자)에게 데이터의 발행을 알림
                }
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // getAllFantasyBookList : RatingPresenter에서 모든 FantasyInfo 데이터를 가져오는 함수
    fun getAllFantasyBookList(page: Int, page_size: Int = 10): Observable<BookList> {
        return Observable.create { subscriber ->
            // 데이터 생성을 위한 Create
            val callResponse = retrofitInterface.selectAllFantasyData(page, page_size)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val bookListResults: List<BookData> = response.body()!!.results

                if (bookListResults != null) {
                    val responsePage = page + 1 // 페이지 증가
                    val bookList = BookList(responsePage, bookListResults) // page + results
                    subscriber.onNext(bookList) // 구독자(관찰자)에게 데이터의 발행을 알림
                }
                subscriber.onComplete() // 모든 데이터 발행이 완료되었음을 알림
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }
    }

    // dropView : View가 delete, unBind 될 때 Presenter에 전달하는 함수
    override fun dropView() {
        ratingView = null
    }

    // executionLog : 공통으로 사용하는 Log 출력 부분을 생성하는 함수
    override fun executionLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}