package oNode;

public class Rout {
    String previousIP;
    int jumps;
    long delay;

    public Rout(String previousIP, int jumps, long delay) {
        this.previousIP = previousIP;
        this.jumps = jumps;
        this.delay = delay;
    }

    public String toString() {
        return previousIP + ":" + jumps + ":" + delay;
    }
}
