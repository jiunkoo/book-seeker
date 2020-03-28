// 웹 프레임워크
const express = require('express');

// 인증
const bcrypt = require('bcrypt');

// 모델 및 미들웨어 선언
const { User, Book, Rating } = require('../models');
const { clientIp, isLoggedIn } = require('./middlewares');

// 파일 시스템 접근
const fs = require('fs');
const path = require('path');

// 로그 생성
const winston = require('../config/winston');

//라우터
const router = express.Router();


// 도서 데이터 파싱
router.post('/parsing/book', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;
        winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] 도서 데이터 파싱`);

        // 관리자가 아닌 경우
        if (user_email != process.env.ADMIN) {
            // 접근 불가 메세지 반환
            const result = new Object();
            result.success = false;
            result.data = 'NONE';
            result.message = '관리자만 접근할 수 있습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        } else {
            // json 데이터 불러오기
            const comicData = fs.readFileSync(path.join(__dirname, '../data/comic_data.json'), 'utf8');
            const romanceData = fs.readFileSync(path.join(__dirname, '../data/romance_data.json'), 'utf8');
            const fantasyData = fs.readFileSync(path.join(__dirname, '../data/fantasy_data.json'), 'utf8');

            const parsingComicData = JSON.parse(comicData);
            const parsingRomanceData = JSON.parse(romanceData);
            const parsingFantasyData = JSON.parse(fantasyData);

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingComicData.length; i++) {
                // 코믹스 데이터 생성
                await Book.create({
                    bsin: parsingComicData[i].cbin,
                    title: parsingComicData[i].title,
                    author: parsingComicData[i].author,
                    publisher: parsingComicData[i].publisher,
                    introduction: parsingComicData[i].introduction,
                    cover: parsingComicData[i].image,
                    link: parsingComicData[i].link,
                    keyword: parsingComicData[i].keyword,
                    adult: parsingComicData[i].adult,
                    genre: 'COMIC',
                    publication_date: parsingComicData[i].date,
                });
            }

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingRomanceData.length; i++) {
                // 로맨스 데이터 생성
                await Book.create({
                    bsin: parsingRomanceData[i].rbin,
                    title: parsingRomanceData[i].title,
                    author: parsingRomanceData[i].author,
                    publisher: parsingRomanceData[i].publisher,
                    introduction: parsingRomanceData[i].introduction,
                    cover: parsingRomanceData[i].image,
                    link: parsingRomanceData[i].link,
                    keyword: parsingRomanceData[i].keyword,
                    adult: parsingRomanceData[i].adult,
                    genre: 'ROMANCE',
                    publication_date: parsingRomanceData[i].date,
                });
            }

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingFantasyData.length; i++) {
                // 판타지 데이터 생성
                await Book.create({
                    bsin: parsingFantasyData[i].fbin,
                    title: parsingFantasyData[i].title,
                    author: parsingFantasyData[i].author,
                    publisher: parsingFantasyData[i].publisher,
                    introduction: parsingFantasyData[i].introduction,
                    cover: parsingFantasyData[i].image,
                    link: parsingFantasyData[i].link,
                    keyword: parsingFantasyData[i].keyword,
                    adult: parsingFantasyData[i].adult,
                    genre: 'FANTASY',
                    publication_date: parsingFantasyData[i].date,
                });
            }
            // 도서 데이터 파싱 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = 'NONE';
            result.message = '도서 데이터 파싱에 성공했습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(201).send(result);
        }
    } catch (e) {
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] 도서 데이터 파싱 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 사용자 데이터 파싱
router.post('/parsing/user', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;

        winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] 사용자 데이터 파싱`);

        // 관리자가 아닌 경우
        if (user_email != process.env.ADMIN) {
            // 접근 불가 메세지 반환
            const result = new Object();
            result.success = false;
            result.data = 'NONE';
            result.message = '관리자만 접근할 수 있습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        } else {
            // json 데이터 불러오기
            const userData = fs.readFileSync(path.join(__dirname, '../data/user_data.json'), 'utf8');

            const parsingUserData = JSON.parse(userData);

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingUserData.length; i++) {
                const user_uid = await bcrypt.hash(parsingUserData[i].email, 12);
                const encrypt_pw = await bcrypt.hash(parsingUserData[i].password, 12);

                // 사용자 평가 데이터 생성
                await User.create({
                    user_uid: user_uid,
                    email: parsingUserData[i].email,
                    nickname: parsingUserData[i].name,
                    password: encrypt_pw
                });
            }

            // 사용자 데이터 파싱 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = 'NONE';
            result.message = '사용자 데이터 파싱에 성공했습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(201).send(result);
        }
    } catch (e) {
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] 사용자 데이터 파싱 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

// 평가 데이터 파싱
router.post('/parsing/rating', clientIp, isLoggedIn, async (req, res, next) => {
    try {
        const user_email = req.user.email;

        winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] 평가 데이터 파싱`);

        // 관리자가 아닌 경우
        if (user_email != process.env.ADMIN) {
            // 접근 불가 메세지 반환
            const result = new Object();
            result.success = false;
            result.data = 'NONE';
            result.message = '관리자만 접근할 수 있습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(200).send(result);
        } else {
            // json 데이터 불러오기
            const comicRatingData = fs.readFileSync(path.join(__dirname, '../data/comic_rating_data.json'), 'utf8');
            const romanceRatingData = fs.readFileSync(path.join(__dirname, '../data/romance_rating_data.json'), 'utf8');
            const fantasyRatingData = fs.readFileSync(path.join(__dirname, '../data/fantasy_rating_data.json'), 'utf8');

            const parsingComicRatingData = JSON.parse(comicRatingData);
            const parsingRomanceRatingData = JSON.parse(romanceRatingData);
            const parsingFantasyRatingData = JSON.parse(fantasyRatingData);

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingComicRatingData.length; i++) {
                const user_uid = await bcrypt.hash(parsingComicRatingData[i].email, 12);

                // 코믹스 평가 데이터 생성
                await Rating.create({
                    user_uid: user_uid,
                    bsin: parsingComicRatingData[i].bsin,
                    genre: 'COMIC',
                    rating: parsingComicRatingData[i].rating
                });
            }

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingRomanceRatingData.length; i++) {
                const user_uid = await bcrypt.hash(parsingRomanceRatingData[i].email, 12);

                // 로맨스 평가 데이터 생성
                await Rating.create({
                    user_uid: user_uid,
                    bsin: parsingRomanceRatingData[i].bsin,
                    genre: 'ROMANCE',
                    rating: parsingRomanceRatingData[i].rating
                });
            }

            // 파일 길이만큼 반복문 돌기
            for (let i = 0; i < parsingFantasyRatingData.length; i++) {
                const user_uid = await bcrypt.hash(parsingFantasyRatingData[i].email, 12);

                // 판타지 평가 데이터 생성
                await Rating.create({
                    user_uid: user_uid,
                    bsin: parsingFantasyRatingData[i].bsin,
                    genre: 'FANTASY',
                    rating: parsingFantasyRatingData[i].rating
                });
            }

            // 도서 데이터 파싱 성공 메세지 반환
            const result = new Object();
            result.success = true;
            result.data = 'NONE';
            result.message = '도서 데이터 파싱에 성공했습니다.';
            winston.log('info', `[ADMIN][${req.clientIp}|${user_email}] ${result.message}`);
            return res.status(201).send(result);
        }
    } catch (e) {
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] 평가 데이터 파싱 Exception`);

        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = 'INTERNAL SERVER ERROR';
        winston.log('error', `[ADMIN][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(500).send(result);
        return next(e);
    }
});

module.exports = router;