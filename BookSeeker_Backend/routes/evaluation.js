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

        // 별점이 -1점 미만이거나 5점을 초과하는 경우
        if (rating > 5 || rating <= -2) {
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

// 전체 평가 도서 목록 조회
router.get('/:genre/:filter/:page/:limit', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const genre = req.parsms.genre;
        const filter = parseInt(req.params.filter);
        const page = parseInt(req.params.page);
        const limit = parseInt(req.params.limit);

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 전체 평가 도서 목록 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}]  genre: ${genre}, filter : ${filter}, page : ${page}, limit : ${limit}`);

        let offset = 0;
        let order = 'publication_date ASC';

        if (page > 1) {
            offset = 10 * (page - 1);
        }

        // 필터에 따라 정렬 기준 변경
        if (filter == 1) {
            order = 'publication_date ASC';
        } else if (filter == 2) {
            order = 'publication_date DESC';
        } else if (filter == 3) {
            order = 'title ASC';
        } else {
            order = 'title DESC';
        }

        // 삭제 데이터를 제외한 전체 도서 평가 목록 불러오기
        let query =
            'SELECT * ' +
            'FROM books ' +
            'WHERE genre=:genre AND ' +
            'bsin = (SELECT bsin FROM evaluations WHERE user_uid = :user_uid AND deletedAt IS NULL) ' +
            'ORDER BY :order ' +
            'LIMIT :limit ' +
            'OFFSET :offset;';

        const bookList = await sequelize.query(query, {
            replacements: {
                user_uid: user_uid,
                genre: genre,
                order: order,
                limit: limit,
                offset: offset
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bookList;
        result.message = '전체 평가 도서 목록 조회를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 전체 평가 도서 목록 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 개별 도서 평가 조회
router.get('/:bsin', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const bsin = parseInt(req.params.bsin);

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 개별 도서 평가 조회 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}]  bsin: ${bsin}`);

        let query =
            'SELECT IFNULL(e2.rating, -1) AS rating, IFNULL(e2.state, -1) AS state, e1.count, e1.average ' +
            'FROM (' +
            'SELECT bsin, COUNT(bsin) AS count, IFNULL(AVG(rating), -1) AS average ' +
            'FROM evaluations ' +
            'WHERE bsin=:bsin' +
            'AND rating > 0' +
            ') AS e1 ' +
            'LEFT OUTER JOIN evaluations AS e2 ' +
            'ON e1.bsin=e2.bsin ' +
            'AND e2.user_uid=:user_uid ' +
            'AND e2.deletedAt IS NULL;';

        const book = await sequelize.query(query, {
            replacements: {
                bsin: bsin,
                user_uid: user_uid
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = book;
        result.message = '개별 도서 조회를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 개별 도서 조회 Exception`);

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