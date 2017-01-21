module.exports = (function () {
  'use strict'

  let db
  const HIGHSCORES = 'highscores'

  const debug = require('debug')('toffel:database')

  const Database = function (dbName) {
    let mongoDbUri

    if (typeof dbName === 'string') {
      mongoDbUri = process.env.MONGOLAB_URI || 'mongodb://localhost:27017/' + dbName
      db = require('mongoskin').db(mongoDbUri, { native_parser: true })
    } else {
      throw new Error('Database name parameter missing')
    }
  }

  Database.prototype.getHighscores = async function () {
    return new Promise((resolve, reject) => {
      db.collection(HIGHSCORES).find().toArray(function (err, result) {
        if (err) {
          debug('Failed to retrieve highscores: ' + err)
          reject(new Error(err))
        } else {
          resolve(result)
        }
      })
    })
  }

  Database.prototype.saveHighscore = async function (highscore) {
    return new Promise((resolve, reject) => {
      db.collection(HIGHSCORES).insert(highscore, function (err, result) {
        if (err) {
          debug('Failed to save highscore: ' + err)
          reject(new Error(err))
        } else {
          debug('Successfully inserted new highscore into collection', highscore)
          const dbResult = result.ops[0]
          resolve(dbResult)
        }
      })
    })
  }

  return Database
})()
