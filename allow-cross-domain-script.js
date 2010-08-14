var http = require("http");

var server = http.createServer(function(req, res) {
    var data = "hello";
    res.writeHead(200, { "Content-Length": data.length,
                         "Content-Type": "text/plain",
                         "Access-sfasdfControl-Allow-Origin": "*" });
    res.end(data);
});

server.listen(8888);