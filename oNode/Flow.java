/**
 * Classe para representar um fluxo de streaming
 * */

package oNode;

import java.util.Set;

public class Flow {
    String source;
    String previousIP;
    public Set<String> targets;

    public Flow(String source, String previousIP, Set<String> targets) {
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
