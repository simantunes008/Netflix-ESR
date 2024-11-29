/**
 * Classe para representar o Conjunto de fluxos presentes num node
 * */

package oNode;

import java.util.*;

public class Flows {
    public Map<String, Flow> flows;

    public Flows() {
        this.flows = new HashMap<>();
    }

    public void addFlow(String content, String source, String previousIP, String nextIP) {
        if (!this.flows.containsKey(content)) {
            Set<String> targets = new HashSet<>();
            targets.add(previousIP);
            this.flows.put(content, new Flow(source, nextIP, targets));
        } else {
            Flow flow = this.flows.get(content);
            flow.targets.add(previousIP);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Flow> entry : flows.entrySet()) {
            sb.append("Conteúdo: ").append(entry.getKey()).append("\n").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}