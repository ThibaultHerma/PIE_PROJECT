const WebSocket = require('ws');
const http = require('http');


module.exports.createServer = function(app,port){
	const server = http.createServer(app);
	server.listen(port, function listening() {});
	return server;
}

module.exports.createWebSocket = function(server){
	
	const wss =  new WebSocket.Server({ server:server });
	
	wss.broadcast = function broadcast(data) {
		wss.clients.forEach(function each(client) {
			if (client.readyState === WebSocket.OPEN) {
				try {
					//console.log('sending data ' + data);
					client.send(data);
				} catch (e) {
					//console.error(e);
				}
			}
		});
	};
	return wss;
}


