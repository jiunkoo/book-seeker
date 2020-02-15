// 웹 프레임워크
const express = require('express');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const Sequelize = require('sequelize');
const Op = Sequelize.Op;

// 모델 및 미들웨어 선언
const { Book } = require('../models');
const { clientIp, isLoggedIn } = require('./middlewares');

// 로그 생성
const winston = require('../config/winston');

//라우터
const router = express.Router();


// 전체 도서 목록 조회
router.post('/:filter/:page/:limit', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        
        const filter = parseInt(req.params.filter);
        const page = parseInt(req.params.page);
        const limit = parseInt(req.params.limit);
        const keyword = req.body.keyword;

        winston.log('info', `[BOOK][${req.clientIp}|${user_email}]  전체 도서 목록 조회 Request`);
        winston.log('info', `[BOOK][${req.clientIp}|${user_email}]  filter : ${filter}, keyword : ${keyword}, page : ${page}, limit : ${limit}`);

        let offset = 0;
        let order = 'rand()';

        if(page > 1) {
            offset = 10 * (page -1);
        }

        // 필터에 따라 정렬 기준 변경
        if(filter == 0){
            order = 'rand()';
        }else if (filter == 1) {
            order = 'publication_date ASC';
        } else if (filter == 2) {
            order = 'publication_date DESC';
        } else if (filter == 3) {
            order = 'title ASC';
        }else {
            order = 'title DESC';
        }

        // 페이징 적용
        const bookList = await Book.findAll({
            offset: offset,
            limit: limit,
            order: Sequelize.literal(order),
            where: {
                title: {
                    [Op.like]: "%" + keyword + "%"
                }
            }
        });

        // 전체 도서 목록 조회 성공 메세지 반환
        const result = new Object();
        result.success = true;
        result.data = bookList;
        result.message = '전체 도서 목록 조회를 성공했습니다.';
        winston.log('info', `[BOOK][${req.clientIp}|${user_email}] ${result.message}`);
        return res.status(200).send(result);
    } catch (e) {
        winston.log('error', `[BOOK][${req.clientIp}|${req.body.email}] 전체 도서 목록 조회 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[BOOK][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});


module.exports = router;