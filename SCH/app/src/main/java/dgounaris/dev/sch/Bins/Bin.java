package dgounaris.dev.sch.Bins;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DimitrisLPC on 16/5/2017.
 */

public class Bin {

    @SerializedName("Id")
    private long id;
    @SerializedName("Lat")
    private double Lat;
    @SerializedName("Long")
    private double Long;
    @SerializedName("Space")
    private boolean hasSpace;

    public Bin(long id, double Lat, double Long, boolean hasSpace) {
        this.id = id;
        this.Lat = Lat;
        this.Long = Long;
        this.hasSpace = hasSpace;
    }

    public long getId() {
        return id;
    }

    public LatLng getLatlong() {
        return new LatLng(Lat, Long);
    }

    public boolean hasSpace() {
        return hasSpace;
    }

    public void setLatlong(LatLng latlong) {
        this.Lat = latlong.latitude; this.Long = latlong.longitude;
    }

    public void setHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }
}
