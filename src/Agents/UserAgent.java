package Agents;

import Agents.Utils.Vec2;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserAgent extends Agent {
    DFAgentDescription[] result;
    float totalDistance;
    float speed = 16f; // speed in meters per second
    int refreshRate = 20;
    boolean travelAuth = false;
    boolean acceptedProposal = false;
    private String startingStation;
    private String endingStation;
    private Vec2 position, endingPoint, startingPoint;
    private Vec2 movementDir;
    private boolean travelComplete = false;
    private boolean notfiedStation = false;
    private Map<String, Integer> tries;

    protected void setup() {
        result = new DFAgentDescription[0];
        //step 1 -> choose staring point
        //step 2 -> chosse ending point
        System.out.println("Staring User");
        UserParams args = (UserParams) getArguments()[0];

        position = new Vec2(args.startingPoint.x, args.startingPoint.y);
        startingPoint = new Vec2(position.x, position.y);
        setEndingPoint(new Vec2(args.endingPoint.x, args.endingPoint.y));
        startingStation = args.startingAgent;
        endingStation = args.endingAgent;
        this.tries = new HashMap<>();

        //step 2.5 -> Set Movement direction
        setMovementDir();


        System.out.println(String.format("IP: %s, %s FP: %s, %s DIR: %s, %s", startingPoint.x, startingPoint.y, endingPoint.x, endingPoint.y, movementDir.x, movementDir.y));

        //step 2.75 -> notify agent it is lifting bike
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("lift");
        AID receiver = new AID();
        receiver.setName(startingStation);
        message.addReceiver(receiver);
        send(message);

        //step 3 -> start moving

 /*       if (travelAuth) {
            Behaviour movementLoop = new TickerBehaviour(this, refreshRate) {
                protected void onTick() {

                    if (speed > 0 && !travelComplete) {
                        Vec2 delta = Vec2.multiply(movementDir, speed / refreshRate);
                        position.addMe(delta);
                    }

                    //step 3.1 -> check progression
                    float completion = Vec2.subtract(position, startingPoint).getLenght() / totalDistance;
                    //System.out.println(completion);
                    if (completion > 0.75 && !travelComplete) {
                        System.out.println(completion + " || " + position.x + " " + position.y);
                        broadCastPosition(myAgent);

                    }

                    if (completion >= 1) {
                        speed = 0;
                        position = endingPoint;
                        travelComplete = true;

                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        message.setContent("drop");
                        AID receiver = new AID();
                        receiver.setName(endingStation);
                        message.addReceiver(receiver);
                        myAgent.send(message);


                    }


                }
            };
            addBehaviour(movementLoop);

            this.addBehaviour(new Receiver());
        }*/

        addBehaviour(new Receiver());

        // this.addBehaviour(new PositionChecker(this, 1000));

        //step 4 -> start pinging location -> check PingStations

        //step 5 -> receive offers -> Check Receiver

        //step 6 -> adapt to offers


    }

    void broadCastPosition(Agent myAgent) {
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
        movementDir = Vec2.subtract(endingPoint, startingPoint);
        movementDir.normalize();
    }

    private void setEndingPoint(Vec2 ep) {
        endingPoint = ep;
        Vec2 delta = Vec2.subtract(ep, startingPoint);
        totalDistance = delta.getLenght();
    }


    private class Receiver extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage message = myAgent.receive();
            if (message != null) {
                if (message.getPerformative() == ACLMessage.PROPOSE) {
                    //float offer = Float.parseFloat(message.getContent());
                    float offer = Float.parseFloat(message.getContent().split(";")[0]);
                    if (offer > 0.7 && !acceptedProposal) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                        //mudar posição e estacao finais

                        System.out.println("Aceitou -> Boa Oferta");
                        acceptedProposal = true;
                        float newX = Float.parseFloat(message.getContent().split(";")[1]);
                        float newY = Float.parseFloat(message.getContent().split(";")[2]);
                        setEndingPoint(new Vec2(newX, newY));
                        endingStation = message.getSender().getName();

                        myAgent.send(reply);

                    } else if (offer < 0.4) {
                        ACLMessage reply = message.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        boolean toban = false;
                        if (!tries.containsKey(message.getSender().getName())) {
                            tries.put(message.getSender().getName(), 1);
                        } else {
                            int attempt = tries.get(message.getSender().getName());
                            if (attempt > 3) {
                                toban = true;
                                attempt++;
                                tries.put(message.getSender().getName(), attempt);
                            } else {
                                attempt++;
                                tries.put(message.getSender().getName(), attempt);
                            }
                        }
                        reply.setContent(position.x + ";" + position.y + ";" + toban);
                        myAgent.send(reply);
                    } else {
                        Random rand = new Random();
                        if (rand.nextInt(101) > 50 && !acceptedProposal) {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

                            System.out.println("Aceitou -> Aletatório");
                            acceptedProposal = true;
                            float newX = Float.parseFloat(message.getContent().split(";")[1]);
                            float newY = Float.parseFloat(message.getContent().split(";")[2]);
                            setEndingPoint(new Vec2(newX, newY));
                            endingStation = message.getSender().getName();
                            myAgent.send(reply);

                        } else {
                            ACLMessage reply = message.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            boolean toban = false;
                            if (!tries.containsKey(message.getSender().getName())) {
                                tries.put(message.getSender().getName(), 1);
                            } else {
                                int attempt = tries.get(message.getSender().getName());
                                if (attempt > 3) {
                                    toban = true;
                                    attempt++;
                                    tries.put(message.getSender().getName(), attempt);
                                } else {
                                    attempt++;
                                    tries.put(message.getSender().getName(), attempt);
                                }
                            }
                            reply.setContent(position.x + ";" + position.y + ";" + toban);
                            myAgent.send(reply);
                        }
                    }

                } else if (message.getPerformative() == ACLMessage.CONFIRM) {
                    if (message.getContent().equals("lift")) {

                        Behaviour movementLoop = new TickerBehaviour(myAgent, refreshRate) {
                            protected void onTick() {

                                if (speed > 0 && !travelComplete) {
                                    Vec2 delta = Vec2.multiply(movementDir, speed / refreshRate);
                                    position.addMe(delta);
                                }

                                //step 3.1 -> check progression
                                float completion = Vec2.subtract(position, startingPoint).getLenght() / totalDistance;
                                //System.out.println(completion);
                                if (completion > 0.75 && !travelComplete) {
                                    //System.out.println(completion + " || " + position.x + " " + position.y);
                                    broadCastPosition(myAgent);
                                }

                                if (completion >= 1) {
                                    speed = 0;
                                    position = endingPoint;
                                    travelComplete = true;

                                    if (!notfiedStation) {
                                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                        message.setContent("drop");
                                        AID receiver = new AID();
                                        receiver.setName(endingStation);
                                        message.addReceiver(receiver);
                                        myAgent.send(message);
                                        notfiedStation = true;
                                    }
                                }
                            }
                        };

                        myAgent.addBehaviour(movementLoop);


                    } else if (message.getContent().equals("drop")) {
                        myAgent.doDelete();
                    }

                } else if (message.getPerformative() == ACLMessage.REFUSE) {
                    System.out.println("Refused!");
                    myAgent.doWait(4000);
                    if (message.getContent().equals("lift")) {
                        ACLMessage try2 = new ACLMessage(ACLMessage.REQUEST);
                        try2.setContent("lift");
                        AID receiver = new AID();
                        receiver.setName(startingStation);
                        try2.addReceiver(receiver);
                        send(try2);
                    } else if (message.getContent().equals("drop")) {
                        //Para assim voltar a chatear a station
                        notfiedStation = false;
                    }


                } else {
                    block();
                }

            }
        }


    }
}//end class myAgent