/**
 * Classe para representar o Conjunto de fluxos presentes num node
 * */

package oNode;

import java.util.*;

public class Flows {
    public Map<Integer, Flow> flows;

    public Flows() {
        this.flows = new HashMap<>();
    }

    public void addFlow(Integer id, String source, String previousIP, String nextIP) {
        if (!this.flows.containsKey(id)) {
            Set<String> targets = new HashSet<>();
            targets.add(previousIP);
            this.flows.put(id, new Flow(source, nextIP, targets));
        } else {
            Flow flow = this.flows.get(id);
            flow.targets.add(previousIP);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Flow> entry : flows.entrySet()) {
            sb.append("ID do vídeo: ").append(entry.getKey()).append("\n").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}
