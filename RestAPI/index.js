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
//LOGIN NEEDS FIXING
app.post('/login', function(req, res) {
    //BODY KEY NAMES
    //username: username
    //password: password
    if (req.body.username == undefined || req.body.password == undefined) {
        console.log(req.body.username);
        return res.status(400).end();
    }
    console.log("Attempted connection with " + req.body.username + ", " + req.body.password);
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Password from credentials where Username = ?", [req.body.username], function (err, rows) {
            if (err) return res.status(500).end();
            if (rows.length == 0) return res.status(401).end();
            var storedPw = rows[0].c_Password;
            if (passwordHash.verify(req.body.password, storedPw)) {
                con.query("select Person_Id from credentials where Username = ? and Password = ?", [req.body.username, storedPw], function (err, result) {
                    if (err) return res.status(500).end();
                    console.log("found!");
                    sendResponse(result, res);
                });
            }
            else {
                res.status(401).end();
            }
            sendResponse(result,res);
        });
    });
});

//BODY NEEDS FIXING
app.post('/person/newpass', function(req, res) {
    //BODY KEY NAMES
    //username: username
    //password: password
    var hashedPw = passwordHash.generate(req.body.password);
    con.connect(function(err) {
        console.log("Connected!");
        con.query("update credentials set Password = ? where Username = ?", [hashedPw, req.body.username], function (err, result) {
            if (err) return res.status(500).end();
            res.status(200).end();
        });
    });
});

//DONE
app.get('/person/:id/details', function(req, res) {
    console.log("Retrieving details for " + req.params.id);
    req.params.id = parseInt(req.params.id);
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Name, Surname, Points, TotalPoints from people where Id = ?", [req.params.id], function (err, rows) {
            if (err) return res.status(500).end();
            var response = rows;
            console.log("Retrieving trophies for " + req.params.id);
            con.query("select Trophy_Id, Trophy_Name, Trophy_Description from trophies, people_has_trophies where People_idPeople = ? and Trophies_idTrophies = Trophy_Id", [req.params.id], function(err, trophies) {
                if (err) return res.status(500).end();
                response[0].p_Trophies = trophies;
                sendResponse(response, res);
            });
        });
    });
});

//Error: ENOENT: no such file or directory, stat 'C:\Users\Rhogarj\Documents\GitHub\Crowdhackathon\RestAPI\uploads\undefined'
app.get('/person/:id/image', function(req, res) {
    console.log("Retrieving image for " + req.params.id);
    req.params.id = parseInt(req.params.id);
    var dirname = './uploads/';
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Image from people where Id = ?", [req.params.id], function (err, rows) {
            console.log("Done!");
            if (err) return res.status(500).end();
            if (rows.length == 0) return res.status(400).end();
            var filepath = rows[0].p_Image;
            res.sendFile(path.resolve(dirname + filepath));
        });
        /* AN ALTERNATE IDEA FOR SENDING, MAY BE USEFUL
         res.writeHead(200, {'Content-Type': 'image/png'});
         fs.readFile(dirname + dataarray[0].substr(1, dataarray[0].length - 2), function(err, data) {
         var img64 = new Buffer(data, 'binary').toString('base64');
         res.emit("send_img", img64);
         });*/
    });
});

//Done
app.get('/person/:id/trophies', function(req, res) {
    console.log("Retrieving trophies for " + req.params.id);
    req.params.id = parseInt(req.params.id);
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Trophy_Id, Trophy_Name, Trophy_Description from trophies, people_has_trophies where People_idPeople = ? and Trophies_idTrophies = Trophy_Id", [req.params.id], function (err, result) {
            if (err) return res.status(500).end();
            console.log("In");
            sendResponse(result,res);
        });
    });
});

//Error: ENOENT: no such file or directory, stat 'C:\Users\Rhogarj\Documents\GitHub\Crowdhackathon\RestAPI\native\undefined'
app.get('/trophy/:id/image', function(req, res) {
    console.log("Retriecing image for trophy " + req.params.id);
    req.params.id = parseInt(req.params.id);
    var dirname = './native/';
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Trophy_Image from Trophies where Trophy_Id = ?", [req.params.id], function (err, rows) {
            if (err) return res.status(500).end();
            if (rows.length == 0) return res.status(400).end();
            var filepath = rows[0].t_Image;
            res.sendFile(path.resolve(dirname + filepath));
            console.log("sent!");
        });
    });
});

//DONE
app.get('/people/toppoints/:max', function(req, res) {
    console.log("Retrieving top " + req.params.max + " by total points");
    con.connect(function(err) {
        console.log("Connected!");
        req.params.max = parseInt(req.params.max);
        if (err) throw res.status(500).end();
        con.query("select Id, `people`.`Name`, Surname, Points, TotalPoints from people where TotalPoints > 0 order by TotalPoints desc limit ?", [req.params.max] , function (err, result) {
            if (err) throw res.status(500).end();
            sendResponse(result,res);
        });
    });
});

//BODY NEEDS FIXING
app.post('/person/addpoints', function(req, res) {
    //BODY KEY NAMES
    //person id: id
    //points added: points
    con.connect(function(err) {
        console.log("Connected!");
        con.query("update People set Points = Points + ?, TotalPoints = TotalPoints + ? where Id = ?", [req.body.points, req.body.points, req.body.id], function (err, result) {
            if (err) return res.status(500).end();
            con.query("select Points from People where Id = ?", [req.body.id], function(err, rows) {
                if (err) return res.status(500).end();
                sendResponse(rows);
            });
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

//DONE
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

//DONE
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