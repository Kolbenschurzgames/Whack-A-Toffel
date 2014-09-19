module.exports = (function() {
    'use strict';

    var debug = require('debug')('toffel:server');
    var validator = require('./validator.js');

    var Database = require('./database.js');
    var dbName = process.env.NODE_ENV === 'test' ? 'toffelTest' : 'toffel';
    var db = new Database(dbName);

    var koa = require('koa');
    var router = require('koa-router');
    var koaBody = require('koa-body')();
    var app = koa();

    var serverPort = 3000;
    var serverHost = '127.0.0.1';

    app.use(router(app));

	app.get('/highscore', function *() {
		this.body = yield db.getHighscores();
	});

    app.post('/highscore', koaBody, function *() {
        var highscore = this.request.body;

        if (validator.isValidHighscore(highscore)) {
            debug('Received request to save highscore', highscore);
            this.body = yield db.saveHighscore(highscore);
        } else {
            debug('Received invalid highscore', highscore);
            this.response.status = 400;
            this.body = 'Invalid highscore';
        }
    });

    app.listen(serverPort, serverHost, function() {
        debug('Server listening on ' + serverHost + ':' + serverPort);
    });

})();
