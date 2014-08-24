module.exports = (function() {
	var db;
	var HIGHSCORES = 'highscores';

	var debug = require('debug')('toffel:database');
	var Q = require('q');

	var Database = function(dbName) {
		if (typeof dbName === 'string') {
			db = require('mongoskin').db('localhost:27017/' + dbName);
		} else {
			throw new Error('Database name parameter missing');
		}
	};

	Database.prototype.saveHighscore = function(highscore) {
		var deferred = Q.defer();

		db.collection(HIGHSCORES).insert(highscore, function(err, result) {
		    if (err) {
				debug('Failed to save highscore: ' + err);
				deferred.reject(new Error(err));
			} else {
				debug('Successfully inserted new highscore into collection', highscore);
				deferred.resolve(result);
			}
		});

		return deferred.promise;
	};

	return Database;
})();
