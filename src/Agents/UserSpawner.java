package Agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;

import java.util.UUID;

//test
public class UserSpawner extends Agent {
    @Override
    protected void setup(){
        super.setup();
        Behaviour loop = new TickerBehaviour( this, 60000 ) //creates user every 5 minutes
        {
            protected void onTick() {
                System.out.println("Spawning new User:");
                try {
                    AgentController ag = myAgent.getContainerController().createNewAgent("UserAgent_" + UUID.randomUUID() , "User", new Object[]{});
                    ag.start();
                }

                catch (Exception e){

                }
            }
        };

        addBehaviour( loop );
        System.out.println("Spawner Agent Initialized.");
    }

    @Override
    protected void takeDown(){
        super.takeDown();
        System.out.println("User Spawner Terminated.");
    }


}
