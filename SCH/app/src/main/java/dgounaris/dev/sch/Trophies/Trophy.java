package dgounaris.dev.sch.Trophies;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import dgounaris.dev.sch.People.Person;
import dgounaris.dev.sch.Utils.SerializableImage;

/**
 * Created by antonis on 13-May-17.
 */

public class Trophy implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    private SerializableImage image = new SerializableImage();

    public Trophy(String id, String t, String d, Bitmap img) {
        this.id = id;
        title = t;
        description = d;
        image.setImage(img);
    }

    public String getId() { return id; }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image.getImage();
    }

    public String getDescription() {
        return description;
    }
}
