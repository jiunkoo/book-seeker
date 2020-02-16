// Mapping Book Table
module.exports = (sequelize, DataTypes) => (
    sequelize.define('book', {
        bsin: {
            type: DataTypes.STRING(100),
            allowNull: false,
            unique: true,
        },
        title: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        author: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
        publisher: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
        introduction: {
            type: DataTypes.TEXT,
            allowNull: false,
        },
        cover: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        link: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        keyword: {
            type: DataTypes.TEXT,
            allowNull: false,
        },
        adult: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        genre: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        publication_date: {
            type: DataTypes.STRING,
            allowNull: false,
        }
    }, {
        timestamps: true, // 생성&수정일 기록
        paranoid: true, // 삭제일 기록
    })
);