import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;

import java.util.UUID;

public class UserSpawner extends Agent {
    @Override
    protected void setup(){
        super.setup();
        Behaviour loop = new TickerBehaviour( this, 50000 ) //creates user every 5 minutes
        {
            protected void onTick() {
                System.out.println("New User Spawned");
                try {
                    AgentController ag = myAgent.getContainerController().createNewAgent("UserAgent_" + UUID.randomUUID() , "User", new Object[]{});
                    ag.start();
                }

                catch (Exception e){

                }
            }
        };

        addBehaviour( loop );
        System.out.println("Spawner agent initialized.");
    }

    @Override
    protected void takeDown(){
        super.takeDown();
        System.out.println("User agent terminated.");
    }


}
