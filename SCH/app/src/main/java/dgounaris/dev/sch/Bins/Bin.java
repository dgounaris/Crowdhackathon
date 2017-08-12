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
    private int space;

    public Bin(long id, double Lat, double Long, int space) {
        this.id = id;
        this.Lat = Lat;
        this.Long = Long;
        this.space = space;
    }

    public long getId() {
        return id;
    }

    public LatLng getLatlong() {
        return new LatLng(Lat, Long);
    }

    public boolean hasSpace() {
        return space>0;
    }

    public void setLatlong(LatLng latlong) {
        this.Lat = latlong.latitude; this.Long = latlong.longitude;
    }

    public void setSpace(int space) {
        this.space = space;
    }
}
