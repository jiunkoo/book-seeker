// 웹 프레임워크
const express = require('express');

// 인증
const passport = require('passport');
const passportConfig = require('./passport');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const flash = require('connect-flash');

// 프로미스 기반 ORM(Objective-Relational Mapping)
const { sequelize } = require('./models');

// 에러와 로그 생성
const createError = require('http-errors');
const winston = require('./config/winston');
// const path = require('path');

// 보안 강화
const hpp = require('hpp');
const helmet = require('helmet');

// 멀티코어 서버 구성
const cluster = require('cluster');
const os = require('os');
const uuid = require('uuid');

// 설정
require('dotenv').config();

// 서버 uuid 생성
const instance_id = uuid.v4();

// 워커 생성(cpu/2)
// const cpuCount = os.cpus().length;
const cpuCount = 1;
const workerCount = cpuCount / 2;

// 클러스터가 마스터인 경우
if (cluster.isMaster) {
  winston.log('info', '[SERVER] SERVER_ID : ' + instance_id);
  winston.log('info', '[SERVER] SERVER_CPU : ' + cpuCount + ', WORKER_COUNT : ' + workerCount);

  let worker_id;
  let worker;

  // 워커 메세지 리스너
  const workerMsgListener = function (msg) {
    worker_id = msg.worker_id;

    // 마스터 uuid 요청
    if (msg.cmd === 'MASTER_ID') {
      cluster.workers[worker_id].send({ cmd: 'MASTER_ID', master_id: instance_id });
    }
  }

  // cpu 개수만큼 워커 생성 및 HTTPS 설정 적용
  for (let i = 0; i < workerCount; i++) {
    winston.log('info', "[SERVER] 워커 생성 - [" + (i + 1) + "/" + workerCount + "]");

    let worker = cluster.fork();

    worker.on('message', workerMsgListener);
  }

  // 워커가 온라인인 경우
  cluster.on('online', function (worker) {
    winston.log('info', '[SERVER] 워커 실행 중 - WORKER_ID : [' + worker.process.pid + ']');
  });

  // 워커가 오프라인인 경우
  // 워커를 생성 및 HTTPS 설정을 적용하고 워커 요청 메세지 받음
  cluster.on('exit', function (worker) {
    winston.log('info', '[SERVER] 워커 실행 중단 - WORKER_ID : [' + worker.process.pid + ']');

    worker = cluster.fork();

    worker.on('message', workerMsgListener);
  });
}
// 클러스터가 워커인 경우
else {
  // 마스터와 워커 아이디 생성
  let worker_id = cluster.worker.id;
  let master_id;

  // 마스터에게 id 요청
  process.send({ worker_id: worker_id, cmd: 'MASTER_ID' });
  process.on('message', function (msg) {
    if (msg.cmd === 'MASTER_ID') {
      master_id = msg.master_id;
    }
  }).on('unhandledRejection', (reason, p) => {
    winston.log('error', '[SERVER][Unhandled Rejection at Promise] ', reason, p);
  }).on('uncaughtException', (error) => {
    try {
      // 에러 발생 : 3초 안에 프로세스 종료
      const killtimer = setTimeout(function () {
        process.exit(1);
      }, 3000);

      // setTimeout을 현재 프로세스와 독립적으로 작동하도록 레퍼런스 제거
      killtimer.unref();

      // 워커가 죽었다는 걸 마스터에게 알림
      cluster.worker.disconnect();

      // 에러 로그 작성
      winston.log('error', '[SERVER][Uncaught Exception thrown] ' + error.stack);
    } catch (error) {
      winston.log('error', '[SERVER] 프로세스 종료 과정에서 에러 발생 ' + error.stack);
    }
  });

  // 필요한 설정 선언
  const app = express();
  const production = process.env.NODE_ENV === 'production';

  // 라우터 객체 생성
  const adminRouter = require('./routes/admin');
  const usersRouter = require('./routes/users');
  const booksRouter = require('./routes/books');
  const evaluationRouter = require('./routes/evaluation');
  const recommendRouter = require('./routes/recommend');

  // sequelize 동기화
  sequelize.sync();

  // passport 설정
  passportConfig(passport);

  // HTTPS 설정
  require('greenlock-express')
  .init({
    packageRoot: __dirname,
    configDir: process.env.HTTPS_CONFIGDIR,
    maintainerEmail: process.env.DOMAIN_EMAIL,
    cluster: false
  }).serve(httpsServer);

  // 포트 설정
  app.set('port', process.env.PORT || 3000);

  // 프로덕션 모드인 경우 보안 강화
  if (production) {
    app.use(hpp());
    app.use(helmet());
  }

  // 기본 설정
  app.use(express.json());
  app.use(express.urlencoded({ extended: false }));
  app.use(cookieParser(process.env.COOKIE_SECRET));

  // 세션 암호화
  app.use(session({
    resave: false,
    saveUninitialized: false,
    secret: process.env.COOKIE_SECRET,
    cookie: {
      httpOnly: true,
      secure: false,
      domain: production && '.hexanovem.com',
    },
  }));
  // app.use(express.static(path.join(__dirname, 'public')));

  // passport 설정 초기화 및 세션 사용
  app.use(flash());
  app.use(passport.initialize());
  app.use(passport.session());

  // express에 라우터 연결
  app.use('/admin', adminRouter);
  app.use('/users', usersRouter);
  app.use('/books', booksRouter);
  app.use('/evaluation', evaluationRouter);
  app.use('/recommend', recommendRouter);

  // 서버 상태 확인
  app.use('/', function(req, res) {
    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.end('[SERVER] BOOKSEEKER의 서버입니다.');
  });

  // 404 에러 생성
  app.use(function (req, res, next) {
    next(createError(404));
  });

  // 에러 핸들러
  app.use(function (err, req, res, next) {
    // Development일 때만 에러 메세지 남김
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // 에러 로그 기록
    winston.log('error', err.stack);

    // 에러 페이지 렌더링
    res.status(err.status || 500);
    res.render('error');
  });

  function httpsServer(glx) {
    glx.serveApp(app);
  }
 
  if(require.main === module) {
    // HTTPS 서버 실행
    app.listen(3000);
  }

  module.exports = app;
}
