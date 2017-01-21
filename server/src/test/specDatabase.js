/* eslint-env mocha */
'use strict'

const expect = require('chai').expect
const sinon = require('sinon')
const Database = require('../main/database.js')
const mongoskin = require('mongoskin')
const testDbName = 'toffelTest'
let db

describe('database spec', function () {
  let dbStub
  const insertStub = sinon.stub()
  const findToArrayStub = sinon.stub()
  const mockCollection = {
    insert: insertStub,
    find: function () {
      return { toArray: findToArrayStub }
    }
  }
  const collectionStub = sinon.stub().returns(mockCollection)

  before(function () {
    dbStub = sinon.stub(mongoskin, 'db')
    dbStub.returns({
      collection: collectionStub
    })
  })

  after(function () {
    dbStub.restore()
  })

  describe('database constructor', function () {
    it('should throw an error if no database name is specified', function () {
      const invalidConstructorCall = function () { Database() }
      expect(invalidConstructorCall).to.throw(Error)
    })

    it('should not throw an error if a database name is specified', function () {
      const validConstructorCall = function () { Database(testDbName) }
      expect(validConstructorCall).not.to.throw()
    })

    it('should connect to the specified database and return a Database instance', function () {
      db = new Database(testDbName)
      expect(db).to.be.an.instanceOf(Database)
      expect(dbStub).to.have.been.calledOnce
      expect(dbStub.firstCall.args[0].indexOf('mongodb://')).to.equal(0)
      expect(dbStub.firstCall.args[0]).to.include(testDbName)
    })

    after(function () {
      dbStub.reset()
    })
  })

  describe('saving a new highscore', function () {
    const highscore = {
      name: 'test',
      score: 100,
      timestamp: new Date()
    }

    it('should try to insert the given highscore object into the highscores collection', function () {
      db.saveHighscore(highscore)
      expect(insertStub).to.have.been.calledOnce
      expect(insertStub.firstCall.args[0]).to.equal(highscore)
      expect(insertStub.firstCall.args[1]).to.be.a.function
    })

    describe('successful insert of a highscore', function () {
      let result
      const dbHighscore = {
        name: 'test',
        score: 100,
        timestamp: 1433401883849,
        _id: '58837ecc6855b5318a0bbfbc'
      }

      before(function () {
        result = {
          result: {
            ok: 1,
            n: 1
          },
          ops: [
            dbHighscore
          ],
          insertedCount: 1,
          insertedIds: [
            '58837ecc6855b5318a0bbfbc'
          ]
        }
        insertStub.yields(null, result)
      })

      it('should return a promise containing the result of the database operation', async function () {
        const val = await db.saveHighscore(highscore)
        expect(val).to.eql(dbHighscore)
      })

      after(function () {
        insertStub.reset()
      })
    })

    describe('error during insert of a highscore', function () {
      let error

      before(function () {
        error = 'something went wrong'
        insertStub.yields(error)
      })

      it('should return a rejected promise containing the error message', async function () {
        let thrown

        try {
          await db.saveHighscore(highscore)
        } catch (err) {
          thrown = err
        }

        expect(thrown).to.be.an.instanceof(Error)
        expect(thrown.message).to.equal(error)
      })

      after(function () {
        insertStub.reset()
      })
    })
  })

  describe('retrieving all highscores', function () {
    describe('succeeds', function () {
      const highscoresResult = [
        { name: 'test', score: 1000 },
        { name: 'test2', score: 2000 }
      ]

      before(function () {
        findToArrayStub.yields(null, highscoresResult)
      })

      it('should return a promise containing the result of the database operation', async function () {
        const val = await db.getHighscores()
        expect(val).to.equal(highscoresResult)
      })

      after(function () {
        findToArrayStub.reset()
      })
    })

    describe('fails', function () {
      const error = 'failure'

      before(function () {
        findToArrayStub.yields(error)
      })

      it('should return a rejected promise containing the error message', async function () {
        let thrown

        try {
          await db.getHighscores()
        } catch (err) {
          thrown = err
        }

        expect(thrown).to.be.an.instanceof(Error)
        expect(thrown.message).to.equal(error)
      })

      after(function () {
        findToArrayStub.reset()
      })
    })
  })
})
