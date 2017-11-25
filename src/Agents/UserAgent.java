package Agents;

import Agents.Utils.Vec2;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class UserAgent extends Agent {
    DFAgentDescription[] result;
    float totalDistance;
    float speed = 16f; // speed in meters per second
    int refreshRate = 20;
    private String startingStation;
    private String endingStation;
    private Vec2 position, endingPoint, startingPont;
    private Vec2 movementDir;

    private boolean travelComplete = false;
    boolean acceptedProposal = false;

    protected void setup() {
        result = new DFAgentDescription[0];
        //step 1 -> choose staring point
        //step 2 -> chosse ending point
        System.out.println("Staring User");
        UserParams args = (UserParams) getArguments()[0];

        position = new Vec2(args.startingPoint.x, args.startingPoint.y);
        startingPont = new Vec2(position.x, position.y);
        setEndingPoint(new Vec2(args.endingPoint.x, args.endingPoint.y));
        startingStation = args.startingAgent;
        endingStation = args.endingAgent;

        //step 2.5 -> Set Movement direction
        setMovementDir();
        System.out.println(String.format("IP: %s, %s FP: %s, %s DIR: %s, %s", startingPont.x, startingPont.y, endingPoint.x, endingPoint.y, movementDir.x, movementDir.y));


        //step 3 -> start moving
        Behaviour movementLoop = new TickerBehaviour(this, refreshRate) {
            protected void onTick() {

                if(speed > 0 && !travelComplete) {
                    Vec2 delta = Vec2.multiply(movementDir, speed / refreshRate);
                    position.addMe(delta);
                }

                //step 3.1 -> check progression
                float completion = Vec2.subtract(position, startingPont).getLenght() / totalDistance;
                System.out.println(completion);
                if (completion > 0.75 && !travelComplete) {
                    System.out.println(completion + " || " + position.x + " " + position.y);
                    broadCastPosition(myAgent);

                }

                if(completion >= 1){
                    speed = 0;
                    position = endingPoint;
                    travelComplete = true;
                }


            }
        };
        addBehaviour(movementLoop);

        this.addBehaviour(new Receiver());


        // this.addBehaviour(new PositionChecker(this, 1000));

        //step 4 -> start pinging location -> check PingStations

        //step 5 -> receive offers -> Check Receiver

        //step 6 -> adapt to offers



    }

    void broadCastPosition(Agent myAgent){
        if (result.length == 0) {
            //Pesquisa Inicial de Estações
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("station");
            template.addServices(sd);
            try {
                result = DFService.search(myAgent, template);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < result.length; i++) {
            ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
            mensagem.addReceiver(result[i].getName());
            mensagem.setContent("" + position.getX() + ";" + position.getY());


            myAgent.send(mensagem);

        }
    }

    private void setMovementDir() {
        movementDir = Vec2.subtract(endingPoint, startingPont);
        movementDir.normalize();
    }

    private void setEndingPoint(Vec2 ep) {
        endingPoint = ep;
        Vec2 delta = Vec2.subtract(ep, startingPont);
        totalDistance = delta.getLenght();
    }

    class PingStations extends TickerBehaviour {

        public PingStations(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (result.length == 0) {
                //Pesquisa Inicial de Estações
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("station");
                template.addServices(sd);
                try {
                    result = DFService.search(myAgent, template);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < result.length; i++) {
                ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
                mensagem.addReceiver(result[i].getName());
                mensagem.setContent("" + position.getX() + ";" + position.getY());

                myAgent.send(mensagem);
            }


        }

    }


    private class Receiver extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = myAgent.receive();
            if (message != null ) {
                if (message.getPerformative() == ACLMessage.PROPOSE) {
                    //float offer = Float.parseFloat(message.getContent());
                    float offer = Float.parseFloat(message.getContent().split(";")[0]);
                    if (offer > 0.7 && !acceptedProposal) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                        //mudar posição e estacao finais

                        System.out.println("Aceitou");
                        acceptedProposal = true;
                        float newX = Float.parseFloat(message.getContent().split(";")[1]);
                        float newY = Float.parseFloat(message.getContent().split(";")[2]);
                        setEndingPoint(new Vec2(newX, newY));

                        myAgent.send(reply);

                    } else if (offer < 0.4) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        reply.setContent(position.x + ";" + position.y);
                        myAgent.send(reply);
                    } else {
                        Random rand = new Random();
                        if (rand.nextInt(101) > 50 && !acceptedProposal) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);


                            System.out.println("Aceitou");
                            acceptedProposal = true;
                            float newX = Float.parseFloat(message.getContent().split(";")[1]);
                            float newY = Float.parseFloat(message.getContent().split(";")[2]);
                            setEndingPoint(new Vec2(newX, newY));
                            myAgent.send(reply);

                        } else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            reply.setContent(position.x + ";" + position.y);
                            myAgent.send(reply);
                        }
                    }

                } else if (message.getPerformative() == ACLMessage.CONFIRM) {
                    if (message.getContent().equals("lift")) {

                    } else if (message.getContent().equals("drop")) {
                        myAgent.doDelete();
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