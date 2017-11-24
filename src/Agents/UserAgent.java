package Agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.awt.geom.Point2D;
import java.util.Random;

public class UserAgent extends Agent {
    private String startingStation;
    private String endingStation;
    private Point2D position, endingPoint,startingPont;

    protected void setup() {
        //step 1 -> choose staring point
        //step 2 -> chosse ending point
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("station");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);
            Random rand = new Random();
            int chosen1 = 1;
            int chosen2 = 1;
            while (chosen1 == chosen2) {
                chosen1 = rand.nextInt(result.length);
                chosen2 = rand.nextInt(result.length);


            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }

        //step 3 -> start moving
        this.addBehaviour(new Mover(this,1000));


        //step 3.1 -> check progression

        this.addBehaviour(new PositionChecker(this,1000));

        //step 4 -> start pinging location

        //step 5 -> receive offers

        //step 6 -> adapt to offers



    }

    class PositionChecker extends TickerBehaviour{

        public PositionChecker(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

        }
    }

    class Mover extends TickerBehaviour {

        public Mover(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            double xdif=endingPoint.getX()-position.getX();
            double ydif=endingPoint.getX()-position.getY();
            xdif=xdif/20;
            ydif=ydif/20;
            position.setLocation(xdif,ydif);

        }

    }


    class myBehaviour extends SimpleBehaviour {
        private boolean finished = false;

        public myBehaviour(Agent a) {
            super(a);
        }

        public void action() {


            //...this is where the real programming goes !!
        }

        public boolean done() {
            return finished;
        }

    } // ----------- End myBehaviour

}//end class myAgent