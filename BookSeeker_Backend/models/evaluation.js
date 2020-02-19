// Mapping Rating Table
module.exports = (sequelize, DataTypes) => (
    sequelize.define('evaluation', {
        user_uid: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
        bsin: {
            type: DataTypes.STRING(100),
            allowNull: false,
        }, 
        genre: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        rating: {
            type: DataTypes.FLOAT,
            allowNull: false,
        },
        state: {
            type: DataTypes.INTEGER,
            allowNull: true,
        }
    }, {
        timestamps: true, // 생성&수정일 기록
        paranoid: true
    })
);
