/* eslint-env mocha */
'use strict'

const expect = require('chai').expect
const sinon = require('sinon')
const proxyquire = require('proxyquire')
const request = require('supertest')('http://localhost:3000')
const Database = require('../main/database.js')
const validator = require('../main/validator.js')

describe('server spec', function () {
  let dbMock,
    route,
    isValidHighscoreStub

  before(function () {
    dbMock = sinon.createStubInstance(Database)
    isValidHighscoreStub = sinon.stub(validator, 'isValidHighscore').returns(true)

    proxyquire('../main/server.js', {
      './database.js': function () {
        return dbMock
      }
    })
  })

  after(function () {
    isValidHighscoreStub.restore()
  })

  describe('/highscore', function () {
    before(function () {
      route = '/highscore'
    })

    describe('POST', function () {
      describe('valid data', function () {
        const validHighscore = {
          name: 'name',
          score: 1000,
          timestamp: new Date().getTime()
        }
        const dbResult = { '_id': 1, 'object': validHighscore }

        before(function () {
          dbMock.saveHighscore.returns(dbResult)
        })

        after(function () {
          dbMock.saveHighscore.reset()
        })

        it('should return status code 200 and the database result in the response body', function (done) {
          request.post(route)
            .set('Content-Type', 'application/json')
            .send(validHighscore)
            .expect(200, dbResult, done)
        })

        it('should pass the data to the database module', function () {
          expect(dbMock.saveHighscore).to.have.been.calledOnce
          expect(dbMock.saveHighscore.firstCall.args[0]).to.eql(validHighscore)
        })
      })

      describe('invalid data', function () {
        const invalidData = {
          name: 'name',
          score: 'notAScore',
          timestamp: new Date().getTime()
        }

        before(function () {
          isValidHighscoreStub.withArgs(invalidData).returns(false)
        })

        after(function () {
          isValidHighscoreStub.reset()
        })

        it('should return status code 400', function (done) {
          request.post(route)
            .set('Content-Type', 'application/json')
            .send(invalidData)
            .expect(400, done)
        })

        it('should not pass any data to the database module', function () {
          expect(dbMock.saveHighscore).not.to.have.been.called
        })
      })
    })

    describe('GET', function () {
      describe('successfully fetched data from database', function () {
        const dbResult = [
          { name: 'test', score: 1000 },
          { name: 'test2', score: 2000 }
        ]

        before(function () {
          dbMock.getHighscores.returns(dbResult)
        })

        after(function () {
          dbMock.getHighscores.reset()
        })

        it('should return status code 200 and the database result in the response body', function (done) {
          request.get(route)
            .expect(200, dbResult, done)
        })

        it('should retrieve the highscores from the database module', function () {
          expect(dbMock.getHighscores).to.have.been.calledOnce
          expect(dbMock.getHighscores.firstCall.args).to.be.empty
        })
      })

      describe('error while fetching data from database', function () {
        const msg = "Can't do it"

        before(function () {
          dbMock.getHighscores.returns(Promise.reject(new Error(msg)))
        })

        after(function () {
          dbMock.getHighscores.reset()
        })

        it('should return status code 500 and the error message in the response body', function (done) {
          request.get(route)
            .expect(500, msg, done)
        })

        it('should have tried to retrieve the highscores from the database module', function () {
          expect(dbMock.getHighscores).to.have.been.calledOnce
          expect(dbMock.getHighscores.firstCall.args).to.be.empty
        })
      })
    })
  })
})
