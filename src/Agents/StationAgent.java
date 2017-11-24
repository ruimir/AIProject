package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class StationAgent extends Agent {

    //Agent Position
    public Point position;
    public int capacity, load;
    public int offersAccepted;
    public int offersRejected;


    public StationAgent() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            this.position = new Point((int) args[0],(int) args[1]);
            capacity = (int) args[2];
        }
        load = 0;
        offersAccepted=0;
        offersRejected=0;

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


    private class SendMetrics extends TickerBehaviour {

        public SendMetrics(Agent a, long period) {
            super(a, period);
        }


        @Override
        protected void onTick() {
            ACLMessage mensagem = new ACLMessage(ACLMessage.CONFIRM);
            AID receiver = new AID();
            receiver.setLocalName("ControllerAgent");
            mensagem.addReceiver(receiver);
            mensagem.setContent(""+load + ";" + capacity + ";" + offersAccepted + ";"+offersRejected);
            myAgent.send(mensagem);

        }
    }

    private class Receiver extends CyclicBehaviour {

        @Override
        public void action() {

        }
    }


}
