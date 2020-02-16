// Mapping Book Table
module.exports = (sequelize, DataTypes) => (
    sequelize.define('bookhistory', {
        user_uid: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        title: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        bsin: {
            type: DataTypes.STRING(100),
            allowNull: false,
            unique: true       
        },
        genre: {
            type: DataTypes.STRING,
            allowNull: false,
        }
    })
);