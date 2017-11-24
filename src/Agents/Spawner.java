package Agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

//test
public class Spawner extends Agent {

    Map<Integer, Point> stations;

    @Override
    protected void setup() {
        super.setup();
        stations = new HashMap<>();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            int seats = rand.nextInt(10);



            try {
                System.out.println("Creating Station nº" + i + " , x:" + x + ", y:" + y);

                Object[] objs = new Object[3];
                objs[0] = x;
                objs[1] = y;
                objs[2] = seats;

                AgentController ag = this.getContainerController().createNewAgent("StationAgent_" + UUID.randomUUID(), "Agents.StationAgent", objs);
                ag.start();
                stations.put((stations.size()), new Point(x, y));
            } catch (Exception e) {

            }

        }

        Behaviour loop = new TickerBehaviour(this, 60000) //creates user every 5 minutes
        {
            protected void onTick() {
                System.out.println("Spawning new User:");
                try {
                    Random rand = new Random();
                    int chosen1 = 1;
                    int chosen2 = 1;
                    while (chosen1 == chosen2) {
                        chosen1 = rand.nextInt(stations.size());
                        chosen2 = rand.nextInt(stations.size());
                    }

                    Object[] objs = new Object[3];
                    objs[0] = new Point(stations.get(chosen1));
                    objs[1] = new Point(stations.get(chosen1));

                    AgentController ag = myAgent.getContainerController().createNewAgent("UserAgent_" + UUID.randomUUID(), "Agents.User", new Object[]{new Point(stations.get(chosen1)), new Point(stations.get(chosen2))});
                    ag.start();
                } catch (Exception e) {

                }
            }
        };

        addBehaviour(loop);
        System.out.println("Spawner Agent Initialized.");
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("User Spawner Terminated.");
    }


}
