// 웹 프레임워크
const express = require('express');

// 인증
const passport = require('passport');
const bcrypt = require('bcrypt');
// const crypto = require('crypto');
// const nodemailer = require('nodemailer');

// 모델 및 미들웨어 선언
const { User } = require('../models');
const { clientIp, isLoggedIn } = require('./middlewares');

// 로그 생성
const winston = require('../config/winston');

//라우터
const router = express.Router();


// 회원가입
router.post('/register', clientIp, async (req, res, next) => {
  try {
    const { email, nickname, password } = req.body;

    winston.log('info', `[USER][${req.clientIp}|${email}] 회원가입 Request`);
    winston.log('info', `[USER][${req.clientIp}|${email}] email : ${email}, nickname : ${nickname}, password : ${password}`);

    // 사용자 조회
    const user = await User.findOne({
      where: {
        email: email
      }
    });

    // 사용자 조회에 성공한 경우
    // 중복되는 이메일이므로 회원가입 실패
    if (user) {
      // 회원가입 실패 메세지 반환
      const result = new Object();
      result.success = false;
      result.data = 'NONE';
      result.message = '이미 가입한 이메일입니다.';
      winston.log('info', `[USER][${req.clientIp}|${email}] ${result.message}`);
      return res.status(200).send(result);
    }
    // 사용자 조회에 실패한 경우
    // 중복이 아니므로 회원가입
    else {
      // uuid 생성 및 비밀번호 암호화
      const user_uid = await bcrypt.hash(email, 12);
      const encrypt_pw = await bcrypt.hash(password, 12);

      // 새로운 사용자 생성
      await User.create({
        user_uid: user_uid,
        email: email,
        nickname: nickname,
        password: encrypt_pw
      });

      // 생성한 사용자 정보 저장 객체 생성
      const returnData = new Object();
      returnData.user_uid = user_uid;
      returnData.email = email;
      returnData.nickname = nickname;
      returnData.tutorial = false;

      // 회원가입 성공 메세지 반환
      const result = new Object();
      result.success = true;
      result.data = returnData;
      result.message = '회원 가입에 성공했습니다.';
      winston.log('info', `[USER][${req.clientIp}|${email}] ${result.message}`);
      return res.status(201).send(result);
    }
  } catch (e) {
    winston.log('error', `[USER][${req.clientIp}|${req.body.email}] 회원가입 Exception`);

    const result = new Object();
    result.success = false;
    result.data = 'NONE';
    result.message = 'INTERNAL SERVER ERROR';
    winston.log('error', `[USER][${req.clientIp}|${req.body.email}] ${result.message}`);
    res.status(500).send(result);
    return next(e);
  }
});

// 로그인
router.post('/login', clientIp, async (req, res, next) => {
  try {
    const { email, password } = req.body;

    winston.log('info', `[USER][${req.clientIp}|${email}] 로그인 Request`);
    winston.log('info', `[USER][${req.clientIp}|${email}] email : ${email}, password : ${password}`);

    // passport 인증 시스템 이용
    passport.authenticate('local', (authError, user, info) => {
      // 에러 or 성공 or 실패 세 가지 경우 중 하나 반환
      // 에러가 발생한 경우
      if (authError) {
        winston.log('error', `[USER][${req.clientIp}|${email}] ${authError}`);

        return next(authError);
      }
      // 실패한 경우
      if (!user) {
        // 로그인 실패 메세지 반환
        const result = new Object();
        result.success = false;
        result.data = 'NONE';
        result.message = info.message;
        winston.log('info', `[USER][${req.clientIp}|${email}] ${info.message}`);
        return res.status(200).send(result);
      }
      // 성공한 경우
      else {
        return req.login(user, (loginError) => {
          // 로그인 과정에서 에러가 발생한 경우
          if (loginError) {
            winston.log('error', `[USER][${req.clientIp}|${email}] ${loginError}`);

            return next(loginError);
          }
          else {
            // 정상적으로 로그인에 성공한 경우
            winston.log('debug', `[USER][${req.clientIp}|${email}] 로그인 성공`);
            winston.log('debug', `[USER][${req.clientIp}|${email}] 로그인 인증 여부 : ${req.isAuthenticated()}`);

            let json_user = {
              email: user.email,
              nickname: user.nickname,
              tutorial: user.tutorial,
            }

            // 로그인 한 사용자 데이터 리턴
            const result = new Object();
            result.success = true;
            result.data = json_user;
            result.message = '성공적으로 로그인했습니다.';
            winston.log('info', `[USER][${req.clientIp}|${email}] ${result.message}`);
            return res.status(200).send(result);
          }
        });
      }
    })(req, res, next); // 미들웨어 내의 미들웨어에는 (req, res, next)를 붙입니다.
  } catch (e) {
    winston.log('error', `[USER][${req.clientIp}|${req.body.email}] 로그인 Exception`);

    const result = new Object();
    result.success = false;
    result.data = 'NONE';
    result.message = 'INTERNAL SERVER ERROR';
    winston.log('error', `[USER][${req.clientIp}|${req.body.email}] ${result.message}`);
    res.status(500).send(result);
    return next(e);
  }
});

