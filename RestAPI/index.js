var express = require('express');
var bodyParser = require('body-parser');
var multer = require('multer');
var app = express();
var path = require('path');
var fs = require('fs');
var multiparty = require('multiparty');
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
//FIXED ERRORS, STILL NEEDS TO RETURN WHOLE PERSON INSTEAD OF ID
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
            var storedPw = rows[0].Password;
            if (passwordHash.verify(req.body.password, storedPw)) {
                con.query("select Id, Name, Surname, Points, TotalPoints, City_City_Id from people where Id = (select Person_Id from credentials where Username = ? and Password = ?)", [req.body.username, storedPw], function (err, rows) {
                    if (err) return res.status(500).end();
                    var response = rows;
                    console.log("Retrieving trophies for user logging in");
                    con.query("select Trophy_Id, Trophy_Name, Trophy_Description from trophies, people_has_trophies where People_idPeople = (select Person_Id from credentials where Username = ? and Password = ?) and Trophies_idTrophies = Trophy_Id", [req.body.username, storedPw], function(err, trophies) {
                        if (err) return res.status(500).end();
                        response[0].Trophies = trophies;
                        sendResponse(response[0], res);
                    });
                });
            }
            else {
                res.status(401).end();
            }
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
        con.query("select Name, Surname, Points, TotalPoints, City_City_id from people where Id = ?", [req.params.id], function (err, rows) {
            if (err) return res.status(500).end();
            var response = rows;
            console.log("Retrieving trophies for " + req.params.id);
            con.query("select Trophy_Id, Trophy_Name, Trophy_Description from trophies, people_has_trophies where People_idPeople = ? and Trophies_idTrophies = Trophy_Id", [req.params.id], function(err, trophies) {
                if (err) return res.status(500).end();
                response[0].Trophies = trophies;
                sendResponse(response[0], res);
            });
        });
    });
});

//DONE
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
            var filepath = rows[0].Image;
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

//DONE
app.get('/trophy/:id/image', function(req, res) {
    console.log("Retriecing image for trophy " + req.params.id);
    req.params.id = parseInt(req.params.id);
    var dirname = './native/';
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select Trophy_Image from Trophies where Trophy_Id = ?", [req.params.id], function (err, rows) {
            if (err) return res.status(500).end();
            if (rows.length == 0) return res.status(400).end();
            var filepath = rows[0].Trophy_Image;
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
        con.query("select Id, `people`.`Name`, Surname, Points, TotalPoints from people where TotalPoints > 0 order by TotalPoints desc limit ?", [req.params.max] , function (err, result) {
            if (err) return res.status(500).end();
            sendResponse(result,res);
        });
    });
});

//DONE
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
                sendResponse(rows[0].Points, res);
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
    }).fields(
        [
            { name: 'image', maxCount: 1 }
        ]
    )

    upload(req,res,function(err) {
        if (err) return res.status(500).end();
        con.connect(function(err) {
            console.log("Connected!");
            con.query("update People set Image = ? where Id = ?", [filename, passedId], function(err, rows) {
                res.status(200).end();
            })
        })
    });
});

app.post('/person/register', function(req, res) {
    //BODY KEY NAMES
    //username key: username
    //password key: password
    //name key: name
    //surname key: surname
    //image key: image
    var form = new multiparty.Form();
    form.parse(req, function(err, fields, files) {
        if (fields.username == undefined || fields.password == undefined || fields.name == undefined || fields.surname == undefined) {
            return res.status(400).end();
        }
        var username = fields.username.toString().substr(1, fields.username.toString().length-2);
        var password = fields.password.toString().substr(1, fields.password.toString().length-2);
        var name = fields.name.toString().substr(1, fields.name.toString().length-2);
        var surname = fields.surname.toString().substr(1, fields.surname.toString().length-2);
        console.log("Attempting registration with " + username + ", " + password + ", " + name + ", " + surname);
        con.connect(function(err) {
            var filename = defaultpic;
            var storage = multer.diskStorage({
                destination: function(req, file, callback) {
                    callback(null, './uploads');
                },
                filename: function(req, file, callback) {
                    var id = crypto.randomBytes(4).toString('hex'); //avoiding pic name duplicates
                    filename = file.fieldName + "_" + id + "_" + Date.now() + "." + file.mimetype.split("/").pop(); //avoiding pic name duplicates
                    callback(null, filename);
                }
            });

            var upload = multer({
                storage: storage
            }).single('image');

            upload(req,res,function(err) {
                if (err) throw err;
                con.query("insert into People values(?,?,?,?,?,?,?)", [null, name, surname, 0, filename, 0, 1], function(err, rows) {
                    if (err) throw err;
                    var id = rows.insertId;
                    var hashedPw = passwordHash.generate(password.toString()); //hashing password before sending to db
                    con.query("insert into Credentials values(?,?,?)", [username, hashedPw, id], function(err, rows) {
                        if (err) throw err;
                        con.query("select Id, Name, Surname, Points, TotalPoints, City_City_Id from people where Id = ?", [id], function (err, rows) {
                            if (err) return res.status(500).end();
                            var response = rows;
                            console.log("Retrieving trophies for user logging in");
                            con.query("select Trophy_Id, Trophy_Name, Trophy_Description from trophies, people_has_trophies where People_idPeople = ? and Trophies_idTrophies = Trophy_Id", [id], function(err, trophies) {
                                if (err) return res.status(500).end();
                                response[0].Trophies = trophies;
                                sendResponse(response[0], res);
                            });
                        });
                    });
                });
            });
        });
    });
});

//DONE
app.get('/services/available/:cityid', function(req, res) {
    console.log("Getting available services...");
    con.connect(function(err) {
        console.log("Connected!");
        con.query("select * from Services where City_City_Id = ? and Empty_Slots > 0 order by Points_Required desc", [parseInt(req.params.cityid)], function (err, result) {
            sendResponse(result,res);
        });
    });
});

//DONE
app.post('/services/redeem', function(req, res) {
    //BODY KEY NAMES
    //person id: personid
    //service id: serviceid
    console.log("Redeeming " + req.body.serviceid + " from " + req.body.personid);
    con.connect(function(err) {
        console.log("Connected!");
        con.query("update People p1 set p1.Points = p1.Points - (select s1.Points_Required from services s1 where s1.Service_Id = ?) where p1.Id = ? and exists(select * from services s where s.Service_Id = ? and s.Empty_Slots > 0 and p1.Points>=s.Points_Required)", [req.body.serviceid, req.body.personid, req.body.serviceid], function(err, rows) {
            if (err) throw err;
            if (rows.affectedRows>0) {
                con.query("update Services set Empty_Slots = Empty_Slots - 1 where Service_Id = ?", [req.body.serviceid], function(err, rows) {
                    if (err) throw err;
                    con.query("insert into People_Services values (?, ?)", [req.body.personid, req.body.serviceid], function(err, rows) {
                        if (err) throw err;
                        con.query("select Points from People where Id = ?", [req.body.personid], function(err, rows) {
                            if (err) throw err;
                            pointsAfter = rows[0].Points;
                            sendResponse(pointsAfter, res);
                        });
                    });
                });
            }
        });
    })
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