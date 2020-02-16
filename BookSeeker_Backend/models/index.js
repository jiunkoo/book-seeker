const Sequelize = require('sequelize');
const Temporal = require('sequelize-temporal');
const env = process.env.NODE_ENV || 'development';
const config = require(__dirname + '/../config/server_info.json')[env];
const db = {};

let sequelize;
if (config.use_env_variable) {
  sequelize = new Sequelize(process.env[config.use_env_variable], config);
} else {
  sequelize = new Sequelize(config.database, config.username, config.password, config);
}

db.sequelize = sequelize;
db.Sequelize = Sequelize;

// 테이블 객체 선언
db.User = require('./user')(sequelize, Sequelize);
db.Book = require('./book')(sequelize, Sequelize);
db.Rating = require('./rating')(sequelize, Sequelize);

// 임시 테이블로 사용할 테이블 객체 선언
db.BookHistory = require('./bookhistory')(sequelize, Sequelize);

// 임시 테이블 선언
// Temporal(db.Book, sequelize);

// Table 객체 관계맺기

module.exports = db;