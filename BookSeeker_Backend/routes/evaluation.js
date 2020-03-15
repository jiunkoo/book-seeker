// 웹 프레임워크
const express = require('express');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const Sequelize = require('sequelize');
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/../config/server_info.json')[env];

// 모델 및 미들웨어 선언
const { Book, Evaluation } = require('../models');
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

// 도서 평가
router.post('/', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const { bsin, genre } = req.body;
        const rating = parseFloat(req.body.rating);
        const state = parseInt(req.body.state);

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 도서 평가 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] bsin : ${bsin}, genre : ${genre}, rating : ${rating}, state : ${state}`);

        // 별점이 0점 미만이거나 5점을 초과하는 경우
        if (rating > 5 || rating < 0) {
            // 도서 평가 실패 메세지 반환
            const result = new Object();
            result.success = false;
            result.data = 'NONE';
            result.message = '별점이 잘못되었습니다.';
            winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        }

        // 기존 사용자의 평가 데이터가 있는지 검색
        let query =
            'SELECT * ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid AND bsin=:bsin; ';

        const evaluation = await sequelize.query(query, {
            replacements: {
                user_uid: user_uid,
                bsin: bsin
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        // 평가 데이터가 있는 경우
        if (evaluation[0] != null) {
            // 기존의 평가 데이터 수정
            let query =
                'UPDATE evaluations ' +
                'SET rating=:rating, state=:state, deletedAt=null ' +
                'WHERE user_uid=:user_uid AND bsin=:bsin; ';

            await sequelize.query(query, {
                replacements: {
                    user_uid: user_uid,
                    bsin: bsin,
                    rating: rating,
                    state: state
                },
                type: Sequelize.QueryTypes.UPDATE,
                raw: true
            });

            const returnData = await Evaluation.findOne({
                where: {
                    user_uid: user_uid,
                    bsin: bsin
                }
            });

            // 도서 평가 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = returnData;
            result.message = '도서 평가를 성공했습니다.';
            winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        }
        // 평가 데이터가 없는 경우
        else {
            // 평가 데이터 생성
            const createEvaluation = await Evaluation.create({
                user_uid: user_uid,
                bsin: bsin,
                genre: genre,
                rating: rating,
                state: state
            });

            // 도서 평가 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = createEvaluation;
            result.message = '도서 평가를 성공했습니다.';
            winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(201).send(result);
        }
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 도서 평가 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 도서 평가 수정
router.patch('/', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const bsin = req.body.bsin;
        const rating = req.body.rating;
        const state = req.body.state;

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 도서 평가 수정 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] bsin : ${bsin}, rating : ${rating}, state : ${state}`);

        const returnData = Object();
        returnData.bsin = bsin;
        returnData.rating = rating;
        returnData.state = state;

        // 도서 평가 수정
        await Evaluation.update({
            rating: rating,
            state: state
        }, {
            where: {
                user_uid: user_uid,
                bsin: bsin
            }
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
        result.message = '도서 평가 수정을 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 도서 평가 수정 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 도서 평가 삭제
router.delete('/:bsin', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const bsin = req.params.bsin;

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 도서 평가 삭제 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] bsin : ${bsin}`);

        // 도서 평가 삭제
        await Evaluation.destroy({
            where: {
                user_uid: user_uid,
                bsin: bsin
            }
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bsin;
        result.message = '도서 평가 삭제를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 도서 평가 삭제 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

module.exports = router;