// 인증
const LocalStrategy = require('passport-local').Strategy;
const bcrypt = require('bcrypt');

// 모델 선언
const { User } = require('../models');

module.exports = (passport) => {
    passport.use(new LocalStrategy({
        usernameField: 'email',
        passwordField: 'password',
        session: true, // 세션 저장
    }, async (email, password, done) => {
        try {
            // 사용자 조회
            const user = await User.findOne({
                where: {
                    email:email
                }
            });

            // 만일 사용자가 존재하는 경우
            if (user) {
                // 비밀번호 비교
                const comparePassword = await bcrypt.compare(password, user.password);

                // 비밀번호가 일치하는 경우
                if (comparePassword) {
                    return done(null, user);
                }
                // 비밀번호가 일치하지 않는 경우 
                else {
                    return done(null, false, { message: '비밀번호가 일치하지 않습니다.' });
                }
            }
            // 사용자가 존재하지 않는 경우 
            else {
                return done(null, false, { message: '가입되지 않은 회원입니다.' });
            }
        } catch (error) {
            return done(error);
        }
    }));

};