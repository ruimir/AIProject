package Agents;

public class StationMetrics {
    public float occupationRate;
    public int spaces;
    public int parkedbikes;
    public int offersAccepted;
    public int offersRejected;


    public StationMetrics(float occupationRate, int spaces, int parkedbikes, int offersAccepted, int offersRejected) {
        this.occupationRate = occupationRate;
        this.spaces = spaces;
        this.parkedbikes = parkedbikes;
        this.offersAccepted = offersAccepted;
        this.offersRejected = offersRejected;
    }

    public float getOccupationRate() {
        return occupationRate;
    }

    public void setOccupationRate(float occupationRate) {
        this.occupationRate = occupationRate;
    }

    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }

    public int getOffersAccepted() {
        return offersAccepted;
    }

    public void setOffersAccepted(int offersAccepted) {
        this.offersAccepted = offersAccepted;
    }

    public int getOffersRejected() {
        return offersRejected;
    }

    public void setOffersRejected(int offersRejected) {
        this.offersRejected = offersRejected;
    }

    @Override
    public String toString() {
        return "Métricas:\n" +
                "Taxa de Ocupação =" + occupationRate +
                "\n Espaços Disponíveis=" + spaces +
                "\n Espaços Ocupados=" + parkedbikes +
                "\n Ofertas Aceites=" + offersAccepted +
                "\n Ofertas Rejeitadas=" + offersRejected
                ;
    }
}
