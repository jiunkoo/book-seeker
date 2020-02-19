module.exports = {
    // 데이터 셋 트레이닝
    trainingDataSet: (evaluationList) => {
        // trainedDataSet : 학습을 마친 데이터 객체
        // userBasedData : 사용자별 도서 평가 데이터 객체
        // bookBasedData : 도서별 도서 평가 사용자 데이터 객체
        // bookRatingRank : 도서별 평점 합계 객체
        let trainedDataSet = {}, userBasedData = {}, bookBasedData = {}, bookRatingRank = {};

        // trainSet : 도서 목록에서 추출한 학습 데이터 배열
        // testSet : 도서 목록에서 추출한 테스트 데이터 베열
        // bookRankingList : 전체 평가를 바탕으로 한 도서 순위 목록 배열
        let bookRankingList = [], trainSet = [], testSet = [];

        // 데이터 셋을 불러온 후 8:2 비율로 트레이닝, 테스트 집합 분할
        for (let i = 0; i < evaluationList.length; i++) {
            if (Math.random() > 0.8) {
                testSet.push(evaluationList[i]);
            }
            else {
                trainSet.push(evaluationList[i]);
            }
        }

        // 반복문을 돌려 트레이닝 집합에서 기준에 따라 데이터 분류
        for (let i = 0; i < trainSet.length; i++) {
            let user_uid = trainSet[i]['user_uid'];
            let bsin = trainSet[i]['bsin'];
            let rating = trainSet[i]['rating'] * 1;

            // 사용자가 userBasedData에 없는 경우 새로운 1차원 배열을 생성하고, 평가 데이터 저장
            if (!userBasedData[user_uid]) {
                userBasedData[user_uid] = [];
            }
            userBasedData[user_uid].push({ bsin: bsin, rating: rating });

            // 도서가 bookBasedData에 없는 경우 새로운 1차원 배열을 생성하고 평가 데이터 저장
            if (!bookBasedData[bsin]) {
                bookBasedData[bsin] = [];
            }
            bookBasedData[bsin].push({ user_uid: user_uid, rating: rating });

            // 도서가 bookRatingRank에 없는 경우 새로운 1차원 배열을 생성하고 평가 데이터 합계를 저장
            if (!bookRatingRank[bsin]) {
                bookRatingRank[bsin] = 0;
            }
            bookRatingRank[bsin] += rating;
        }

        // 반복문을 돌려 bookRatingRank에 있는 평점을 bookRankingList으로 옮기고 내림차순으로 정렬
        for (let bsin in bookRatingRank) {
            bookRankingList.push({ bsin: bsin, rating: bookRatingRank[bsin] });
        }
        bookRankingList.sort((a, b) => b.rating - a.rating);

        // 학습을 마친 데이터를 trainedDdataSet에 넣음
        trainedDataSet.userBasedData = userBasedData;
        trainedDataSet.bookBasedData = bookBasedData;
        trainedDataSet.bookRankingList = bookRankingList;

        return trainedDataSet;
    },

    // 사용자에게 도서 추천
    recommendBookList: (user_uid, trainedDataSet, count) => {
        // 특정 사용자의 도서 평가 목록을 불러옴
        let userList = trainedDataSet.userBasedData[user_uid];

        // 특정 사용자의 도서 평가 목록이 있는 경우
        // 특정 사용자의 도서 평가 목록을 바탕으로 비슷한 사용자를 찾아 도서 추천
        if (userList) {
            // completionEvaluation : 특정 사용자의 도서 평가 목록(유사도 계산이 끝난 도서 목록)
            // similarUsers :  특정 사용자와 같은 도서를 평가한 비슷한 사용자 유사도 목록 객체
            // estimatedEvaluation : 계산한 예상 도서 평점 목록 객체
            let completionEvaluation = {}, similarUsers = {}, estimatedEvaluation = {};

            // relatedUsers : 전체 유사도 계산을 바탕으로 한 특정 사용자와 비슷한 사용자 간 유사도 배열
            // returnData : 프론트에 반환할 결과값이 들어갈 배열
            let relatedUsers = [], returnData = [];

            // estimatedEvaluationCount : 예상 도서 평점 목록 객체 개수
            let estimatedEvaluationCount = 0;

            // 반복문을 돌려 특정 사용자와 같은 도서를 평가한 비슷한 사용자 목록 추출
            for (let i = 0; i < userList.length; i++) {
                completionEvaluation[userList[i].bsin] = true;
                let similarUserList = trainedDataSet.bookBasedData[userList[i].bsin];

                // 반복문을 돌려 한 개 도서에 대해 사용자 간 유사도 계산
                // 유사도 계산 방법 : 내적(Inner Product)
                for (let j = 0; j < similarUserList.length; j++) {
                    if (!similarUsers[similarUserList[j].user_uid]) {
                        similarUsers[similarUserList[j].user_uid] = 0;
                    }
                    // 내적으로 유사도 계산
                    similarUsers[similarUserList[j].user_uid] += similarUserList[j].rating * userList[i].rating;
                }
            }

            // 반복문을 돌려 similarUsers에 있는 평점을 relatedUsers로 옮기고 내림차순으로 정렬
            for (let user_uid in similarUsers) {
                relatedUsers.push({ user_uid: user_uid, rating: similarUsers[user_uid] });
            }
            relatedUsers.sort((a, b) => b.rating - a.rating);

            // 반복문을 돌려 사용자 간 유사도 배열을 바탕으로 사용자의 도서 평가 목록 추출
            for (let i = 0; i < relatedUsers.length; i++) {
                let user_uid = relatedUsers[i].user_uid;
                let rating = relatedUsers[i].rating;
                let userList = trainedDataSet.userBasedData[user_uid];

                // 반복문을 돌려 추출한 도서 평가 목록을 바탕으로 도서 예상 평점 계산
                // 예상 평점 계산 : 평점 합계
                for (let j = 0; j < userList.length; j++) {
                    // 이미 평점 계산이 끝난 도서인 경우
                    if (completionEvaluation[userList[j].bsin]) {
                        continue;
                    }
                    // 아직 예상 평점 계산을 하지 않은 도서의 경우 평점 계산
                    if (!estimatedEvaluation[userList[j].bsin]) {
                        estimatedEvaluation[userList[j].bsin] = 0;
                        estimatedEvaluationCount++;
                    }
                    estimatedEvaluation[userList[j].bsin] += rating;
                }
            }

            // 예상 평점 목록을 결과 배열에 넣고 내림차순으로 정렬
            // 특정 개수만큼 자름
            for (let bsin in estimatedEvaluation) {
                returnData.push({ bsin: bsin, rating: Math.round(Math.log(estimatedEvaluation[bsin] + 1) * 100) / 100 });
            }
            returnData.sort((a, b) => b.rating - a.rating);
            returnData.splice(count); 

            // 반복문을 돌려 특정 개수만큼 채움
            for (let i = 0; i < trainedDataSet.bookRankingList.length; i++) {
                if (returnData.length >= count) {
                    break;
                }
                if (!estimatedEvaluation[trainedDataSet.bookRankingList[i].bsin]) {
                    returnData.push(trainedDataSet.bookRankingList[i]);
                }
            }

            return returnData;
        }
        // 로그인 한 사용자의 도서 평가 목록이 없는 경우
        else {
            // 페이징 후 랭킹 배열에서 선택한 개수만큼 반환
            return JSON.parse(JSON.stringify(trainedDataSet.bookRankingList)).splice(0, count);
        }
    }
};