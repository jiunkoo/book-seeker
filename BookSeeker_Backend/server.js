"use strict";

// app.js
const app = require('./app.js');

// 설정
require('dotenv').config();

// HTTPS 설정
require('greenlock-express')
.init({
    packageRoot: __dirname,
    configDir: HTTPS_CONFIGDIR,
    maintainerEmail: process.env.DOMAIN_EMAIL,
    cluster: false
}).serve(app);