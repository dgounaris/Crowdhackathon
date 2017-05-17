package dgounaris.dev.sch.adapter;

import android.graphics.Bitmap;

import java.io.Serializable;

import dgounaris.dev.sch.Utils.SerializableImage;

/**
 * Created by antonis on 13-May-17.
 */

public class Trophy implements Serializable {
    private String title;
    private String description;
    private SerializableImage image = new SerializableImage();

    public Trophy(String t, String d, Bitmap img) {
        title = t;
        description = d;
        image.setImage(img);
    }

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
