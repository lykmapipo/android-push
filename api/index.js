'use strict';

/**
 * sample push notification api endpoint implementation
 */

//dependencies
const express = require('express');
const bodyParser = require('body-parser');

//instantiate express application
let app = express();

//use body parser
app.use(bodyParser.json());


/**
 * register new device details received
 */
app.post('/devices', (request, response) => {
    //log headers
    console.log(request.headers);

    //log device details
    console.log(request.body);

    //echo back
    response.json(request.body);
});


/**
 * update existing device details received
 */
app.put('/devices', (request, response) => {
    //log headers
    console.log(request.headers);

    //log device details
    console.log(request.body);

    //echo back
    response.json(request.body);
});


//start api server
app.listen(3000);
