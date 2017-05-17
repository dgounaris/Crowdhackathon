package dgounaris.dev.sch.People;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dgounaris.dev.sch.Utils.SerializableImage;
import dgounaris.dev.sch.adapter.Trophy;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class Person implements Serializable {

    private int id;
    private String name;
    private String surname;
    private SerializableImage mImage = new SerializableImage();
    private int points;
    private ArrayList<Trophy> myTrophies = new ArrayList<>();

    public Person(int id, String name, String surname, int points, Bitmap profileImg) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.points = points;
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

    public int getId() {
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setmImage(SerializableImage mImage) {
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
