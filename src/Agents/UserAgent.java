package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import sun.jvm.hotspot.runtime.Thread;

import java.awt.geom.Point2D;
import java.util.Random;

public class UserAgent extends Agent {
    DFAgentDescription[] result;
    private String startingStation;
    private String endingStation;
    private Point2D.Float position, endingPoint, startingPont;

    protected void setup() {
        //step 1 -> choose staring point
        //step 2 -> chosse ending point
        System.out.println("Staring User");
        UserParams args = (UserParams) getArguments()[0];

        position = args.startingPoint;
        startingPont = args.startingPoint;
        endingPoint = args.endingPoint;


        //step 3 -> start moving
        this.addBehaviour(new Mover(this, 1000));


        //step 3.1 -> check progression

        this.addBehaviour(new PositionChecker(this, 1000));

        //step 4 -> start pinging location -> check PingStations

        //step 5 -> receive offers -> Check Receiver

        //step 6 -> adapt to offers


    }

    class PositionChecker extends TickerBehaviour {

        public PositionChecker(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            double totalDistance = endingPoint.distance(startingPont);
            double done = position.distance(startingPont);
            double percentage = done / totalDistance;

            if (percentage > 0.75) {
                myAgent.addBehaviour(new PingStations(myAgent, 5000));
                this.stop();
            }

        }
    }

    class PingStations extends TickerBehaviour {

        public PingStations(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            for (int i = 0; i < result.length; i++) {
                ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
                mensagem.addReceiver(result[i].getName());
                mensagem.setContent("" + position.getX() + ";" + position.getY());
                myAgent.send(mensagem);
            }


        }

    }

    class Mover extends TickerBehaviour {

        public Mover(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            double xdif = endingPoint.getX() - position.getX();
            double ydif = endingPoint.getX() - position.getY();
            xdif = xdif / 20;
            ydif = ydif / 20;
            System.out.println("Moving from (" + position.getX() + "," + position.getY() + ") to (" + xdif + "," + ydif + ")");
            position.setLocation(xdif, ydif);

        }

    }

    private class Receiver extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = myAgent.receive();
            if (message != null) {
                if (message.getPerformative() == ACLMessage.PROPOSE) {
                    float offer = Float.parseFloat(message.getContent());
                    if (offer > 0.7) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        myAgent.send(reply);
                        //mudar posição final

                    } else if (offer < 0.4) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        myAgent.send(reply);
                    } else {
                        Random rand = new Random();
                        if (rand.nextInt(101) > 50) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                            myAgent.send(reply);
                        } else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            myAgent.send(reply);
                        }
                    }

                } else if (message.getPerformative() == ACLMessage.CONFIRM) {
                    if (message.getContent().equals("lift")) {

                    } else if (message.getContent().equals("drop")) {
                        myAgent.doWait(4000);
                    } else if (message.getPerformative() == ACLMessage.REFUSE) {
                        myAgent.doWait(4000);
                    }

                } else {
                    block();
                }

            }
        }


    }
}//end class myAgent