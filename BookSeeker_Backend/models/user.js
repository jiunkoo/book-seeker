//Mapping User Table
module.exports = (sequelize, DataTypes) => (
    sequelize.define('user', {
        user_uid: {
            type: DataTypes.STRING,
            allowNull: false,
            unique: true,
            primaryKey: true,
        }, 
        email: {
            type: DataTypes.STRING(50),
            allowNull: false,
            unique: true,
        }, 
        nickname: {
            type: DataTypes.STRING(20), 
            allowNull: true,
        }, 
        password: {
            type: DataTypes.STRING,
            allowNull: true, 
        }, 
        tutorial: {
            type: DataTypes.BOOLEAN,
            allowNull: true,
        }
    }, {
        timestamps: true, // 생성&수정일 기록
        paranoid: true, // 삭제일 기록
    })
);