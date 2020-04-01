BOOK SEEKER
=============
> 만화 및 장르 소설 추천 서비스

요약
------------
```
Watcher를 벤치마킹한 만화 및 장르소설 추천 서비스입니다.
사용자는 도서를 검색하고 도서를 평가하고 상태를 분류할 수 있습니다.
또한 평가한 도서를 바탕으로 추천을 받거나 취향을 분석할 수 있습니다.

* 앱 개발에 사용된 모든 데이터는 리디북스에서 크롤링하였습니다.
* 추천 알고리즘은 nodeml을 기반으로 작성되었습니다.
```

개발 환경
------------
> 프론트엔드
- IDE : AndroidStudio
- 언어 : Kotlin
- HTTP 통신 : Retrofit2
- Reactive : RxJava

> 백엔드
- IDE : VSCode
- 런타임 : Node.js
- 언어 : javascript
- 웹 프레임워크 : Express
- 데이터베이스 : mariaDB
- ORM : Sequelize

> 서버
- 플랫폼 : AWS(EC2)
- 운영체제 : Ubuntu
- 프록시 서버 : nginx
- 프로토콜 : HTTPS
- 인증서 : let's encrypt

* 도서 데이터 : 리디북스(2019.9월까지) 크롤링
* 추천 알고리즘 : nodeml 기반(https://github.com/proin/nodeml)

작품 영상(이미지 클릭 시 유튜브로 이동)
-----------
 [![overview](https://user-images.githubusercontent.com/52573800/78160420-e0890980-747e-11ea-801a-fa2118dc318f.png)](https://youtu.be/1qO37pXMd8g)

상세 소개
-----------
* ### 회원 가입, 로그인
  ![register](https://user-images.githubusercontent.com/52573800/78158302-019c2b00-747c-11ea-9391-d482688e10cd.gif)
  ![login](https://user-images.githubusercontent.com/52573800/78158252-f517d280-747b-11ea-8852-bca3ec418537.gif)  
  - 회원가입 시 이메일과 별명, 비밀번호를 입력합니다.
    + 중복되지 않는 이메일이고, 비밀번호와 비밀번호 확인이 일치하면 회원가입이 완료됩니다.
  - 로그인 시 이메일과 비밀번호를 입력해서 회원 정보와 일치하면 메인화면으로 넘어갑니다.

* ### 검색 
  ![search](https://user-images.githubusercontent.com/52573800/78158303-0234c180-747c-11ea-8e4f-c0104156f704.gif)
  ![spinner](https://user-images.githubusercontent.com/52573800/78158305-02cd5800-747c-11ea-832a-a34b77ee2049.gif)
  ![infinity_scroll](https://user-images.githubusercontent.com/52573800/78158226-f21ce200-747b-11ea-8e86-176f26f64d47.gif)
  - 검색 탭에서는 제목을 검색해서 도서를 찾을 수 있습니다.
    + 전체 제목 중 일부분만 일치해도 검색 결과에 나타납니다.
    + 상단에 위치한 스피너로 검색 결과를 기준에 따라 필터링 할 수 있습니다.
    + 검색 결과는 인피니티 스크롤이 적용됩니다.

* ### 도서 상세 조회
  ![bookinfo_1](https://user-images.githubusercontent.com/52573800/78158148-d74a6d80-747b-11ea-8488-d9aa74ef4b4e.gif)
  ![bookinfo_2](https://user-images.githubusercontent.com/52573800/78158216-edf0c480-747b-11ea-8b20-ac849c6b0c43.gif)
  - 도서 목록에서 도서를 터치하면 도서 상세 조회 화면으로 넘어갑니다.
    + 도서 상세 조회 화면에서는 도서에 별점을 매기거나 도서의 상태를 구분할 수 있습니다.
    + 링크 클릭 시 실제 인터넷 서점 화면으로 넘어갑니다.

* ### 추천
  ![recommend_1](https://user-images.githubusercontent.com/52573800/78158282-fcd77700-747b-11ea-9d1e-0d360052213c.gif)
  ![recommend_2](https://user-images.githubusercontent.com/52573800/78158291-ffd26780-747b-11ea-8cec-b1e2013e78bf.gif)
  - 추천 탭에서는 다른 사용자가 평가한 도서를 추천받을 수 있습니다.
    + 추천받은 도서는 카드뷰로 표시되며, 상/하/좌/우 이동으로 상태를 구분할 수 있습니다.
    + 다른 상태는 별점을 매기지 않아도 넘어가지만, '완독 했어요'는 별점 평가가 필요합니다.
    + 10장을 넘기면 새롭게 카드 뭉치가 생성되며, 본인이 평가한 도서는 추천에 나타나지 않습니다.

* ### 추천 알고리즘 요약
```
- 평가 데이터를 1) 사용자별 도서 평가, 2)도서별 사용자 도서 평가, 3) 도서별 랭킹으로 분류합니다.
   + 사용자 별 도서 평가 : 각각의 사용자가 평가한 도서 목록을 의미합니다.
   + 도서별 사용자 도서 평가 : 각각의 도서를 평가한 사용자들의 목록을 의미합니다.
   + 도서별 랭킹 : 각각의 사용자가 평가한 개별 도서 평점의 합계를 의미합니다.

  - 추천받을 사용자의 평가 데이터가 있을 경우 해당 사용자와 유사한 사용자를 찾아 유사도를 계산합니다.
   + 유사도 계산 방식 : 내적((추천받을 사용자의 평점 * 유사한 사용자의 평점)의 총 합계)

 - 사용자 별 유사도를 바탕으로 추천 도서 목록을 추출하고 예상 평점을 계산합니다.
   + 예상 평점 계산 방식 : 유사한 사용자의 평점 합계
   
 - 추천받을 도서 10개를 반환합니다.
   + 추천 도서 목록에서 10개를 자르며, 10개보다 모자랄 경우 도서 랭킹에서 가져옵니다.

- 추천받을 사용자의 평가 데이터가 없을 경우 도서 랭킹에서 10개를 반환합니다.
```

* ### 평가
  ![rating_1](https://user-images.githubusercontent.com/52573800/78158275-fb0db380-747b-11ea-91ec-01f758641163.gif)
  ![rating_2](https://user-images.githubusercontent.com/52573800/78158279-fc3ee080-747b-11ea-8b37-4ac127286b59.gif)
  - 평가 탭에서는 내가 평가하지 않은 도서를 평가할 수 있습니다.
   + 탭을 누르면 장르를 변경할 수 있습니다.
   + 상단에 위치한 스피너로 평가 목록을 기준에 따라 필터링 할 수 있습니다.
   + 랜덤 목록은 페이징 시 결과가 겹치지 않게 임시 테이블에 정보를 저장하며, 스피너 혹은 탭이 변경될 시 초기화됩니다.

* ### 마이 페이지
![mypage](https://user-images.githubusercontent.com/52573800/78158271-f9dc8680-747b-11ea-8646-add06a5854fa.png)
- 마이페이지 탭에서는 지금까지 내가 평가한 도서 개수를 확인할 수 있습니다.

* ### 내가 평가한 도서 조회
![myevaluation_1](https://user-images.githubusercontent.com/52573800/78158255-f5b06900-747b-11ea-8df5-42536268440d.gif)
![myevaluation_2](https://user-images.githubusercontent.com/52573800/78158266-f8ab5980-747b-11ea-8f11-632b89e2797e.gif)
- 마이페이지 탭에서 평가한 도서 개수를 터치하면 내가 평가한 도서 조회 화면으로 넘어갑니다.
  + 상단에 위치한 스피너로 내가 평가한 목록을 도서 상태에 따라 필터링할 수 있습니다.
  + 도서를 터치하면 도서 상세 조회 화면으로 연결됩니다.

* ### 나의 취향 분석
![mypreference](https://user-images.githubusercontent.com/52573800/78158273-fa751d00-747b-11ea-9b08-45db2044e1c8.gif)
- 마이페이지 탭에서 나의 취향 분석 버튼을 터치하면 취향 분석 화면으로 넘어갑니다.
  + 별점에 따라 평가한 도서 개수를 확인할 수 있습니다.
  + 내가 평가한 도서의 키워드가 word cloud 형태로 나타납니다.
