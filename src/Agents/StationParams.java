package Agents;

//Object used for creating a new station
public class StationParams {
    public float x;
    public float y;
    public int capacity;
    public int parkedBikes;
    public String name;


    public StationParams(float x, float y, int capacity, int parkedBikes) {
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.parkedBikes = parkedBikes;

    }
}
