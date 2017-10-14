package dgounaris.dev.sch.People;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Serializable;
import java.util.ArrayList;

import dgounaris.dev.sch.Trophies.Trophy;
import dgounaris.dev.sch.Utils.PicassoHTTPS;
import dgounaris.dev.sch.Utils.SerializableImage;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class Person implements Serializable {

    @SerializedName("Id")
    private long id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Surname")
    private String surname;
    @SerializedName("Points")
    private int points;
    @SerializedName("TotalPoints")
    private int totalPoints;
    @SerializedName("City_City_id")
    private long cityId;
    @SerializedName("Trophies")
    private ArrayList<Trophy> myTrophies = new ArrayList<>();

    public Person(long id, String name, String surname, int points, int totalPoints) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.points = points;
        this.totalPoints = totalPoints;
    }

    public Person(long id, String name, String surname, int points, int totalPoints, Bitmap profileImg, ArrayList<Trophy> trophies) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.points = points;
        this.totalPoints = totalPoints;
        this.myTrophies = trophies;
    }

    public boolean addPoints(int extrapoints) {
        this.points += extrapoints;
        return true;
    }

    public boolean redeemPoints(int pointsRequired) {
        points -= pointsRequired;
        return true;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void getmImage(final ImageView imgView, Context context) {
        PicassoHTTPS.getInstance(context)
                .load("https://10.0.2.2:8433/person/" + this.id + "/image")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                        imgView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        //todo add default img
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    public int getPoints() {
        return points;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public long getCityId() {
        return cityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setImage(SerializableImage mImage) {
        //todo when updating profile img
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
