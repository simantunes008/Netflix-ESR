package oNode;

import java.util.List;

public class Flow {
    String source;
    String previousIP;
    public List<String> targets;

    public Flow(String source, String previousIP, List<String> targets) {
        this.source = source;
        this.previousIP = previousIP;
        this.targets = targets;
    }

    public String toString() {
        return "Fonte: " + source + "\n" +
                "Nodo anterior: " + previousIP + "\n" +
                "Destinos: " + targets + "\n" +
                "----------------------------------";
    }
}
