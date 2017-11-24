import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;


public class InterfaceAgent extends Agent
{
    @Override
    protected void setup()
    {
        super.setup();

        System.out.println(this.getLocalName()+"a começar!");

        this.addBehaviour(new ReceiveBehaviour());
    }

    @Override
    protected void takeDown()
    {
        super.takeDown();

        System.out.println(this.getLocalName()+"a morrer...");
    }

    public class SenderAsk extends Agent{

        @Override
        protected void setup() {

            super.setup();

            this.addBehaviour(new ReceiveBehaviour());
            System.out.println("Olá e bem vindo");
            obter a listas dos agentes
                    selecione uma etscao paa ver info
                    input/escolha de uma estacao
                    envia mensagem para obter info
                    recebe info

            this.addBehaviour(new SendMessage(this,60000));

        }

        public class SendMessage extends TickerBehaviour {

            public SendMessage(Agent agent, long timeout)
        }
    }


    public class ReceiveBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            //Actions behavious will do - receive message
            ACLMessage msg = receive();
            if (msg != null) {

                if (msg.getPerformative()==0){

                    System.out.println("Recebi uma mensagem de" + msg.getSender() + "Quais as estações mais vazias?");
                }else System.out.println("Recebi uma mensagem de" + msg.getSender() + "Não há estaçōes vazias");
            }
            block();
        }

    }
}//end class myAgent

