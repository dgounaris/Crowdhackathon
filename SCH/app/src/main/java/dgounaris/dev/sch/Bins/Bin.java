package dgounaris.dev.sch.Bins;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DimitrisLPC on 16/5/2017.
 */

public class Bin {

    @SerializedName("id")
    private String id;
    @SerializedName("latlong")
    private LatLng latlong;
    @SerializedName("hasSpace")
    private boolean hasSpace;

    public Bin(String id, LatLng latlong, boolean hasSpace) {
        this.id = id;
        this.latlong = latlong;
        this.hasSpace = hasSpace;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatlong() {
        return latlong;
    }

    public boolean isHasSpace() {
        return hasSpace;
    }

    public void setLatlong(LatLng latlong) {
        this.latlong = latlong;
    }

    public void setHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }
}
