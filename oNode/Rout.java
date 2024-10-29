package oNode;

public class Rout {
    String previousIP;
    int nJumps;
    long delay;

    public Rout(String previousIP, int jumps, long delay) {
        this.previousIP = previousIP;
        this.nJumps = jumps;
        this.delay = delay;
    }

    public String toString() {
        return "Nodo anterior: " + previousIP + "\n" +
                "Nº de saltos: " + nJumps + "\n" +
                "Delay: " + delay + "ms\n" +
                "----------------------------------";
    }
}
