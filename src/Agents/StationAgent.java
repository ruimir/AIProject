package Agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class StationAgent extends Agent {

    //Agent Position
    public Point position;
    public int capacity, load;


    public StationAgent() {
        Random rand = new Random();
        this.position = new Point(rand.nextInt(100),rand.nextInt(100));
        capacity=rand.nextInt(10);
        load=0;

    }

    public StationAgent(Point position, int capacity, int load) {
        this.position = position;
        this.capacity = capacity;
        this.load = 0;
    }

    @Override
    protected void setup() {
        System.out.println("Staring Station");
        super.setup();
        registerInDF();
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

    public void setPosition(Point position) {
        this.position = position;
    }

    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("station");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void takeDown() {
        System.out.println("Ending Station");
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();

    }


}
