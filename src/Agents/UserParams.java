package Agents;

import java.awt.geom.Point2D;

public class UserParams {

    public Point2D.Float startingPoint,endingPoint;
    public String startingAgent,endingAgent;

    public UserParams(Point2D.Float startingPoint, Point2D.Float endingPoint, String startingAgent, String endingAgent) {
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.startingAgent = startingAgent;
        this.endingAgent = endingAgent;
    }
}
