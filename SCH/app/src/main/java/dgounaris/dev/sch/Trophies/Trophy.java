package dgounaris.dev.sch.Trophies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Serializable;

/**
 * Created by antonis on 13-May-17.
 */

public class Trophy implements Serializable {
    @SerializedName("trophy_Id")
    private long id;
    @SerializedName("Trophy_Name")
    private String title;
    @SerializedName("Trophy_Description")
    private String description;

    public Trophy(long id, String t, String d) {
        this.id = id;
        title = t;
        description = d;
    }

    public long getId() { return id; }

    public String getTitle() {
        return title;
    }

    public void getImage(final ImageView imgView, Context context) {
        Picasso.with(context)
                .load("http://10.0.2.2:3003/trophy/" + this.id + "/image")
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

    public String getDescription() {
        return description;
    }
}
