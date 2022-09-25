/* eslint-disable no-console */
require('dotenv').config({ path: '../.env' });
const path = require('path');
const HtmlWebPackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ESLintPlugin = require('eslint-webpack-plugin');
const webpack = require('webpack');

const htmlPlugin = new HtmlWebPackPlugin({
  template: './src/index.html',
  filename: './index.html',
  favicon: './src/assets/images/spectacular-icon.png',
});

const miniCssExtractPlugin = new MiniCssExtractPlugin({
  filename: '[name].[contenthash:4].css',
});

const definePlugin = new webpack.DefinePlugin({
  VERSION: JSON.stringify(process.env.SEMVER),
  SHORTSHA: JSON.stringify(process.env.SHORTSHA),
});

const esLintPlugin = new ESLintPlugin({
  extensions: ['js', 'jsx', 'ts', 'tsx'],
  failOnWarning: false
});

module.exports = () => {
  console.log('VERSION: ', process.env.SEMVER);
  console.log('SHORTSHA: ', process.env.SHORTSHA);

  return {
    module: {
      rules: [
        {
          test: /\.(js|jsx|ts|tsx)$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
          },
        },
        {
          test: /\.css$/,
          use: [MiniCssExtractPlugin.loader, 'css-loader'],
        },
        { // Load fonts
          test: /\.woff($|\?)|\.woff2($|\?)|\.ttf($|\?)|\.eot($|\?)|\.svg($|\?)/,
          type: 'asset/resource',
        },
        { // Load other files, images etc
          test: /\.(png|j?g|gif|ico)?$/,
          type: 'asset/resource',
        },
        {
          test: /\.less$/,
          use: [
            {
              loader: MiniCssExtractPlugin.loader,
            },
            'css-loader',
            'less-loader',
          ],
        },
      ],
    },
    resolve: {
      extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
      alias: {
        '../../theme.config$': path.join(
          __dirname,
          'my-custom-semantic-theme/theme.config',
        ),
      },
    },
    plugins: [definePlugin, htmlPlugin, miniCssExtractPlugin, esLintPlugin],
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      publicPath: '/',
    },
    devServer: {
      allowedHosts: 'all',
      historyApiFallback: {
        disableDotRule: true,
      },
      proxy: {
        '/api': {
          target: 'http://localhost:5000', // actual api
          // target: 'http://localhost:5005', //wiremock
          pathRewrite: { '^/api': '' },
        },
      },
    },
  };
};
