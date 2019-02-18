'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  dev: {
    proxyTable: {
      // proxy all requests starting with /api to jsonplaceholder
      '/': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
