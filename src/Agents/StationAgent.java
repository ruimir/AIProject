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

public class StationAgent extends Agent {

    //Station Position x and y on the ambient
    float x;
    float y;
    int capacity; //how many bikes can this  station fit.
    int parkedBikes; //howm many bikes are currently on station
    int offersAccepted; //how many offers has this station been accepted
    int offersRejected; //how many offers has this station been rejectes


    @Override
    protected void setup() {
        System.out.println("Starting Station...");

        super.setup();
        StationParams myParams = (StationParams) getArguments()[0];
        this.x = myParams.x;
        this.y = myParams.y;
        this.capacity = myParams.capacity;
        this.parkedBikes = myParams.parkedBikes;
        this.offersAccepted = 0;
        this.offersRejected = 0;

        System.out.println(String.format("Station at (%s, %s) initialized with %s bikes of %s.", x, y, parkedBikes, capacity));

        registerInDF();
        this.addBehaviour(new SendMetrics(this, 10000));
        this.addBehaviour(new Receiver());
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
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            float occupationRate = (float) parkedBikes / (float) capacity;
            msg.setContent("" + occupationRate + ";" + capacity + ";" + offersAccepted + ";" + offersRejected);
            AID receiver = new AID();
            receiver.setLocalName("ControllerAgent");
            msg.addReceiver(receiver);

            send(msg);

        }
    }

    private class Receiver extends CyclicBehaviour {

        private double createOffer(double reduction) {
            double rate = (double) parkedBikes / (double) capacity; //
            double inverse = 1 - rate;
            return inverse * reduction;
        }

        @Override
        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                if (message.getPerformative() == ACLMessage.REQUEST) {
                    if (message.getContent().equals("lift")) {
                        if (parkedBikes != 0) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.CONFIRM);
                            reply.setContent("lift");
                            parkedBikes++;
                            myAgent.send(reply);
                        } else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REFUSE);
                            myAgent.send(reply);
                        }
                    } else if (message.getContent().equals("drop")) {
                        if (parkedBikes < capacity) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.CONFIRM);
                            reply.setContent("drop");
                            parkedBikes--;
                            myAgent.send(reply);
                        } else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REFUSE);
                            myAgent.send(reply);
                        }
                    }

                }
                float unit = UserSpawnerAgent.gridUnitDistance;
                int nRange = 1;

                if (message.getPerformative() == ACLMessage.INFORM) {
                    String[] split = message.getContent().split(";");
                    float userX = Float.parseFloat(split[0]);
                    float userY = Float.parseFloat(split[1]);


                    if (userX < (x + nRange * unit) && userX > (x - nRange * unit) &&
                            userY < (y + nRange * unit) && userY > (y - nRange * unit)) {
                        //Propor oferta
                        System.out.println(myAgent.getName() + "is proposing.");
                        double offer = createOffer(0.7);
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(offer + ";" + x + ";" + y);
                        myAgent.send(reply);
                    }


                } else if (message.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                    String[] split = message.getContent().split(";");
                    float userX = Float.parseFloat(split[0]);
                    float userY = Float.parseFloat(split[1]);
                    if (userX < (x + nRange * unit) && userX > (x - nRange * unit) &&
                            userY < (y + nRange * unit) && userY > (y - nRange * unit)) {
                        //Propor oferta
                        double offer = createOffer(0.9);
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(offer + ";" + x + ";" + y);
                        myAgent.send(reply);
                    }
                }

            } else {
                block();
            }


        }


    }
}



