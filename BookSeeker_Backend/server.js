"use strict";

// 설정
require('dotenv').config();

// 멀티코어 서버 구성
const os = require('os');

// 실행파일 삽입
const app = require("./app.js");

// 워커 생성(cpu/2)
// const cpuCount = os.cpus().length;
const cpuCount = 1;
const workerCount = cpuCount / 2;

// HTTPS 설정(greenlock v4)
require("greenlock-express").init({
    packageRoot: __dirname,
    configDir: process.env.HTTPS_CONFIGDIR,
    maintainerEmail: process.env.DOMAIN_EMAIL,
    cluster: true,
    workers: workerCount
}).serve(app);