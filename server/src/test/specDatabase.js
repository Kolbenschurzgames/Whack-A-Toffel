var expect = require('chai').expect;
var sinon = require('sinon');
var Database = require('../main/database.js');
var mongoskin = require('mongoskin');
var testDbName = 'toffelTest';
var db;

describe('database spec', function() {
    var dbStub,
        insertStub = sinon.stub(),
        mockCollection = {
            insert: insertStub
        },
        collectionStub = sinon.stub().returns(mockCollection);

    before(function() {
        dbStub = sinon.stub(mongoskin, 'db');
        dbStub.returns({
            collection: collectionStub
        });
    });

    after(function() {
        dbStub.restore();
    });

    describe('database constructor', function() {
        it('should throw an error if no database name is specified', function() {
            var invalidConstructorCall = function() { Database(); };
            expect(invalidConstructorCall).to.throw(Error);
        });

        it('should not throw an error if a database name is specified', function() {
            var validConstructorCall = function() { Database(testDbName); };
            expect(validConstructorCall).not.to.throw();
        });

        it('should connect to the specified database and return a Database instance', function() {
            db = new Database(testDbName);
            expect(db).to.be.an.instanceOf(Database);
            expect(dbStub).to.have.been.calledOnce;
            expect(dbStub.firstCall.args[0]).to.include(testDbName);
        });

        after(function() {
            dbStub.reset();
        });
    });

    describe('saving a new highscore', function() {
        var highscore;

        before(function() {
            highscore = {
                name: 'test',
                score: 100,
                timestamp: new Date()
            };
        });

        it('should try to insert the given highscore object into the highscores collection', function() {
            db.saveHighscore(highscore);
            expect(insertStub).to.have.been.calledOnce;
            expect(insertStub.firstCall.args[0]).to.equal(highscore);
            expect(insertStub.firstCall.args[1]).to.be.a.function;
        });

        describe('successful insert of a highscore', function() {
            var result;

            before(function() {
                result = 'success';
                insertStub.yields(null, result);
            });

            it('should return a promise containing the result of the database operation', function(done) {
                db.saveHighscore(highscore).then(function(val) {
                    expect(val).to.equal(result);
                    done();
                });
            });

            after(function() {
                insertStub.reset();
            });
        });

        describe('error during insert of a highscore', function() {
            var error;

            before(function() {
                error = 'something went wrong';
                insertStub.yields(error);
            });

            it('should return a rejected promise containing the error message', function(done) {
                var promise = db.saveHighscore(highscore).fail(function(err) {
                    expect(err).to.be.an.instanceof(Error);
                    expect(err.message).to.equal(error);
                    done();
                });
            });

            after(function() {
                insertStub.reset();
            });
        });
    });
});
