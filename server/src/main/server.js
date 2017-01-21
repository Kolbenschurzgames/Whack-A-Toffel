module.exports = (function () {
  'use strict'

  var debug = require('debug')('toffel:server')
  var validator = require('./validator.js')

  var Database = require('./database.js')
  var dbName = process.env.NODE_ENV === 'test' ? 'toffelTest' : 'toffel'
  var db = new Database(dbName)

  var koa = require('koa')
  var router = require('koa-router')()
  var koaBody = require('koa-body')()
  var app = koa()

  var serverPort = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000

  app.use(router.routes())

  router.get('/highscore', function* () {
    this.body = yield db.getHighscores()
  })

  router.post('/highscore', koaBody, function* () {
    var highscore = this.request.body

    if (validator.isValidHighscore(highscore)) {
      debug('Received request to save highscore', highscore)
      this.body = yield db.saveHighscore(highscore)
    } else {
      debug('Received invalid highscore', highscore)
      this.response.status = 400
      this.body = 'Invalid highscore'
    }
  })

  app.listen(serverPort, function () {
    debug('Server listening on port ' + serverPort)
  })
})()
