package dgounaris.dev.sch.People;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class Service {

    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("slots")
    private int slots;
    @SerializedName("points_needed")
    private int points_needed;

    public Service(long id, String name, int slots, int points_needed) {
        this.id = id;
        this.name = name;
        this.slots = slots;
        this.points_needed = points_needed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setPoints_needed(int points_needed) {
        this.points_needed = points_needed;
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }

    public int getPoints_needed() {
        return points_needed;
    }

    public long getId() {
        return id;
    }
}
