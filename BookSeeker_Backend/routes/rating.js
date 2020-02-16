// 웹 프레임워크
const express = require('express');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const Sequelize = require('sequelize');
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/../config/server_info.json')[env];

// 모델 및 미들웨어 선언
const { Rating } = require('../models');
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

        const { bsin, genre, rating } = req.body;

        winston.log('info', `[RATING][${req.clientIp}|${user_email}] 도서 평가 Request`);
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] bsin : ${bsin}, genre : ${genre}, rating : ${rating}`);

        // 별점이 0점 미만이거나 5점을 초과하는 경우
        if (rating > 5 || rating < 0) {
            // 도서 평가 실패 메세지 반환
            const result = new Object();
            result.success = false;
            result.data = 'NONE';
            result.message = '별점이 잘못되었습니다.';
            winston.log('info', `[RATING][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        }

        // 평가 데이터 저장
        await Rating.create({
            user_uid: user_uid,
            bsin: bsin,
            genre: genre,
            rating: rating
        });

        // 도서 평가 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bookList;
        result.message = '도서 평가를 성공했습니다.';
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] 도서 평가 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 내가 평가한 전체 도서 목록 조회
router.get('/rating/:genre/:filter/:page/:limit', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const genre = req.parsms.genre;
        const filter = parseInt(req.params.filter);
        const page = parseInt(req.params.page);
        const limit = parseInt(req.params.limit);

        winston.log('info', `[RATING][${req.clientIp}|${user_email}] 내가 평가한 전체 도서 목록 Request`);
        winston.log('info', `[RATING][${req.clientIp}|${user_email}]  genre: ${genre}, filter : ${filter}, page : ${page}, limit : ${limit}`);

        let offset = 0;
        let order = 'publication_date ASC';

        if(page > 1) {
            offset = 10 * (page -1);
        }

        // 필터에 따라 정렬 기준 변경
        if (filter == 1) {
            order = 'publication_date ASC';
        } else if (filter == 2) {
            order = 'publication_date DESC';
        } else if (filter == 3) {
            order = 'title ASC';
        }else {
            order = 'title DESC';
        }

        // 삭제 데이터를 제외한 전체 도서 평가 목록 불러오기
        let query =
            'SELECT * ' +
            'FROM books ' +
            'WHERE genre=:genre AND ' +
            'bsin = (SELECT bsin FROM ratings WHERE user_uid = :user_uid AND deletedAt IS NULL) ' +
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
        result.message = '내가 평가한 전체 도서 목록 조회를 성공했습니다.';
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] 내가 평가한 전체 도서 목록 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 도서 평가 수정
router.patch('/', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const rating = req.body.rating;

        winston.log('info', `[RATING][${req.clientIp}|${user_email}] 도서 평가 수정 Request`);
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] rating : ${rating}`);

        // 도서 평가 수정
        await Rating.update({
            rating: rating,
            where: {
                user_uid: user_uid
            }
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bookList;
        result.message = '도서 평가 수정을 성공했습니다.';
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] 도서 평가 수정 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] ${result.message}`);
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

        winston.log('info', `[RATING][${req.clientIp}|${user_email}] 도서 평가 삭제 Request`);
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] bsin : ${bsin}`);

        // 도서 평가 삭제
        await Rating.destroy({
            where: {
                user_uid: user_uid,
                bsin: bsin
            }
        });

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bookList;
        result.message = '도서 평가 삭제를 성공했습니다.';
        winston.log('info', `[RATING][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] 도서 평가 삭제 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[RATING][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

module.exports = router;