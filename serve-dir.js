var http = require("http");
var fs = require("fs");
var url = require("url");

http.createServer(function(req, res) {
    var path = "." + url.parse(req.url).pathname;
    
    fs.readFile(path, function(err, data) {
        if(err) {
            var body = "404'd";
            res.writeHead(404, { "Content-Length": body.length,
                                 "Content-Type": "text/plain" });
            res.end(body);
            
            console.error("404'd: " + path);
            
            return;
        };
        
        var ext = path.substr(path.lastIndexOf("."));
        var mime = "text/plain";
        
        if(ext === ".html") { mime = "text/html"; }
        else if(ext === ".xml") { mime = "application/xml"; }
        else if(ext === ".png") { mime = "image/png"; }
        else if(ext === ".gif") { mime = "image/gif"; }
        else if(ext === ".jpg" || ext === ".jpeg") { mime = "image/jpeg"; }
        console.log("path: " + path + "; ext: " + ext + "; mime: " + mime);
        res.writeHead(200, { "Conent-Length": data.length,
                             "Content-Type": mime });
        res.end(data, "utf8");
    });
}).listen(8006);

console.log("Listening on port 8006...");