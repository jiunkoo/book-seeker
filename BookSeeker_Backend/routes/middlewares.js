// 로그 생성 및 ip 확인
const winston = require('../config/winston');
const requestIp = require('request-ip');

// IP 주소 반환
exports.clientIp = (req, res, next) => {
    req.clientIp = requestIp.getClientIp(req); 
    next();
};

// 로그인 검사
exports.isLoggedIn = (req, res, next) => {
      if (req.isAuthenticated()) {
        winston.log('info', `[MIDDLEWWARE][${req.clientIp}|${req.body.email}] 로그인 상태입니다.`);
        next();
      } else {
        const result ={ 
          success: false,
          data: 'NONE',
          message: '로그인이 필요합니다.'
        }
        winston.log('info', `[MIDDLEWWARE][${req.clientIp}|${req.body.email}] ${result.message}`);
        res.status(200).send(result);
      }
    };

// 로그아웃 검사
exports.isNotLoggedIn = (req, res, next) => {
    if (!req.isAuthenticated()) { 
      winston.log('info', `[MIDDLEWWARE][${req.clientIp}|${req.body.email}] 로그아웃 상태입니다.`);
        next();
    } else {
      const result ={
        success: false,
        data: 'NONE',
        message: '로그아웃이 필요합니다.'
      }
      winston.log('info', `[MIDDLEWWARE][${req.clientIp}|${req.body.email}] ${result.message}`);
      res.status(200).send(result);
    }
};