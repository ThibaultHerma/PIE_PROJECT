/**
 * Network configuration for the backend server
 * @type {{}}
 * @author Theo Nguyen
 */
var config = {};

//IP address of the host
config.HOST = '127.0.0.1';
// port to receive data  from the  java app
config.JAVA_TO_JS_PORT = '28764';
//port to send data to the java app
config.JS_TO_JAVA_PORT = '28765';
//port of the websocket to communicate with the frontend
config.WS_PORT= '8080';

module.exports = config;