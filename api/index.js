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
    // log sync time
    console.log('POST Sync Time: ', new Date());

    //log headers
    console.log('POST Header: ', request.headers);

    //log device details
    console.log('POST Body: ', request.body);

    //echo back
    const result = request.body;
    result.extras.account = 'ABCDR';
    result.info.imei = '90372y24';
    result.topics = [].concat(result.topics).concat('news');
    response.status(201);
    response.json(result);
});


/**
 * update existing device details received
 */
app.put('/devices', (request, response) => {
    // log sync time
    console.log('PUT Sync Time: ', new Date());

    //log headers
    console.log('PUT Headers: ', request.headers);

    //log device details
    console.log('PUT Body: ', request.body);

    //echo back
    const result = request.body;
    result.extras.account = 'ABCDR';
    result.info.imei = '90372y24';
    result.topics = [].concat(result.topics).concat('news');
    response.status(200);
    response.json(result);
});


//start api server
app.listen(3000);
