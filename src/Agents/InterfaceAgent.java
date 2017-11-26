package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Scanner;


public class InterfaceAgent extends Agent {
    @Override
    protected void setup() {
        super.setup();

        System.out.println(this.getLocalName() + "a começar!");

        this.addBehaviour(new ReceiveBehaviour());
        // this.addBehaviour(new ReceiveBehaviour());
        System.out.println("Bem vindo!");
        System.out.println("Sistema de Partilha de Bicicletas - Interface");
        System.out.println("Prima alguma tecla para continuar:");
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        this.addBehaviour(new AskStation());


    }

    @Override
    protected void takeDown() {
        super.takeDown();

        System.out.println(this.getLocalName() + "a morrer...");
    }


    public class AskStation extends OneShotBehaviour {


        @Override
        public void action() {
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            AID receiver = new AID();
            receiver.setLocalName("ControllerAgent");
            message.addReceiver(receiver);
            myAgent.send(message);
        }
    }

    public class ReceiveBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            //Actions behavious will do - receive message
            ACLMessage msg = receive();
            if (msg != null) {

                if (msg.getPerformative() == ACLMessage.PROPAGATE) {
                    //Content: Lista de estações
                    String[] stations = msg.getContent().split(";");
                    for (int i = 0; i < stations.length; i++) {
                        System.out.println("Estação nº" + i + " :" + stations[i]);
                    }
                    System.out.println("Escolha um a estação para obter mais informação:");
                    Scanner sc = new Scanner(System.in);
                    int i = sc.nextInt();
                    while (i < 0 && i >= stations.length) {
                        System.out.println("Opção errada, tente novamente:");
                        i = sc.nextInt();
                    }
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.SUBSCRIBE);
                    reply.setContent(stations[i]);
                    myAgent.send(reply);
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    //Info de Estação
                    System.out.println("Informação de Estação:");
                    System.out.println(msg.getContent());
                    myAgent.addBehaviour(new AskStation());
                }
            } else {
                block();
            }
        }

    }
}//end class myAgent

