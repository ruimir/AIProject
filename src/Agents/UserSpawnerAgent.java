package Agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class UserSpawnerAgent extends Agent {

    Map<Integer, StationParams> stations;
    public static int stationRowsNo = 8;
    public static int stationColumnsNo = 8;
    public static float gridUnitDistance = 100f;

    @Override
    protected void setup() {
        super.setup();
        stations = new HashMap<>();


        for (int i = 0; i < stationRowsNo; i++) {
            for (int j = 0; j < stationColumnsNo; j++) {
                float x = j * gridUnitDistance;
                float y = i * gridUnitDistance;

                Random rand = new Random();
                int capacity=rand.nextInt(30);
                int parked=rand.nextInt(capacity+1);

                StationParams stationEx = new StationParams(x, y, capacity, parked);
                try {
                    AgentController ag = this.getContainerController().createNewAgent("Station_" + i + "_" + j, "Agents.StationAgent", new Object[]{(Object) stationEx});
                    stationEx.name = ag.getName();
                    ag.start();
                    stations.put((stations.size()), stationEx);
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            AgentController IA = this.getContainerController().createNewAgent("InterfaceAgent", "Agents.InterfaceAgent", null);
            IA.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


        Behaviour loop = new TickerBehaviour(this, 500) //creates user every second
        {
            protected void onTick() {
                SpawnUser(myAgent);
            }
        };

        Behaviour spaw1user = new SimpleBehaviour() {
            @Override
            public void action() {
                SpawnUser(myAgent);
            }

            @Override
            public boolean done() {
                return true;
            }
        };

        addBehaviour(loop);
        //addBehaviour(spaw1user);
        System.out.println("UserSpawnerAgent Agent Initialized.");
    }

    void SpawnUser(Agent myAgent){
        System.out.println("Spawning new User:");
        try {
            Random rand = new Random();
            int chosen1 = 1;
            int chosen2 = 1;

            while (chosen1 == chosen2) {
                chosen1 = rand.nextInt(stations.size());
                chosen2 = rand.nextInt(stations.size());
            }

            StationParams stationEx1 = stations.get(chosen1);
            StationParams stationEx2 = stations.get(chosen2);

            UserParams up = new UserParams(new Point2D.Float(stationEx1.x, stationEx1.y), new Point2D.Float(stationEx2.x, stationEx2.y), stationEx1.name, stationEx2.name);

            AgentController ag = myAgent.getContainerController().createNewAgent("UserAgent_" + UUID.randomUUID(), "Agents.UserAgent", new Object[]{(Object) up});
            ag.start();

        } catch (Exception e) {

        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        System.out.println("User UserSpawnerAgent Terminated.");
    }


}
