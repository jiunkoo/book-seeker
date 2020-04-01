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
        let searchQuery =
            'SELECT * ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid AND bsin=:bsin; ';

        const evaluation = await sequelize.query(searchQuery, {
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
            let updateQuery =
                'UPDATE evaluations ' +
                'SET rating=:rating, state=:state, deletedAt=null ' +
                'WHERE user_uid=:user_uid AND bsin=:bsin; ';

            await sequelize.query(updateQuery, {
                replacements: {
                    user_uid: user_uid,
                    bsin: bsin,
                    rating: rating,
                    state: state
                },
                type: Sequelize.QueryTypes.UPDATE,
                raw: true
            });

            // 도서 평균 평점, 인원 수 조회
            let averageQuery =
                'SELECT IFNULL(AVG(rating), 0) as average, COUNT(bsin) AS count ' +
                'FROM evaluations ' +
                'WHERE bsin=:bsin ' +
                'AND rating > 0 ' +
                'AND deletedAt IS NULL';

            const average = await sequelize.query(averageQuery, {
                replacements: {
                    bsin: bsin
                },
                type: Sequelize.QueryTypes.SELECT,
                raw: true
            });

            console.log(average);

            const returnData = new Object();
            returnData.bsin = bsin;
            returnData.rating = rating;
            returnData.state = state;
            returnData.average = average[0].average;
            returnData.count = average[0].count;

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
            await Evaluation.create({
                user_uid: user_uid,
                bsin: bsin,
                genre: genre,
                rating: rating,
                state: state
            });

            // 도서 평균 평점, 인원 수 조회
            let averageQuery =
                'SELECT IFNULL(AVG(rating), 0) as average, COUNT(bsin) AS count ' +
                'FROM evaluations ' +
                'WHERE bsin=:bsin ' +
                'AND rating > 0 ' +
                'AND deletedAt IS NULL';

            const average = await sequelize.query(averageQuery, {
                replacements: {
                    bsin: bsin
                },
                type: Sequelize.QueryTypes.SELECT,
                raw: true
            });

            const returnData = new Object();
            returnData.bsin = bsin;
            returnData.rating = rating;
            returnData.state = state;
            returnData.average = average[0].average;
            returnData.count = average[0].count;

            // 도서 평가 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = returnData;
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
router.get('/:genre/:state/:page/:limit', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        const genre = req.params.genre;
        const state = parseInt(req.params.state)-1;
        const page = parseInt(req.params.page);
        const limit = parseInt(req.params.limit);

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 전체 평가 도서 목록 Request`);
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}]  genre: ${genre},  state : ${state}, page : ${page}, limit : ${limit}`);

        let offset = 0;

        if (page > 1) {
            offset = 10 * (page - 1);
        }

        // 삭제 데이터를 제외한 전체 도서 평가 목록 불러오기
        let query =
            'SELECT b.*, e.rating ' +
            'FROM books AS b, evaluations AS e ' +
            'WHERE b.bsin = e.bsin ' +
            'AND e.user_uid=:user_uid ' +
            'AND e.genre = :genre ' +
            'AND e.state = :state ' +
            'AND e.deletedAt IS NULL ' +
            'ORDER BY rating DESC ' +
            'LIMIT :limit ' +
            'OFFSET :offset;';

        const bookList = await sequelize.query(query, {
            replacements: {
                user_uid: user_uid,
                genre: genre,
                state: state,
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

// 장르별 도서 평가 개수 조회
router.get('/count/genre', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 장르별 도서 평가 개수 조회 Request`);

        let countQuery =
            'SELECT ' +
            'COUNT(IF (genre="COMIC", genre, NULL)) AS count_comic, ' +
            'COUNT(IF (genre="ROMANCE", genre, NULL)) AS count_romance, ' +
            'COUNT(IF (genre="FANTASY", genre, NULL)) AS count_fantasy ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid ' +
            'AND deletedAt IS NULL;';
        
        const countGenre = await sequelize.query(countQuery, {
            replacements: {
                user_uid: user_uid
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        returnData = new Object();
        returnData.count_comic = countGenre[0].count_comic;
        returnData.count_romance = countGenre[0].count_romance;
        returnData.count_fantasy = countGenre[0].count_fantasy;

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
        result.message = '장르별 도서 평가 개수 조회를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 장르별 도서 평가 개수 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 상태별 도서 평가 개수 조회
router.get('/count/state', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 상태별 도서 평가 개수 조회 Request`);

        let countQuery =
            'SELECT IFNULL(genre, "COMIC") AS genre, ' +
            'COUNT(IF(state=-1, state, NULL)) AS count_nothing, ' +
            'COUNT(IF(state=0, state, NULL)) AS count_boring, ' +
            'COUNT(IF(state=1, state, NULL)) AS count_interesting, ' +
            'COUNT(IF(state=2, state, NULL)) AS count_reading, ' +
            'COUNT(IF(state=3, state, NULL)) AS count_read ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid ' +
            'AND genre="COMIC" ' +
            'AND deletedAt IS NULL ' +
            'UNION ' +
            'SELECT IFNULL(genre, "ROMANCE") AS genre, ' +
            'COUNT(IF(state=-1, state, NULL)) AS count_nothing, ' +
            'COUNT(IF(state=0, state, NULL)) AS count_boring, ' +
            'COUNT(IF(state=1, state, NULL)) AS count_interesting, ' +
            'COUNT(IF(state=2, state, NULL)) AS count_reading, ' +
            'COUNT(IF(state=3, state, NULL)) AS count_read ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid ' +
            'AND genre="ROMANCE" ' +
            'AND deletedAt IS NULL ' +
            'UNION ' +
            'SELECT IFNULL(genre, "FANTASY") AS genre, ' +
            'COUNT(IF(state=-1, state, NULL)) AS count_nothing, ' +
            'COUNT(IF(state=0, state, NULL)) AS count_boring, ' +
            'COUNT(IF(state=1, state, NULL)) AS count_interesting, ' +
            'COUNT(IF(state=2, state, NULL)) AS count_reading, ' +
            'COUNT(IF(state=3, state, NULL)) AS count_read ' +
            'FROM evaluations ' +
            'WHERE user_uid=:user_uid ' +
            'AND genre="FANTASY" ' +
            'AND deletedAt IS NULL;';
        

        const countState = await sequelize.query(countQuery, {
            replacements: {
                user_uid: user_uid
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        returnData = new Object();
        returnData.comic_nothing = countState[0].count_nothing;
        returnData.comic_boring = countState[0].count_boring;
        returnData.comic_interesting = countState[0].count_interesting;
        returnData.comic_reading = countState[0].count_reading;
        returnData.comic_read = countState[0].count_read;

        returnData.romance_nothing = countState[1].count_nothing;
        returnData.romance_boring = countState[1].count_boring;
        returnData.romance_interesting = countState[1].count_interesting;
        returnData.romance_reading = countState[1].count_reading;
        returnData.romance_read = countState[1].count_read;

        returnData.fantasy_nothing = countState[2].count_nothing;
        returnData.fantasy_boring = countState[2].count_boring;
        returnData.fantasy_interesting = countState[2].count_interesting;
        returnData.fantasy_reading = countState[2].count_reading;
        returnData.fantasy_read = countState[2].count_read;

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
        result.message = '상태별 도서 평가 개수 조회를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 상태별 도서 평가 개수 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 평점별 도서 평가 개수 조회
router.get('/count/rating', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        const user_uid = req.user.user_uid;

        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] 평점별 도서 평가 개수 조회 Request`);

        let countQuery =
        'SELECT ' +
        'COUNT(IF(rating=0.5, rating, NULL)) AS "rating_05", ' +
        'COUNT(IF(rating=1.0, rating, NULL)) AS "rating_10", ' +
        'COUNT(IF(rating=1.5, rating, NULL)) AS "rating_15", ' +
        'COUNT(IF(rating=2.0, rating, NULL)) AS "rating_20", ' +
        'COUNT(IF(rating=2.5, rating, NULL)) AS "rating_25", ' +
        'COUNT(IF(rating=3.0, rating, NULL)) AS "rating_30", ' +
        'COUNT(IF(rating=3.5, rating, NULL)) AS "rating_35", ' +
        'COUNT(IF(rating=4.0, rating, NULL)) AS "rating_40", ' +
        'COUNT(IF(rating=4.5, rating, NULL)) AS "rating_45", ' +
        'COUNT(IF(rating=5.0, rating, NULL)) AS "rating_50" ' +
        'FROM evaluations ' +
        'WHERE user_uid=:user_uid ' +
        'AND rating > 0 ' +
        'AND deletedAt IS NULL;';
        

        const countRating = await sequelize.query(countQuery, {
            replacements: {
                user_uid: user_uid
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        returnData = new Object();
        returnData.rating_05 = countRating[0].rating_05;
        returnData.rating_10 = countRating[0].rating_10;
        returnData.rating_15 = countRating[0].rating_15;
        returnData.rating_20 = countRating[0].rating_20;
        returnData.rating_25 = countRating[0].rating_25;
        returnData.rating_30 = countRating[0].rating_30;
        returnData.rating_35 = countRating[0].rating_35;
        returnData.rating_40 = countRating[0].rating_40;
        returnData.rating_45 = countRating[0].rating_45;
        returnData.rating_50 = countRating[0].rating_50;

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
        result.message = '상태별 도서 평가 개수 조회를 성공했습니다.';
        winston.log('info', `[EVALUATION][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[EVALUATION][${req.clientIp}|${req.user.email}] 상태별 도서 평가 개수 조회 Exception`);

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

        // 도서 평균 평점, 인원 수 조회
        let averageQuery =
            'SELECT IFNULL(AVG(rating), 0) as average, COUNT(bsin) AS count ' +
            'FROM evaluations ' +
            'WHERE bsin=:bsin ' +
            'AND rating > 0 ' +
            'AND deletedAt IS NULL';

        const average = await sequelize.query(averageQuery, {
            replacements: {
                bsin: bsin
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        const returnData = new Object();
        returnData.bsin = bsin;
        returnData.rating = rating;
        returnData.state = state;
        returnData.average = average[0].average;
        returnData.count = average[0].count;

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

        // 도서 평가 변경
        await Evaluation.update({
            rating: -1
        }, {
            where: {
                user_uid: user_uid,
                bsin: bsin
            }
        });

        // 도서 평가 삭제
        await Evaluation.destroy({
            where: {
                user_uid: user_uid,
                bsin: bsin
            }
        });

        // 도서 평균 평점, 인원 수 조회
        let averageQuery =
            'SELECT IFNULL(AVG(rating), 0) as average, COUNT(bsin) AS count ' +
            'FROM evaluations ' +
            'WHERE bsin=:bsin ' +
            'AND rating > 0 ' +
            'AND deletedAt IS NULL';

        const average = await sequelize.query(averageQuery, {
            replacements: {
                bsin: bsin
            },
            type: Sequelize.QueryTypes.SELECT,
            raw: true
        });

        const returnData = new Object();
        returnData.bsin = bsin;
        returnData.rating = -1;
        returnData.state = -1;
        returnData.average = average[0].average;
        returnData.count = average[0].count;

        // 도서 검색 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = returnData;
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