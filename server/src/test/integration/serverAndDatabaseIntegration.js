'use strict';

// run with "NODE_ENV=test DEBUG=toffel* mocha --harmony src/test/integration"

if (process.env.NODE_ENV === 'test') {

    var expect = require('chai').expect;
    var request = require('supertest')('http://localhost:3000');
    var db;

    var emptyCollection = function(collectionName) {
        db.collection(collectionName).remove({}, function(err){
            if (err) {
                console.error(err);
            }
        });
    };

    describe('integration test suite', function() {
        var route, collection;

        before(function() {
            db = require('mongoskin').db('mongodb://localhost:27017/toffelTest', {native_parser:true});
            require('../../main/server.js');
        });

        describe('highscores', function() {
            before(function() {
                route = '/highscore';
                collection = 'highscores';
            });

            after(function() {
                emptyCollection(collection);
            });

            describe('POST', function() {
                describe('valid data', function() {
                    var validHighscore;

                    before(function() {
                        validHighscore = {
                            name: 'test',
                            score: 1000,
                            timestamp: new Date().getTime()
                        };
                    });

                    it('should return status code 200 and the database result in the response body', function(done) {
                        request.post(route)
                        .set('Content-Type', 'application/json')
                        .send(validHighscore)
                        .expect(200, function(err, result) {
                            var body;
                            expect(err).to.not.exist;
                            expect(result).to.exist;
                            body = result.body;
                            expect(body).to.be.an.instanceof(Array);
                            expect(body).to.have.length(1);
                            expect(body[0]).to.have.property('name').that.equals(validHighscore.name);
                            expect(body[0]).to.have.property('score').that.equals(validHighscore.score);
                            expect(body[0]).to.have.property('timestamp').that.equals(validHighscore.timestamp);
                            expect(body[0]).to.have.property('_id').that.is.a.string;
                            done(err, result);
                        });
                    });

                    it('should store the highscore in the database', function(done) {
                        db.collection(collection).find().toArray(function(err, result) {
                            expect(err).to.not.exist;
                            expect(result).to.have.length(1);
                            expect(result[0]).to.have.property('name').that.equals(validHighscore.name);
                            expect(result[0]).to.have.property('score').that.equals(validHighscore.score);
                            expect(result[0]).to.have.property('timestamp').that.equals(validHighscore.timestamp);
                            done();
                        });
                    });
                });

                describe('invalid data', function() {
                    var invalidData;

                    before(function() {
                        emptyCollection(collection);

                        invalidData = {
                            name: 'name',
                            score: 'notAScore',
                            timestamp: 'new Date().getTime()'
                        };
                    });

                    it('should return status code 400', function(done) {
                        request.post(route)
                        .set('Content-Type', 'application/json')
                        .send(invalidData)
                        .expect(400, done);
                    });

                    it('should not store the invalid data in the database', function(done) {
                        db.collection(collection).find().toArray(function(err, result) {
                            expect(err).to.not.exist;
                            expect(result).to.be.empty;
                            done();
                        });
                    });
                });
            });
        });
    });
} else {
    console.log('Ignoring integration test suite because environment is not set to "test"');
}
