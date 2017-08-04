var express = require('express');
var bodyParser = require('body-parser');
var multer = require('multer');
var app = express();
var path = require('path');
var fs = require('fs');
var mysql = require('mysql');
var con = mysql.createConnection({
    host: "localhost",
    port: "3306",
    user: "root",
    password: "test",
    database: "wecycledb"
});
var crypto = require('crypto');
var passwordHash = require('password-hash');

app.use(bodyParser.urlencoded({
    extended: true
}));

app.use(bodyParser.json());

//create some random hash numbers for id's
//var id = crypto.randomBytes(20).toString('hex');

var defaultpic = "profile_default.png";

app.post('/login', function(req, res) {
    //BODY KEY NAMES
    //username: username
    //password: password
    if (req.body.username == undefined || req.body.password == undefined) {
        return res.status(400).end();
    }
    console.log("Attempted connection with " + req.body.username + ", " + req.body.password);
    db.all("select c_Password from Credentials where c_Username = ?", [req.body.username], function(err, rows) {
        if (err) return res.status(500).end();
        if (rows.length == 0) return res.status(401).end();
        var storedPw = rows[0].c_Password;
        if (passwordHash.verify(req.body.password, storedPw)) {
            db.all("select c_Id from Credentials where c_Username = ? and c_Password = ?", [req.body.username, storedPw], function(err, rows) {
                if (err) return res.status(500).end();
                sendResponse(rows, res);
            });
        }
        else {
            res.status(401).end();
        }
    });
});

app.post('/person/newpass', function(req, res) {
    //BODY KEY NAMES
    //username: username
    //password: password
    var hashedPw = passwordHash.generate(req.body.password);
    db.run("update Credentials set c_Password = ? where c_Username = ?", [hashedPw, req.body.username], function(err, rows) {
        if (err) return res.status(500).end();
        res.status(200).end();
    });
});

app.get('/person/:id/details', function(req, res) {
    console.log("Retrieving details for " + req.params.id);
    db.all("select p_Name, p_Surname, p_Points, p_TotalPoints from People where p_Id = ?", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        var response = rows;
        console.log("Retrieving trophies for " + req.params.id);
        db.all("select t_Id, t_Name, t_Description from Trophies, People_Trophies where pt_Person_Id = ? and pt_Trophy_Id = t_Id", [req.params.id], function(err, trophies) {
            if (err) return res.status(500).end();
            response[0].p_Trophies = trophies;
            sendResponse(response, res);
        });
    });
});

app.get('/person/:id/image', function(req, res) {
    console.log("Retrieving image for " + req.params.id);
    var dirname = './uploads/';
    db.all("select p_Image from People where p_Id = ?", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        if (rows.length == 0) return res.status(400).end();
        var filepath = rows[0].p_Image;
        /* AN ALTERNATE IDEA FOR SENDING, MAY BE USEFUL
         res.writeHead(200, {'Content-Type': 'image/png'});
         fs.readFile(dirname + dataarray[0].substr(1, dataarray[0].length - 2), function(err, data) {
         var img64 = new Buffer(data, 'binary').toString('base64');
         res.emit("send_img", img64);
         });*/
        res.sendFile(path.resolve(dirname + filepath));
    });
});

app.get('/person/:id/trophies', function(req, res) {
    console.log("Retrieving trophies for " + req.params.id);
    db.all("select t_Id, t_Name, t_Description from Trophies, People_Trophies where pt_Person_Id = ? and pt_Trophy_Id = t_Id", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        sendResponse(rows, res);
    });
});

app.get('/trophy/:id/image', function(req, res) {
    console.log("Retriecing image for trophy " + req.params.id);
    var dirname = './native/';
    db.all("select t_Image from Trophies where t_Id = ?", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        if (rows.length == 0) return res.status(400).end();
        var filepath = rows[0].t_Image;
        res.sendFile(path.resolve(dirname + filepath));
    });
});

app.get('/people/toppoints/:max', function(req, res) {
    console.log("Retrieving top " + req.params.max + " by total points");
    db.all("select p_Id, p_Name, p_Surname, p_Points, p_TotalPoints from People where p_TotalPoints > 0 order by p_TotalPoints desc limit ?", [req.params.max], function(err, rows) {
        if (err) return res.status(500).end();
        sendResponse(rows, res);
    });
});

app.post('/person/addpoints', function(req, res) {
    //BODY KEY NAMES
    //person id: id
    //points added: points
    db.run("update People set p_Points = p_Points + ?, p_TotalPoints = p_TotalPoints + ? where p_Id = ?", [req.body.points, req.body.points, req.body.id], function(err, rows) {
        if (err) return res.status(500).end();
        db.all("select p_Points from People where p_Id = ?", [req.body.id], function(err, rows) {
            if (err) return res.status(500).end();
            sendResponse(rows);
        });
    });
});

