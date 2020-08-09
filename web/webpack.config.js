/* eslint-disable no-console */
require('dotenv').config({ path: '../.env' });
const path = require('path');
const HtmlWebPackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
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

module.exports = () => {
  console.log('SPECTACULAR_GITHUB_APP_INSTALLATION_ID: ', process.env.SPECTACULAR_GITHUB_APP_INSTALLATION_ID);
  console.log('VERSION: ', process.env.SEMVER);
  console.log('SHORTSHA: ', process.env.SHORTSHA);

  return {
    module: {
      rules: [
        {
          enforce: 'pre',
          test: /\.(js|jsx|ts|tsx)$/,
          exclude: /node_modules/,
          use: {
            loader: 'eslint-loader',
          },
        },
        {
          test: /\.(js|jsx|ts|tsx)$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
          },
        },
        {
          test: /\.css$/,
          use: ['style-loader', MiniCssExtractPlugin.loader, 'css-loader'],
        },
        { // Load fonts
          test: /\.woff($|\?)|\.woff2($|\?)|\.ttf($|\?)|\.eot($|\?)|\.svg($|\?)/,
          use: 'url-loader',
        },
        { // Load other files, images etc
          test: /\.(png|j?g|gif|ico)?$/,
          use: 'url-loader',
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
    plugins: [definePlugin, htmlPlugin, miniCssExtractPlugin],
    output: {
      filename: '[name].[contenthash].js',
      path: path.resolve(__dirname, 'dist'),
      publicPath: '/',
    },
    devServer: {
      disableHostCheck: true,
      historyApiFallback: {
        disableDotRule: true,
      },
      proxy: {
        '/api': {
          target: 'http://localhost:5000', // actual api
          // target: 'http://localhost:5005', //wiremock
          pathRewrite: { '^/api': '' },
          headers: {
            'x-spec-installation-id': process.env.SPECTACULAR_GITHUB_APP_INSTALLATION_ID,
          },
        },
        '/login': {
          target: 'http://localhost:5001',
        },
      },
    },
  };
};
