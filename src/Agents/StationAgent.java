import jade.core.Agent;

import java.awt.geom.Point2D;

public class StationAgent extends Agent {

    //Agent Position
    public Point2D position;
    public  int capacity, load;


    @Override
    protected void setup() {
        super.setup();
    }

    public StationAgent(Point2D position, int capacity, int load) {
        this.position = position;
        this.capacity = capacity;
        this.load = load;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }





}
