module.exports = (function () {
  'use strict'

  const debug = require('debug')('toffel:server')
  const validator = require('./validator.js')

  const Database = require('./database.js')
  const dbName = process.env.NODE_ENV === 'test' ? 'toffelTest' : 'toffel'
  const db = new Database(dbName)

  const Koa = require('koa')
  const router = require('koa-router')()
  const koaBody = require('koa-body')()
  const app = new Koa()

  const serverPort = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000

  app.use(router.routes())

  router.get('/highscore', async ctx => {
    ctx.body = await db.getHighscores()
  })

  router.post('/highscore', koaBody, async ctx => {
    const highscore = ctx.request.body

    if (validator.isValidHighscore(highscore)) {
      debug('Received request to save highscore', highscore)
      ctx.body = await db.saveHighscore(highscore)
    } else {
      debug('Received invalid highscore', highscore)
      ctx.status = 400
      ctx.body = 'Invalid highscore'
    }
  })

  app.listen(serverPort, function () {
    debug('Server listening on port ' + serverPort)
  })
})()