app.post('/person/upload', function(req, res) {
    //BODY KEY NAMES
    //person id: id
    //image: image
    var passedId = req.body.id;
    var filename = defaultpic;
    var storage = multer.diskStorage({
        destination: function(req, file, callback) {
            callback(null, './uploads');
        },
        filename: function(req, file, callback) {
            filename = file.fieldname + "_" + passedId.substr(passedId.length-4, 3) + "_" + Date.now() + "." + file.mimetype.split("/").pop(); //avoiding duplicates
            callback(null, filename);
        }
    });

    var upload = multer({
        storage: storage
    }).single('image');

    upload(req,res,function(err) {
        if (err) return res.status(500).end();
        db.all("update People set p_Image = ? where p_Id = ?", [filename, passedId], function(err, rows) {
            if (err) return res.status(500).end();
            res.status(200).end();
        });
    });
});

app.post('/person/register', function(req, res) {
    //BODY KEY NAMES
    //username key: username
    //password key: password
    //name key: name
    //surname key: surname
    //image key: image
    console.log("Attempting registration with " + req.body.username + ", " + req.body.password + ", " + req.body.name + ", " + req.body.surname);
    if (req.body.username == undefined || req.body.password == undefined || req.body.name == undefined || req.body.surname == undefined) {
        return res.status(400).end();
    }
    db.serialize(function() {
        var id = crypto.randomBytes(20).toString('hex'); //generating random id
        var filename = defaultpic;
        var storage = multer.diskStorage({
            destination: function(req, file, callback) {
                callback(null, './uploads');
            },
            filename: function(req, file, callback) {
                filename = file.fieldname + "_" + id.substr(id.length-4, 3) + "_" + Date.now() + "." + file.mimetype.split("/").pop(); //avoiding duplicates
                callback(null, filename);
            }
        });

        var upload = multer({
            storage: storage
        }).single('image');

        upload(req,res,function(err) {
            if (err) throw err;
            db.run("insert into People values(?,?,?,?,?,?)", [id, req.body.name, req.body.surname, 0, filename, 0], function(err, rows) {
                if (err) throw err;
            });
        });
        //this is done last because i'd rather have "trash" users in people than "trash" credential pairs
        var hashedPw = passwordHash.generate(req.body.password); //hashing password before sending to db
        db.run("insert into Credentials values(?,?,?)", [id, req.body.username, hashedPw], function(err, rows) {
            if (err) throw err;
            var jsonResponse = [];
            jsonResponse.push({'c_Id': id});
            sendResponse(jsonResponse, res);
        });
    });
});

app.get('/services/available', function(req, res) {
    console.log("Getting available services...");
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select * from Services where Empty_Slots > 0 order by Points_Required desc", function (err, result) {
            sendResponse(result,res);
        });
    });
});

app.post('/services/redeem', function(req, res) {
    //BODY KEY NAMES
    //person id: personid
    //service id: serviceid
    console.log("Redeeming " + req.body.serviceid + " from " + req.body.personid);
    db.serialize(function() {
        db.run("update People set p_Points = p_Points - (select s_Points from Services where s_Id = ?) where p_Id = ? and exists(select * from Services, People where s_Id = ? and p_Id = ? and s_EmptySlots > 0 and p_Points > s_Points)", [req.body.serviceid, req.body.personid, req.body.serviceid, req.body.personid], function(err, rows) {
            if (err) return res.status(500).end();
        });
        db.all("select changes()", function(err, rows) {
            if (rows.length == 0) return res.status(500).end();
            else {
                var pointsAfter = [];
                db.all("select p_Points from People where p_Id = ?", [req.body.personid], function(err, rows) {
                    if (err) return res.status(500).end();
                    pointsAfter = rows;
                });
                db.run("update Services set s_EmptySlots = s_EmptySlots - 1 where s_Id = ?", [req.body.serviceid], function(err, rows) {
                    if (err) return res.status(500).end();
                });
                db.run("insert into People_Services values (?, ?)", [req.body.personid, req.body.serviceid], function(err, rows) {
                    if (err) return res.status(500).end();
                    sendResponse(pointsAfter, res);
                });
            }
        });
    });
});

app.get('/bins/all', function(req, res) {
    console.log("Requested all bins...");
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select * from bins", function (err, result) {
            sendResponse(result,res);
        });
    });
});

var sendResponse = function(data, res) {
    if (data.length==0) {
        res.status(204).send(); //204 status has no body, and the client shouldn't alter the doc view, so browser doesn't refresh on test
    }
    else {
        res.status(200).send(JSON.stringify(data));
    }
}

app.listen(3003);