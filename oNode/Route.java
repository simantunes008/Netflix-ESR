import java.net.InetAddress;

public class Route{
    private InetAddress origin;
    private InetAddress previous;
    private int cost;

    //Route's constructor
    public Route(InetAddress origin, InetAddress previous, int cost){
        this.origin = origin;
        this.previous = previous;
        this.cost = cost;
    }
}