/* eslint-env mocha */
'use strict'

const expect = require('chai').expect
const validator = require('../main/validator.js')

describe('validator spec', function () {
  describe('isValidDate', function () {
    it('should return false if Date.getTime() returns NaN', function () {
      expect(validator.isValidDate('abc')).to.be.false
    })

    it('should return false for non-Date objects', function () {
      const invalidObject = {
        getTime: function () { }
      }
      expect(validator.isValidDate(invalidObject)).to.be.false
    })

    it('should return true for numeric timestamp values', function () {
      expect(validator.isValidDate(123456789)).to.be.true
    })

    it('should return true for valid Date objects', function () {
      expect(validator.isValidDate(new Date())).to.be.true
    })
  })

  describe('isValidHighscore', function () {
    const timestamp = new Date().getTime()

    it('should return false if no parameter is passed', function () {
      expect(validator.isValidHighscore()).to.be.false
    })

    it('should return false if the name property does not contain a string', function () {
      const invalidName = {
        name: 123,
        score: 1000,
        timestamp: timestamp
      }

      expect(validator.isValidHighscore(invalidName)).to.be.false
    })

    it('should return false if the score property does not contain a number', function () {
      const invalidName = {
        name: 'name',
        score: '1000',
        timestamp: timestamp
      }

      expect(validator.isValidHighscore(invalidName)).to.be.false
    })

    it('should return false if the timestamp property does not contain a valid date', function () {
      const invalidName = {
        name: 'name',
        score: 1000,
        timestamp: 'random falseness'
      }

      expect(validator.isValidHighscore(invalidName)).to.be.false
    })

    it('should return true for valid highscore objects', function () {
      const validHighscore = {
        name: 'name',
        score: 1000,
        timestamp: timestamp
      }

      expect(validator.isValidHighscore(validHighscore)).to.be.true
    })
  })
})
