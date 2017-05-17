package dgounaris.dev.sch.People;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

public class Service {

    private int id;
    private String name;
    private int slots;
    private int points_needed;

    public Service(int id, String name, int slots, int points_needed) {
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

    public int getId() {
        return id;
    }
}
