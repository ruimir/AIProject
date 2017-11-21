import jade.core.Agent;
import jade.core.
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class myAgent extends Agent
{
    protected void setup()
    {
        addBehaviour( new myBehaviour( this ) );
    }


    class myBehaviour extends SimpleBehaviour
    {
        public myBehaviour(Agent a) {
            super(a);
        }

        public void action()
        {
            //...this is where the real programming goes !!
        }

        private boolean finished = false;

        public boolean done() {
            return finished;
        }

    } // ----------- End myBehaviour

}//end class myAgent