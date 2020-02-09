// 인증
const localStrategy = require('./localStrategy');

// 모델 선언
const User = require('../models');

// 로그 생성
const winston = require('../config/winston');


module.exports = (passport) => {
    passport.serializeUser((user, done) => {
        winston.log('info', '[PASSPORT] SerializeUser 호출');
        done(null, user.user_uid);
    });

    passport.deserializeUser(async (id, done) => {
        try {
            winston.log('info', '[PASSPORT] DeserializeUser 호출');

            // 사용자 정보 불러옴
            const user = await User.findOne({
                where: { user_uid: id },
            });

            return done(null, user);
        } catch (e) {
            return done(e);
        }
    });
    localStrategy(passport);
}