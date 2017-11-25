package Agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

//test
public class UserSpawnerAgent extends Agent {

    Map<Integer, Point2D.Float> stations;

    @Override
    protected void setup() {
        super.setup();
        stations = new HashMap<>();
        int stationRowsNo = 8;
        int stationColumnsNo = 8;
        float gridUnitDistance = 100f;

        for (int i = 0; i < stationRowsNo; i++) {
            for (int j = 0; j < stationColumnsNo; j++) {
                float x = j * gridUnitDistance;
                float y = i * gridUnitDistance;

                StationParams stationEx = new StationParams(x, y, 30, 15);
                try {
                    AgentController ag = this.getContainerController().createNewAgent("Station_" + i + "_" + j, "Agents.StationAgent", new Object[]{(Object) stationEx});
                    ag.start();
                    stations.put((stations.size()), new Point2D.Float(x, y));
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }


        Behaviour loop = new TickerBehaviour(this, 10000) //creates user every 5 minutes
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
                    UserParams up = new UserParams(stations.get(chosen1), stations.get(chosen2));


                    AgentController ag = myAgent.getContainerController().createNewAgent("UserAgent_" + UUID.randomUUID(), "Agents.UserAgent", new Object[]{(Object) up});
                    ag.start();
                } catch (Exception e) {

                }
            }
        };

        addBehaviour(loop);
        System.out.println("UserSpawnerAgent Agent Initialized.");
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("User UserSpawnerAgent Terminated.");
    }


}
