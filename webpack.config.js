const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const { DefinePlugin } = require('webpack');
const dotenv = require('dotenv');

// Load environment variables from .env file
const env = dotenv.config().parsed || {};
const envKeys = Object.keys(env).reduce((prev, next) => {
  prev[`process.env.${next}`] = JSON.stringify(env[next]);
  return prev;
}, {});

module.exports = (env, argv) => {
  const isProduction = argv.mode === 'production';

  return {
    mode: isProduction ? 'production' : 'development',
    entry: './src/main/resources/static/js/app.js',
    output: {
      path: path.resolve(__dirname, 'src/main/resources/static/dist'),
      filename: 'js/[name].[contenthash].js',
      publicPath: '/'
    },
    module: {
      rules: [
        {
          test: /\.vue$/,
          loader: 'vue-loader'
        },
        {
          test: /\.js$/,
          exclude: /node_modules/,
          use: {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env']
            }
          }
        },
        {
          test: /\.css$/,
          use: [
            isProduction ? MiniCssExtractPlugin.loader : 'vue-style-loader',
            'css-loader'
          ]
        },
        {
          test: /\.(png|jpe?g|gif|svg)$/i,
          type: 'asset/resource',
          generator: {
            filename: 'img/[name].[hash][ext]'
          }
        },
        {
          test: /\.(woff|woff2|eot|ttf|otf)$/i,
          type: 'asset/resource',
          generator: {
            filename: 'fonts/[name].[hash][ext]'
          }
        }
      ]
    },
    resolve: {
      extensions: ['.js', '.vue', '.json'],
      alias: {
        '@': path.resolve(__dirname, 'src/main/resources/static/js'),
        'vue$': 'vue/dist/vue.esm.js',
        'img': path.resolve(__dirname, 'src/main/resources/static/img')
      }
    },
    plugins: [
      new VueLoaderPlugin(),
      new CleanWebpackPlugin(),
      new MiniCssExtractPlugin({
        filename: 'css/[name].[contenthash].css'
      }),
      new CopyWebpackPlugin({
        patterns: [
          {
            from: 'src/main/resources/static/img',
            to: 'img',
            noErrorOnMissing: true
          }
        ]
      }),
      new DefinePlugin({
        'process.env.NODE_ENV': JSON.stringify(isProduction ? 'production' : 'development'),
        'process.env.VUE_APP_API_URL': JSON.stringify(process.env.VUE_APP_API_URL || ''),
        'process.env.VUE_APP_STRIPE_PUBLIC_KEY': JSON.stringify(process.env.VUE_APP_STRIPE_PUBLIC_KEY || ''),
        ...envKeys
      })
    ],
    optimization: {
      splitChunks: {
        chunks: 'all',
        name: 'vendors'
      }
    },
    devtool: isProduction ? 'source-map' : 'eval-cheap-module-source-map',
    devServer: {
      static: {
        directory: path.join(__dirname, 'src/main/resources/static')
      },
      historyApiFallback: true,
      hot: true,
      port: 8081,
      proxy: {
        '/api/v1': 'http://localhost:8080'
      }
    },
    performance: {
      hints: isProduction ? 'warning' : false,
      maxEntrypointSize: 512000,
      maxAssetSize: 512000
    }
  };
}; 