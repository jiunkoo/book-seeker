package com.example.bookseeker.adapter.contract

interface RatingAdapterContract {
    interface View {
        // notifyDataChange : 데이터 변경을 알리는 함수
        fun notifyDataChange()
    }

    interface Model {
        // 내 이메일 정보
        var loginUserEmail: String

//        // addBookData : 도서 데이터를 추가하는 함수
//        fun addBookData(bookData: ArrayList<BookData>)
//
//        // getOneBookData : 하나의 도서 정보를 가져오는 함수
//        fun getOneBookData(position: Int): BookData
//
//        // clearAllBookData : 모든 도서 정보를 삭제하는 함수
//        fun clearAllBookData()
    }
}