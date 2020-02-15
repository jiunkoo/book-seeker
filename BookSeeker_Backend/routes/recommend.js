// 웹 프레임워크
const express = require('express');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const Sequelize = require('sequelize');

// 추천 라이브러리
const RF = require('../config/recommendAlgorithm');

// 모델 및 미들웨어 선언
const { Rating } = require('../models');
const { clientIp, isLoggedIn } = require('./middlewares');

// 로그 생성
const winston = require('../config/winston');

//라우터
const router = express.Router();

// 추천 만화 목록을 반환
router.post('/comic', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        winston.log('info', `[RECOMMEND][${req.clientIp}|${user_email}]  추천 만화 목록 조회 Request`);

        // 추천을 적용할 만화 평가 데이터 불러오기
        const comicRatingList = await Rating.findAll({
            where: {
                genre: 'COMIC'
            }
        });

        const trainedDataSet = await RF.trainingDataSet(comicRatingList);
        const recommendBookList = await RF.recommendBookList(user_uid, trainedDataSet, 100);

        // 전체 도서 목록 조회 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = recommendBookList;
        result.message = '추천 도서 목록 조회를 성공했습니다.';
        winston.log('info', `[RECOMMEND][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RECOMMEND][${req.clientIp}|${req.body.email}] 추천 도서 목록 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RECOMMEND][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

module.exports = router;