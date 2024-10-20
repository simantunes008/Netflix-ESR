import java.util.List;

public class Flow{
    private String ip_server;
    private String ip_previous;
    private List<String> destinations;
    private boolean on;
    private Map<String, Route> routes_table;

    //Flow's constructor
    public Flow(String ip_server, String ip_previous, List<String> destinations, boolean on){
        this.ip_server = ip_server;
        this.ip_previous = ip_server;
        this.destinations = destinations;
        this.on = on;
        this.routes_table = new HashMap<>(); 
    }

}