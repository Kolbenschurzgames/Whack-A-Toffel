'use strict'

module.exports = class Database {

  constructor (dbName) {
    this.HIGHSCORES = 'highscores'
    this.debug = require('debug')('toffel:database')

    if (typeof dbName === 'string') {
      const mongoDbUri = process.env.MONGOLAB_URI || 'mongodb://localhost:27017/' + dbName
      this.db = require('mongoskin').db(mongoDbUri, { native_parser: true })
    } else {
      throw new Error('Database name parameter missing')
    }
  }

  async getHighscores () {
    return new Promise((resolve, reject) => {
      this.db.collection(this.HIGHSCORES).find().toArray((err, result) => {
        if (err) {
          this.debug('Failed to retrieve highscores: ' + err)
          reject(new Error(err))
        } else {
          resolve(result)
        }
      })
    })
  }

  async saveHighscore (highscore) {
    return new Promise((resolve, reject) => {
      this.db.collection(this.HIGHSCORES).insert(highscore, (err, result) => {
        if (err) {
          this.debug('Failed to save highscore: ' + err)
          reject(new Error(err))
        } else {
          this.debug('Successfully inserted new highscore into collection', highscore)
          const dbResult = result.ops[0]
          resolve(dbResult)
        }
      })
    })
  }
}
