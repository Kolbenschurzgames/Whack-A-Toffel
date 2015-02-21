'use strict';

var expect = require('chai').expect;
var sinon = require('sinon');
var validator = require('../main/validator.js');

describe('validator spec', function() {
    describe('isValidDate', function() {
        it('should return false if Date.getTime() returns NaN', function() {
            expect(validator.isValidDate('abc')).to.be.false;
        });

        it('should return false for non-Date objects', function() {
            var invalidObject = {
                getTime: function() {}
            };
            expect(validator.isValidDate(invalidObject)).to.be.false;
        });

        it('should return true for numeric timestamp values', function() {
            expect(validator.isValidDate(123456789)).to.be.true;
        });

        it('should return true for valid Date objects', function() {
            expect(validator.isValidDate(new Date())).to.be.true;
        });
    });

    describe('isValidHighscore', function() {
        var timestamp;

        before(function() {
            timestamp = new Date().getTime();
        });

        it('should return false if no parameter is passed', function() {
            expect(validator.isValidHighscore()).to.be.false;
        });

        it('should return false if the name property does not contain a string', function() {
            var invalidName = {
                name: 123,
                score: 1000,
                timestamp: timestamp
            };

            expect(validator.isValidHighscore(invalidName)).to.be.false;
        });

        it('should return false if the score property does not contain a number', function() {
            var invalidName = {
                name: 'name',
                score: '1000',
                timestamp: timestamp
            };

            expect(validator.isValidHighscore(invalidName)).to.be.false;
        });

        it('should return false if the timestamp property does not contain a valid date', function() {
            var invalidName = {
                name: 'name',
                score: 1000,
                timestamp: 'random falseness'
            };

            expect(validator.isValidHighscore(invalidName)).to.be.false;
        });

        it('should return true for valid highscore objects', function() {
            var validHighscore = {
                name: 'name',
                score: 1000,
                timestamp: timestamp
            };

            expect(validator.isValidHighscore(validHighscore)).to.be.true;
        });
    });
});
