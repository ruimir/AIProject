package Agents;

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

public class UserAgent extends Agent {
    DFAgentDescription[] result;
    private String startingStation;
    private String endingStation;
    private Point2D position, endingPoint, startingPont;

    protected void setup() {
        //step 1 -> choose staring point
        //step 2 -> chosse ending point
        System.out.println("Staring User");
        Object[] args = getArguments();
        position=new Point((Point) args[0]);
        startingPont=new Point((Point) args[0]);
        endingPoint=new Point((Point) args[1]);


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
            position.setLocation(xdif, ydif);

        }

    }

    private class Reveiver extends CyclicBehaviour {

        @Override
        public void action() {

        }
    }


}//end class myAgent