package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControllerAgent extends Agent {

    //fazer classe stationMetrics
    Map<String, StationMetrics> agentinfo;


    @Override
    protected void setup() {
        super.setup();
        this.agentinfo = new HashMap<>();
    }


    @Override
    protected void takeDown() {
        super.takeDown();
    }


    //to process messages
    private class Receiver extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    //inteface deve pedir pelas estações existentes primeiro!
                    Iterator<String> it = agentinfo.keySet().iterator();
                    StringBuilder sb = new StringBuilder();
                    while (it.hasNext()) {
                        sb.append(it.next()).append(";");
                    }
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPAGATE);
                    reply.setContent(sb.toString());
                    myAgent.send(reply);


                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    //quando recebe info de estação -> update info
                    String[] split = msg.getContent().split(";");
                    float occupationRate = Float.parseFloat(split[0]);
                    int spaces = Integer.parseInt(split[1]);
                    int offersAccepted = Integer.parseInt(split[2]);
                    int offersRejected = Integer.parseInt(split[3]);
                    agentinfo.put(msg.getSender().getLocalName(), new StationMetrics(occupationRate, spaces, offersAccepted, offersRejected));

                } else if (msg.getPerformative() == ACLMessage.SUBSCRIBE) {
                    //quando recebe pedidos -> devolver info
                    if (agentinfo.containsKey(msg.getContent())) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(agentinfo.get(msg.getContent()).toString());
                        myAgent.send(reply);

                    } else {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(agentinfo.get(msg.getContent()).toString());
                        myAgent.send(reply);
                    }


                }

            } else {
                block();
            }


        }


    }


}
