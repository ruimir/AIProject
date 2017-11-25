package Agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class StationAgent extends Agent {

    //Station Position x and y on the ambient
    float x;
    float y;
    int capacity; //how many bikes can this  station fit.
    int parkedBikes; //howm many bikes are currently on station



    @Override
    protected void setup() {
        System.out.println("Starting Station...");

        super.setup();
        StationParams myParams = (StationParams) getArguments()[0];
        this.x = myParams.x;
        this.y = myParams.y;
        this.capacity = myParams.capacity;
        this.parkedBikes = myParams.parkedBikes;

        System.out.println(String.format("Station at (%s, %s) initialized with %s bikes of %s.",x,y,parkedBikes,capacity ));

        registerInDF();
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
        System.out.println("Finishing Station...");
        try {
            DFService.deregister(this);
            System.out.println(getName() + " finished with success.");
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();

    }


}