// 내 정보
router.get('/mine', clientIp, isLoggedIn, async (req, res, next) => {
  try {
    const user_email = req.user.email;

    winston.log('info', `[USER][${req.clientIp}|${user_email}] 내 정보 Request`);

    let json_user = {
      email: req.user.email,
      nickname: req.user.nickname,
      portrait: req.user.portrait,
      introduction: req.user.introduction,
      tutorial: req.user.tutorial
    }

    // 로그인 성공 메세지 리턴
    const result = new Object();
    result.success = true;
    result.data = json_user;
    result.message = '로그인 한 사용자의 데이터를 불러왔습니다.';
    winston.log('info', `[USER][${req.clientIp}|${user_email}] ${result.message}`);
    return res.status(200).send(result);
  } catch (e) {
    winston.log('error', `[USER][${req.clientIp}|${req.user.email}] 내 정보 Exception`);

    const result = new Object();
    result.success = false;
    result.data = 'NONE';
    result.message = 'INTERNAL SERVER ERROR';
    winston.log('error', `[USER][${req.clientIp}|${req.user.email}] ${result.message}`);
    res.status(500).send(result);
    return next(e);
  }
});

// 로그아웃
router.get('/logout', clientIp, isLoggedIn, (req, res) => {
  try {
    const user_email = req.user.email;

    winston.log('info', `[USER][${req.clientIp}|${user_email}] 로그아웃 Request`);

    // 로그아웃
    req.logout();

    // 로그아웃 성공 메세지 리턴
    const result = new Object();
    result.success = true;
    result.data = 'NONE';
    result.message = '로그아웃을 완료했습니다.';
    winston.log('info', `[USER][${req.clientIp}|${user_email}] ${result.message}`);
    return res.status(200).send(result);
  } catch (e) {
    winston.log('error', `[USER][${req.clientIp}|${req.user.email}] 로그아웃 Exception`);

    const result = new Object();
    result.success = false;
    result.data = 'NONE';
    result.message = 'INTERNAL SERVER ERROR';
    winston.log('error', `[USER][${req.clientIp}|${req.user.email}] ${result.message}`);
    res.status(500).send(result);
    return next(e);
  }
});

// 회원 탈퇴
router.delete('/unregister', clientIp, isLoggedIn, async (req, res, next) => {
  try {
    const user_email = req.user.email;

    winston.log('info', `[USER][${req.clientIp}|${user_email}] 회원 탈퇴 Request`);

    // 회원 탈퇴 작성 필요

  } catch (e) {
    winston.log('error', `[USER][${req.clientIp}|${req.user.email}] 회원 탈퇴 Exception`);

    const result = new Object();
    result.success = false;
    result.data = 'NONE';
    result.message = 'INTERNAL SERVER ERROR';
    winston.log('error', `[USER][${req.clientIp}|${req.body.email}] ${result.message}`);
    res.status(500).send(result);
    return next(e);
  }
});

module.exports = router;