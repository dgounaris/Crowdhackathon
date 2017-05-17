package dgounaris.dev.sch.Bins;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DimitrisLPC on 16/5/2017.
 */

public class Bin {

    private int index;
    private LatLng latlong;
    private boolean hasSpace;

    public Bin(int index, LatLng latlong, boolean hasSpace) {
        this.index = index;
        this.latlong = latlong;
        this.hasSpace = hasSpace;
    }

    public int getIndex() {
        return index;
    }

    public LatLng getLatlong() {
        return latlong;
    }

    public boolean isHasSpace() {
        return hasSpace;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLatlong(LatLng latlong) {
        this.latlong = latlong;
    }

    public void setHasSpace(boolean hasSpace) {
        this.hasSpace = hasSpace;
    }
}
