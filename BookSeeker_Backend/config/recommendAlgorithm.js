module.exports = {
    // 데이터 셋 트레이닝
    trainingDataSet: (user_uid, evaluationList, unEvaluationList) => {
        // trainedDataSet : 학습을 마친 데이터 객체
        // userState : 특정 사용자의 도서 상태 객체
        // userBasedData : 사용자별 도서 평가 데이터 객체
        // bookBasedData : 도서별 도서 평가 사용자 데이터 객체
        // bookRatingRank : 도서별 평점 합계 객체
        let trainedDataSet = {}, userState = {}, userBasedData = {}, bookBasedData = {}, bookRatingRank = {};

        // trainSet : 도서 목록에서 추출한 학습 데이터 배열
        // testSet : 도서 목록에서 추출한 테스트 데이터 베열
        // bookRankingList : 전체 평가를 바탕으로 한 도서 순위 목록 배열
        let bookRankingList = [], trainSet = [], testSet = [], unTrainSet = [];

        /*
        // 데이터 셋을 불러온 후 8:2 비율로 트레이닝, 테스트 집합 분할
        for (let i = 0; i < evaluationList.length; i++) {
            if (Math.random() > 0.8) {
                testSet.push(evaluationList[i]);
            }
            else {
                trainSet.push(evaluationList[i]);
            }
        }
        */

        // 데이터 셋을 불러와 저장
        for (let i = 0; i < evaluationList.length; i++) {
            trainSet.push(evaluationList[i]);
        }

        for (let i = 0; i < unEvaluationList.length; i++) {
            unTrainSet.push(unEvaluationList[i]);
        }

        // 반복문을 돌려 트레이닝 집합에서 기준에 따라 데이터 분류
        for (let i = 0; i < trainSet.length; i++) {
            let user_uid = trainSet[i]['user_uid'];
            let bsin = trainSet[i]['bsin'];
            let rating = trainSet[i]['rating'] * 1;
            let state = trainSet[i]['state'];

            // 사용자가 userBasedData에 없는 경우 새로운 1차원 배열을 생성하고, 평가 데이터 저장
            if (!userBasedData[user_uid]) {
                userBasedData[user_uid] = [];
            }
            userBasedData[user_uid].push({ bsin: bsin, rating: rating, state: state });

            // 도서가 bookBasedData에 없는 경우 새로운 1차원 배열을 생성하고 평가 데이터 저장
            if (!bookBasedData[bsin]) {
                bookBasedData[bsin] = [];
            }
            bookBasedData[bsin].push({ user_uid: user_uid, rating: rating, state: state });

            // 도서가 bookRatingRank에 없는 경우 평가 데이터 합계를 저장
            if (!bookRatingRank[bsin]) {
                bookRatingRank[bsin] = 0;
            }
            bookRatingRank[bsin] += rating;
        }

        // 특정 사용자의 도서 평가 목록을 불러옴
        let userList = userBasedData[user_uid];
        if(userList){
            // 예상 도서 평점 필터링을 위한 도서 상태 객체 분류
            for (let i = 0; i < userList.length; i++) {
                if (!userState[userList[i].bsin]) {
                    userState[userList[i].bsin] = userList[i].state;
                }
            }
        }

        // 반복문을 돌려 unTrainSet에서 기준에 따라 데이터 분류
        for (let i = 0; i < unTrainSet.length; i++) {
            let bsin = unTrainSet[i]['bsin'];

            // 도서가 bookRatingRank에 없는 경우 0점 저장
            if (!bookRatingRank[bsin]) {
                bookRatingRank[bsin] = 0;
            }
        }

        // 반복문을 돌려 bookRatingRank에 있는 평점을 bookRankingList으로 옮기고 내림차순으로 정렬
        for (let bsin in bookRatingRank) {
            if(userState[bsin] == 3){
                continue;
            }else {
                bookRankingList.push({ bsin: bsin, rating: bookRatingRank[bsin] });
            }
        }
        bookRankingList.sort((a, b) => b.rating - a.rating);

        // 학습을 마친 데이터를 trainedDdataSet에 넣음
        trainedDataSet.userState = userState;
        trainedDataSet.userBasedData = userBasedData;
        trainedDataSet.bookBasedData = bookBasedData;
        trainedDataSet.bookRankingList = bookRankingList;

        return trainedDataSet;
    },

    // 사용자에게 도서 추천
    bookRecommend: (user_uid, trainedDataSet, page, limit) => {
        // 특정 사용자의 도서 평가 목록을 불러옴
        let userList = trainedDataSet.userBasedData[user_uid];

        // 특정 사용자의 도서 평가 목록이 있는 경우
        // 특정 사용자의 도서 평가 목록을 바탕으로 비슷한 사용자를 찾아 도서 추천
        if (userList) {
            // completionEvaluation : 특정 사용자의 도서 평가 목록(유사도 계산이 끝난 도서 목록)
            // similarUsers : 특정 사용자와 같은 도서를 평가한 비슷한 사용자 유사도 목록 객체
            // estimatedEvaluation : 계산한 예상 도서 평점 목록 객체
            completionEvaluation = {}, similarUsers = {}, estimatedEvaluation = {};

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
                let related_uuid = relatedUsers[i].user_uid;
                let related_rating = relatedUsers[i].rating;
                let relatedUserList = trainedDataSet.userBasedData[related_uuid];

                // 반복문을 돌려 추출한 도서 평가 목록을 바탕으로 도서 예상 평점 계산
                // 예상 평점 계산 : 평점 합계
                for (let j = 0; j < relatedUserList.length; j++) {
                    // 이미 평점 계산이 끝난 도서인 경우
                    if (completionEvaluation[relatedUserList[j].bsin]) {
                        continue;
                    }
                    // 아직 예상 평점 계산을 하지 않은 도서의 경우 평점 계산
                    if (!estimatedEvaluation[relatedUserList[j].bsin]) {
                        estimatedEvaluation[relatedUserList[j].bsin] = 0;
                        estimatedEvaluationCount++;
                    }
                    estimatedEvaluation[relatedUserList[j].bsin] += related_rating;
                }
            }

            // 예상 평점 목록을 결과 배열에 넣고 내림차순으로 정렬
            for (let bsin in estimatedEvaluation) {
                // 특정 사용자 평가가 있는 경우
                if (trainedDataset.userState[bsin]) {
                    if (trainedDataset.userState[bsin] == 0 | trainedDataset.userState[bsin] == 3) {
                        continue; // push 생략
                    } else {
                        returnData.push({ bsin: bsin, rating: Math.round(Math.log(estimatedEvaluation[bsin] + 1) * 100) / 100 });

                        // 정렬 과정에서 중복되는 값 랭킹 배열에서 제거
                        const itemToFind = trainedDataSet.bookRankingList.find(function (item) { return item.bsin === bsin })
                        const idx = trainedDataSet.bookRankingList.indexOf(itemToFind);
                        if (idx > -1) {
                            trainedDataSet.bookRankingList.splice(idx, 1);
                        }
                    }
                } else {
                    returnData.push({ bsin: bsin, rating: Math.round(Math.log(estimatedEvaluation[bsin] + 1) * 100) / 100 });

                    // 정렬 과정에서 중복되는 값 랭킹 배열에서 제거
                    const itemToFind = trainedDataSet.bookRankingList.find(function (item) { return item.bsin === bsin })
                    const idx = trainedDataSet.bookRankingList.indexOf(itemToFind);
                    if (idx > -1) {
                        trainedDataSet.bookRankingList.splice(idx, 1);
                    }
                }
            }
            returnData.sort((a, b) => b.rating - a.rating);

            console.log("예상 평점 정렬 끝");

            // 페이징 변수 선언
            let start = page * limit;

            // 전체 예상 평점 개수가 요청하는 페이지의 마지막 원소 번호와 같거나 큰 경우
            if (estimatedEvaluationCount >= start + 9) {
                // limit 개수만큼 slice
                returnData.splice(start, limit);
            }
            // 전체 예상 평점 개수가 요청하는 페이지의 마지막 원소 번호보다 작은 경우
            else {
                // 예상 평점의 몫과 나머지
                let quotient = estimatedEvaluationCount / limit;
                let remainder = estimatedEvaluationCount % limit + 1;

                // 예상 평점의 몫과 페이지가 같은 경우
                if (quotient == page) {
                    // 나머지만큼 slice 후 남은 건 ranking으로 채우기
                    returnData.splice(start, limit);

                    // 반복문을 돌려 특정 개수만큼 채움
                    for (let i = 0; i < trainedDataSet.bookRankingList.length; i++) {
                        if (returnData.length >= limit) {
                            break;
                        }
                        if (!estimatedEvaluation[trainedDataSet.bookRankingList[i].bsin]) {
                            returnData.push(trainedDataSet.bookRankingList[i]);
                        } 
                    }
                }
                // 예상 평점의 몫과 페이지가 다른 경우
                else {
                    // 나머지가 없으므로 차이를 구해 차이만큼 반복문에서 데이터 뽑아냄
                    for (let i = (page - quotient) * limit - remainder; i < trainedDataSet.bookRankingList.length; i++) {
                        if (returnData.length >= limit) {
                            break;
                        }
                        if (!estimatedEvaluation[trainedDataSet.bookRankingList[i].bsin]) {
                            returnData.push(trainedDataSet.bookRankingList[i]);
                        }
                    }
                }
            }
            return returnData;
        }
        // 로그인 한 사용자의 도서 평가 목록이 없는 경우
        else {
            // returnData : 프론트에 반환할 결과값이 들어갈 배열
            let returnData = [];

            // 페이징 변수 선언
            let start = page * limit;

            // 반복문을 돌려 limit 개수만큼 집어넣음
            for (let i = start; i < start + limit; i++) {
                returnData.push(trainedDataSet.bookRankingList[i]);
            }
            return returnData;
        }
    }
};