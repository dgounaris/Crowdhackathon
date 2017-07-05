var express = require('express');
var bodyParser = require('body-parser');
var multer = require('multer');
var app = express();
var path = require('path');
var fs = require('fs');
var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database('./schdb.db');
var crypto = require('crypto');

app.use(bodyParser.urlencoded({
    extended: true
}));

app.use(bodyParser.json());

//create some random hash numbers for id's
//var id = crypto.randomBytes(20).toString('hex');

var defaultpic = "profile_default.png";

app.get('/login/:user/:pass', function(req, res) {
    //BODY KEY NAMES
    //username: username
    //password: password
    db.all("select c_Id from Credentials where c_Username = ? and c_Password = ?", [req.body.username, req.body.password], function(err, rows) {
        if (err) res.status(500).end();
        sendResponse(rows, res);
    });
});

app.get('/people/:id', function(req, res) {
    db.all("select p_Id, p_Name, p_Surname, p_Points, p_TotalPoints from People where p_Id = ?", [req.params.id], function(err, rows) {
        if (err) res.status(500).end();
        sendResponse(rows, res);
    });
});

app.get('/people/:id/gettrophies', function(req, res) {
    db.all("select t_Id, t_Name, t_Description from Trophies, People_Trophies where pt_Person_Id = ? and pt_Trophy_Id = t_Id", [req.params.id], function(err, rows) {
        if (err) res.status(500).end();
        sendResponse(rows, res);
    });
});

app.get('/trophy/:id/image', function(req, res) {
    var dirname = './native/';
    db.all("select t_Image from Trophies where t_Id = ?", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        var dataarray = splitJSONObject(rows);
        res.sendFile(path.resolve(dirname + dataarray[0].substr(1, dataarray[0].length - 2)));
    });
});

app.get('/people/gettoppoints/:max', function(req, res) {
    db.all("select p_Id, p_Name, p_Surname, p_TotalPoints, p_Image from People where p_TotalPoints > 0 order by p_TotalPoints desc limit ?", [req.params.max], function(err, rows) {
        if (err) res.status(500).end();
        sendResponse(rows, res);
    });
});

app.post('/people/addpoints', function(req, res) {
    //BODY KEY NAMES
    //person id: id
    //points added: points
    db.run("update People set p_Points = p_Points + ?, p_TotalPoints = p_TotalPoints + ? where p_Id = ?", [req.body.points, req.body.points, req.body.id], function(err, rows) {
        if (err) res.status(500).end();
        res.status(202).end();
    });
});

app.post('/people/upload', function(req, res) {
    //BODY KEY NAMES
    //person id: id
    //image: image
    var filename = defaultpic;
    var storage = multer.diskStorage({
        destination: function(req, file, callback) {
            callback(null, './uploads');
        },
        filename: function(req, file, callback) {
            filename = file.fieldname + "_" + Date.now() + "." + file.mimetype.split("/").pop(); //avoiding duplicates
            callback(null, filename);
        }
    });

    var upload = multer({
        storage: storage
    }).single('image');

    upload(req,res,function(err) {
        if (err) return res.status(500).end();
        db.all("update People set p_Image = ? where p_Id = ?", [filename, req.body.id], function(err, rows) {
            if (err) return res.status(500).end();
            return res.status(202).end();
        });
    });
});

app.get('/people/:id/image', function(req, res) {
    var dirname = './uploads/';
    db.all("select p_Image from People where p_Id = ?", [req.params.id], function(err, rows) {
        if (err) return res.status(500).end();
        var dataarray = splitJSONObject(rows);
        res.sendFile(path.resolve(dirname + dataarray[0].substr(1, dataarray[0].length - 2)));
    });
});

app.post('/people/register', function(req, res) {
    //BODY KEY NAMES
    //username key: username
    //password key: password
    //name key: name
    //surname key: surname
    //image is to be uploaded seperately
    var id = crypto.randomBytes(20).toString('hex'); //generating random id
    db.serialize(function() {
        db.run("insert into Credentials values(?,?,?)", [id, req.body.username, req.body.password], function(err, rows) {
            if (err) throw err;
        });
        db.run("insert into People values(?,?,?,?,?,?)", [id, req.body.name, req.body.surname, 0, defaultpic, 0], function(err, rows) {
            if (err) throw err;
            return res.status(202).end();
        });
    });
});

app.get('/services/get/available', function(req, res) {
    db.all("select * from Services where s_EmptySlots > 0 order by s_Points desc", function(err, rows) {
        if (err) res.status(500).end();
        sendResponse(rows, res);
    });
});

app.post('/services/redeem', function(req, res) {
    //BODY KEY NAMES
    //person id: personid
    //service id: serviceid
    db.serialize(function() {
        db.run("update People set p_Points = p_Points - (select s_Points from Services where s_Id = ?) where p_Id = ? and exists(select * from Services, People where s_Id = ? and p_Id = ? and s_EmptySlots > 0 and p_Points > s_Points)", [req.params.serviceid, req.params.personid, req.params.serviceid, req.params.personid], function(err, rows) {
                if (err) throw err;
        });
        db.all("select changes()", function(err, rows) {
            var changes = splitJSONObject(rows);
            if (changes == 0) {
                res.status(500).end();
            }
            else {
                db.run("update Services set s_EmptySlots = s_EmptySlots - 1 where s_Id = ?", [req.body.serviceid], function(err, rows) {
                    if (err) throw err;
                });
                db.run("insert into People_Services values (?, ?)", [req.body.personid, req.body.serviceid], function(err, rows) {
                    if (err) throw err;
                    res.status(202).end();
                });
            }
        });
    });
});

app.get('/bins/get', function(req, res) {
    db.all("select * from Bins", function(err, rows) {
        if (err) res.status(500).send();
        sendResponse(rows, res);
    });
});

var splitJSONObject = function(object) { //supposed to take ONLY 1 json object at a time
    var datastring = JSON.stringify(object);
    var values = [];
    while (datastring.indexOf(",") != -1) {
        values.push(datastring.substr(datastring.indexOf(':')+1, datastring.indexOf(',')-datastring.indexOf(':')-1));
        datastring = datastring.substr(datastring.indexOf(',')+1, datastring.length-datastring.indexOf(','));
    }
    //last value, after last ','
    values.push(datastring.substr(datastring.indexOf(':')+1, datastring.indexOf('}')-datastring.indexOf(':')-1));
    return values;
}

var sendResponse = function(data, res) {
    var array = [];
        data.forEach(function(element) {
            array.push(element);
        }, this);
        if (array.length==0) {
            res.status(204).send(); //204 status has no body, and the client shouldn't alter the doc view, so browser doesn't refresh on test
        }
        else {
            res.status(200).send(JSON.stringify(array));
        }
}

app.listen(3003);