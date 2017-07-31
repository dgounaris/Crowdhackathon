package dgounaris.dev.sch.Bins;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DimitrisLPC on 16/5/2017.
 */

public class Bin {

    private String id;
    private LatLng latlong;
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
