'use strict';

var expect = require('chai').expect;
var sinon = require('sinon');
var proxyquire = require('proxyquire');
var Q = require('q');
var request = require('supertest')('http://localhost:3000');
var Database = require('../main/database.js');
var validator = require('../main/validator.js');

describe('server spec', function() {
    var dbMock,
        route,
        isValidHighscoreStub;

    before(function() {
        dbMock = sinon.createStubInstance(Database);
        isValidHighscoreStub = sinon.stub(validator, 'isValidHighscore').returns(true);

        proxyquire('../main/server.js', {
            './database.js': function() {
                return dbMock;
            }
        });
    });

    after(function() {
        isValidHighscoreStub.restore();
    });

    describe('/highscore', function() {
        before(function() {
            route = '/highscore';
        });

        describe('POST', function() {
            describe('valid data', function() {
                var dbResult,
                    validHighscore;

                before(function() {
                    validHighscore = {
                        name: 'name',
                        score: 1000,
                        timestamp: new Date().getTime()
                    };

                    dbResult = { '_id': 1, 'object': validHighscore };
                    dbMock.saveHighscore.returns(dbResult);
                });

                after(function() {
                    dbMock.saveHighscore.reset();
                });

                it('should return status code 200 and the database result in the response body', function(done) {
                    request.post(route)
                        .set('Content-Type', 'application/json')
                        .send(validHighscore)
                        .expect(200, dbResult, done);
                });

                it('should pass the data to the database module', function() {
                    expect(dbMock.saveHighscore).to.have.been.calledOnce;
                    expect(dbMock.saveHighscore.firstCall.args[0]).to.eql(validHighscore);
                });
            });

            describe('invalid data', function() {
                var invalidData;

                before(function() {
                    invalidData = {
                        name: 'name',
                        score: 'notAScore',
                        timestamp: new Date().getTime()
                    };

                    isValidHighscoreStub.withArgs(invalidData).returns(false);
                });

                after(function() {
                    isValidHighscoreStub.reset();
                });

                it('should return status code 400', function(done) {
                    request.post(route)
                        .set('Content-Type', 'application/json')
                        .send(invalidData)
                        .expect(400, done);
                });

                it('should not pass any data to the database module', function() {
                    expect(dbMock.saveHighscore).not.to.have.been.called;
                });
            });
        });

        describe('GET', function() {
            describe('successfully fetched data from database', function() {
                var dbResult;

                before(function() {
                    dbResult = [
                        { name: 'test', score: 1000 },
                        { name: 'test2', score: 2000 }
                    ];

                    dbMock.getHighscores.returns(dbResult);
                });

                after(function() {
                    dbMock.getHighscores.reset();
                });

                it('should return status code 200 and the database result in the response body', function(done) {
                    request.get(route)
                        .expect(200, dbResult, done);
                });

                it('should retrieve the highscores from the database module', function() {
                    expect(dbMock.getHighscores).to.have.been.calledOnce;
                    expect(dbMock.getHighscores.firstCall.args).to.be.empty;
                });
            });

            describe('error while fetching data from database', function() {
                var rejectedPromise;

                before(function() {
                    rejectedPromise = Q.fcall(function() {
                        throw new Error('Can\'t do it');
                    });
                    dbMock.getHighscores.returns(rejectedPromise);
                });

                after(function() {
                    dbMock.getHighscores.reset();
                });

                it('should return status code 500 and the error message in the response body', function(done) {
                    request.get(route)
                        .expect(500, done);
                });

                it('should have tried to retrieve the highscores from the database module', function() {
                    expect(dbMock.getHighscores).to.have.been.calledOnce;
                    expect(dbMock.getHighscores.firstCall.args).to.be.empty;
                });
            });
        });
    });
});
