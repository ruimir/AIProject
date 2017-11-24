import Agents.StationParams;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {

    Runtime rt;
    ContainerController container;

    static int stationRowsNo = 8;
    static int stationColumnsNo = 8;
    static float gridUnitDistance = 100f;

    public static void main(String[] args) {
        MainContainer a = new MainContainer();

        a.initMainContainerInPlatform("localhost", "9888", "MainContainer");
        //a.startAgentInPlatform("Station1", "Agents.StationAgent");
        //a.startAgentInPlatform("User1", "Agents.UserAgent");
        //a.startAgentInPlatform("User1", "Agents.UserAgent");

        for(int i = 0; i < stationRowsNo; i++){
            for(int j = 0; j < stationColumnsNo; j++){
                float x = stationColumnsNo * gridUnitDistance;
                float y = stationRowsNo * gridUnitDistance;

                StationParams stationEx = new StationParams(x, y,30,15);
                a.startStationAgent("Station_" + i + "_" + j, "Agents.StationAgent", stationEx);
            }
        }


        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.startAgentInPlatform("Taxi1", "Taxi");
        a.startAgentInPlatform("Taxi2", "Taxi");
        a.startAgentInPlatform("Taxi3", "Taxi");

        a.startAgentInPlatform("Manager", "Manager");

        while (true) {
            try {
                a.startAgentInPlatform("Customer", "Customer");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
*/








		/*
        // Example of Container Creation (not the main container)
		ContainerController newcontainer = a.initContainerInPlatform("localhost", "9888", "OtherContainer");

		// Example of Agent Creation in new container
		try {
			AgentController ag = newcontainer.createNewAgent("agentnick", "ReceiverAgent", new Object[] {});// arguments
			ag.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		*/
    }

    public ContainerController initContainerInPlatform(String host, String port, String containerName) {
        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        // create a non-main agent container
        ContainerController container = rt.createAgentContainer(profile);
        return container;
    }

    public void initMainContainerInPlatform(String host, String port, String containerName) {

        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile prof = new ProfileImpl();
        prof.setParameter(Profile.CONTAINER_NAME, containerName);
        prof.setParameter(Profile.MAIN_HOST, host);
        prof.setParameter(Profile.MAIN_PORT, port);
        prof.setParameter(Profile.MAIN, "true");
        prof.setParameter(Profile.GUI, "true");

        // create a main agent container
        this.container = rt.createMainContainer(prof);
        rt.setCloseVM(true);

    }

    public void startAgentInPlatform(String name, String classpath) {
        try {
            AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
            ac.start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startStationAgent(String name, String classpath, StationParams params) {
        try {
            AgentController ac = container.createNewAgent(name, classpath, new Object[]{(Object)params});
            ac.start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

