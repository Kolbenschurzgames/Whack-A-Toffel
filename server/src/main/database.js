module.exports = (function () {
  'use strict'

  var db
  var HIGHSCORES = 'highscores'

  var debug = require('debug')('toffel:database')
  var Q = require('q')

  var Database = function (dbName) {
    var mongoDbUri

    if (typeof dbName === 'string') {
      mongoDbUri = process.env.MONGOLAB_URI || 'mongodb://localhost:27017/' + dbName
      db = require('mongoskin').db(mongoDbUri, { native_parser: true })
    } else {
      throw new Error('Database name parameter missing')
    }
  }

  Database.prototype.getHighscores = function () {
    var deferred = Q.defer()

    db.collection(HIGHSCORES).find().toArray(function (err, result) {
      if (err) {
        debug('Failed to retrieve highscores: ' + err)
        deferred.reject(new Error(err))
      } else {
        deferred.resolve(result)
      }
    })

    return deferred.promise
  }

  Database.prototype.saveHighscore = function (highscore) {
    var deferred = Q.defer()

    db.collection(HIGHSCORES).insert(highscore, function (err, result) {
      if (err) {
        debug('Failed to save highscore: ' + err)
        deferred.reject(new Error(err))
      } else {
        debug('Successfully inserted new highscore into collection', highscore)
        var res = result.ops[0]
        debug('returning', res)
        deferred.resolve(res)
      }
    })

    return deferred.promise
  }

  return Database
})()
