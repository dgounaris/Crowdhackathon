package dgounaris.dev.sch.People;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import dgounaris.dev.sch.APIHandler.APIHelper;
import dgounaris.dev.sch.Utils.SerializableImage;
import dgounaris.dev.sch.Trophies.Trophy;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class Person implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    private SerializableImage mImage = new SerializableImage();
    @SerializedName("points")
    private int points;
    @SerializedName("totalpoints")
    private int totalPoints;
    @SerializedName("trophies")
    private ArrayList<Trophy> myTrophies = new ArrayList<>();

    public Person(String id, String name, String surname, int points, int totalPoints) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.points = points;
        this.totalPoints = totalPoints;
    }

    public Person(String id, String name, String surname, int points, int totalPoints, Bitmap profileImg, ArrayList<Trophy> trophies) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.points = points;
        this.totalPoints = totalPoints;
        this.myTrophies = trophies;
        mImage.setImage(profileImg);
    }

    public boolean addPoints(int extrapoints) {
        this.points += extrapoints;
        return true;
    }

    public boolean redeemPoints(int pointsRequired) {
        points -= pointsRequired;
        return true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public SerializableImage getmImage() {
        return mImage;
    }

    public int getPoints() {
        return points;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setImage(SerializableImage mImage) {
        this.mImage = mImage;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addTrophy(Trophy newtrophy) {
        myTrophies.add(newtrophy);
    }

    public ArrayList<Trophy> getTrophies() {
        return this.myTrophies;
    }

}
