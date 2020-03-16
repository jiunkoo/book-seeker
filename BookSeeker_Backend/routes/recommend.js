// 웹 프레임워크
const express = require('express');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const Sequelize = require('sequelize');
const Op = Sequelize.Op;
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/../config/server_info.json')[env];

// 추천 라이브러리
const RF = require('../config/recommendAlgorithm');

// 모델 및 미들웨어 선언
const { Evaluation } = require('../models');
const { clientIp, isLoggedIn } = require('./middlewares');

// 로그 생성
const winston = require('../config/winston');

//라우터
const router = express.Router();

// Raw query 작성에 필요한 sequelize 변수 선언
let sequelize;
if (config.use_env_variable) {
    sequelize = new Sequelize(process.env[config.use_env_variable], config);
} else {
    sequelize = new Sequelize(
        config.database,
        config.username,
        config.password,
        config
    );
}

// 추천 도서 목록을 반환
router.get('/:genre/:page/:limit', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const genre = req.params.genre;
        const page = parseInt(req.params.page);
        const limit = parseInt(req.params.limit);

        winston.log('info', `[RECOMMEND][${req.clientIp}|${user_email}]  추천 ${genre} 목록 조회 Request`);
        winston.log('info', `[RECOMMEND][${req.clientIp}|${user_email}] genre: ${genre}, page : ${page}, limit : ${limit}`);

        // 평가 도서 목록 불러오기
        /*
        1) 추천 받을 사용자가 '관심 없어요' 체크한 데이터 제외
        2) 평가가 0 이하인 데이터 제외(추천 알고리즘 정확도 증가)
        3) 일반 사용자가 삭제한 데이터 제외
        */
        // 내부 데이터 확인 : evaluationList[i].id
        let evaluationQuery =
            'SELECT * ' +
            'FROM evaluations ' +
            'WHERE genre=:genre ' +
            'AND id NOT IN ' +
            '(SELECT id ' +
            'FROM evaluations ' +
            'WHERE genre=:genre ' +
            'AND user_uid=:user_uid ' +
            'AND state=:state) ' +
            'AND rating > 0 ' +
            'AND deletedAt IS NULL;';

        const evaluationList = await sequelize.query(evaluationQuery, {
            replacements: {
                genre: genre,
                user_uid: user_uid,
                state: 0
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        // 평가하지 않은 도서 목록 불러오기(평가 도서 목록 제외)
        let unEvaluationQuery =
            'SELECT bsin ' +
            'FROM books ' +
            'WHERE genre=:genre ' +
            'AND bsin NOT IN ' +
            '(SELECT bsin ' +
            'FROM evaluations ' +
            'WHERE genre=:genre ' + 
            'AND id NOT IN ' +
            '(SELECT id ' +
            'FROM evaluations ' +
            'WHERE genre=:genre ' +
            'AND user_uid=:user_uid ' +  
            'AND state=:state) ' +
            'AND rating > 0 ' +
            'AND deletedAt IS NULL);';

        const unEvaluationList = await sequelize.query(unEvaluationQuery, {
            replacements: {
                genre: genre
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        const trainedDataSet = await RF.trainingDataSet(evaluationList, unEvaluationList);
        const bookRecommend = await RF.bookRecommend(user_uid, trainedDataSet, page, limit);

        // Database에서 검사할 BsinList
        let bookBsinList = [];

        // 반환된 추천 목록을 문자열로 변환
        for (let i in bookRecommend) {
            bookBsinList[i] = JSON.stringify(bookRecommend[i]['bsin']);
        }

        let bookQuery =
            'SELECT b.*, IFNULL(e.rating, -2) AS rating, IFNULL(e.state, -2) AS state ' +
            'FROM (' +
            'SELECT * ' +
            'FROM books ' +
            'WHERE bsin IN ( ' + bookBsinList.join() + ')) AS b ' +
            'LEFT OUTER JOIN evaluations AS e ' +
            'ON b.bsin = e.bsin ' +
            'AND e.user_uid=:user_uid ' +
            'AND e.deletedAt IS NULL';

        const recommendBookList = await sequelize.query(bookQuery, {
            replacements: {
                user_uid: user_uid
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        const returnData = Array();
        for(let i = 0; i<recommendBookList.length; i++){
            const jsonObject = Object();
            jsonObject.bsin = recommendBookList[i].bsin;
            jsonObject.title = recommendBookList[i].title;
            jsonObject.author = recommendBookList[i].author;
            jsonObject.publisher = recommendBookList[i].publisher;
            jsonObject.introduction = recommendBookList[i].introduction;
            jsonObject.cover = recommendBookList[i].cover;
            jsonObject.link = recommendBookList[i].link;
            jsonObject.keyword = recommendBookList[i].keyword;
            jsonObject.adult = recommendBookList[i].adult;
            jsonObject.genre = recommendBookList[i].genre;
            jsonObject.publication_date = recommendBookList[i].publication_date;
            jsonObject.rating = recommendBookList[i].rating;
            jsonObject.state = recommendBookList[i].state;
            jsonObject.expect_rating = bookRecommend[i].rating;
            returnData[i] = jsonObject;
        }

        // 전체 도서 목록 조회 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
        result.message = `추천 ${genre} 목록 조회를 성공했습니다.`;
        winston.log('info', `[RECOMMEND][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RECOMMEND][${req.clientIp}|${req.user.email}] 추천 도서 목록 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RECOMMEND][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

module.exports = router;