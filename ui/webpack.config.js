const path = require('path');
const HtmlWebPackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

 const htmlPlugin = new HtmlWebPackPlugin({
    template: "./src/index.html",
    filename: "./index.html"
});

const miniCssExtractPlugin = new MiniCssExtractPlugin({
    filename: '[name].[contenthash:4].css',
});

 module.exports = {
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: "babel-loader"
                }
            },
            {
                test:/\.css$/,
                use:['style-loader', MiniCssExtractPlugin.loader, 'css-loader']
            },
            {
                test: /\.(jpg|png)$/,
                use: {
                  loader: 'url-loader',
                },
            },
        ]
    },
    plugins: [htmlPlugin, miniCssExtractPlugin],
    output: {
        filename: '[name].[contenthash].js',
        path: path.resolve(__dirname, 'dist')
    },
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:5000'
            },
            '/login': {
                target: 'http://localhost:5000'
            },
            '/oauth2': {
                target: 'http://localhost:5000'
            }
        }
    }
};