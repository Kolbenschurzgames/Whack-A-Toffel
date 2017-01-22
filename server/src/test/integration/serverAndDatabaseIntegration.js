/* eslint-env mocha */
'use strict'

// run with "npm run integration""

if (process.env.NODE_ENV === 'test') {
  const expect = require('chai').expect
  const request = require('supertest')('http://localhost:3000')
  let db

  var emptyCollection = function (collectionName, callback) {
    db.collection(collectionName).remove({}, callback)
  }

  describe('integration test suite', function () {
    let route, collection

    before(function () {
      db = require('mongoskin').db('mongodb://localhost:27017/toffelTest', { native_parser: true })
      require('../../main/server.js')
    })

    describe('highscores', function () {
      before(function () {
        route = '/highscore'
        collection = 'highscores'
      })

      after(function (done) {
        emptyCollection(collection, done)
      })

      describe('POST', function () {
        describe('valid data', function () {
          const validHighscore = {
            name: 'test',
            score: 1000,
            timestamp: new Date().getTime()
          }

          it('should return status code 200 and the database result in the response body', function (done) {
            request.post(route)
              .set('Content-Type', 'application/json')
              .send(validHighscore)
              .expect(200, function (err, result) {
                var body
                expect(err).to.not.exist
                expect(result).to.exist
                body = result.body
                expect(body).to.have.property('name').that.equals(validHighscore.name)
                expect(body).to.have.property('score').that.equals(validHighscore.score)
                expect(body).to.have.property('timestamp').that.equals(validHighscore.timestamp)
                expect(body).to.have.property('_id').that.is.a.string
                done(err, result)
              })
          })

          it('should store the highscore in the database', function (done) {
            db.collection(collection).find().toArray(function (err, result) {
              expect(err).to.not.exist
              expect(result).to.have.length(1)
              expect(result[0]).to.have.property('name').that.equals(validHighscore.name)
              expect(result[0]).to.have.property('score').that.equals(validHighscore.score)
              expect(result[0]).to.have.property('timestamp').that.equals(validHighscore.timestamp)
              done(err, result)
            })
          })
        })

        describe('invalid data', function () {
          const invalidData = {
            name: 'name',
            score: 'notAScore',
            timestamp: 'new Date().getTime()'
          }

          before(function (done) {
            emptyCollection(collection, done)
          })

          it('should return status code 400', function (done) {
            request.post(route)
              .set('Content-Type', 'application/json')
              .send(invalidData)
              .expect(400, done)
          })

          it('should not store the invalid data in the database', function (done) {
            db.collection(collection).find().toArray(function (err, result) {
              expect(err).to.not.exist
              expect(result).to.be.instanceof(Array)
              expect(result).to.be.empty
              done(err, result)
            })
          })
        })
      })

      describe('GET', function () {
        describe('empty highscores collection', function () {
          it('should return status code 200 and an empty array in the response body', function (done) {
            const emptyArray = []
            request.get(route)
              .expect(200, emptyArray, done)
          })
        })

        describe('results from database', function () {
          const highscores = [
            { name: 'test', score: 1000, timestamp: new Date().getTime() },
            { name: 'test2', score: 2000, timestamp: new Date().getTime() }
          ]

          before(function (done) {
            db.collection(collection).insert(highscores, done)
          })

          after(function (done) {
            db.collection(collection).remove({}, done)
          })

          it('should return status code 200 and the database result in the response body', function (done) {
            request.get(route)
              .expect(200, function (err, result) {
                const body = result.body
                const numHighscores = highscores.length

                expect(err).to.not.exist
                expect(body).to.be.instanceof(Array)

                for (let i = 0; i < numHighscores; i++) {
                  expect(body[i]).to.have.property('name').that.equals(highscores[i].name)
                  expect(body[i]).to.have.property('score').that.equals(highscores[i].score)
                  expect(body[i]).to.have.property('timestamp').that.equals(highscores[i].timestamp)
                  expect(body[i]).to.have.property('_id')
                }

                done(err, result)
              })
          })
        })
      })
    })
  })
} else {
  console.log('Ignoring integration test suite because environment is not set to "test"')
}
