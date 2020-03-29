//Mapping User Table
module.exports = (sequelize, DataTypes) => (
    sequelize.define('user', {
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
        }
    }, {
        timestamps: true, // 생성&수정일 기록
        paranoid: true, // 삭제일 기록
    })
);