package oNode;

import java.util.ArrayList;
import java.util.List;

public class Flows {
    public List<Flow> flows;

    public Flows() {
        this.flows = new ArrayList<>();
    }

    private boolean exists(String source) {
        for (Flow flow : flows) {
            if (flow.source.equals(source)) {
                return true;
            }
        }
        return false;
    }

    public void addFlow(String source, String previousIP, String nextIP) {
        if (!exists(source)) {
            List<String> targets = new ArrayList<>();
            targets.add(previousIP);
            this.flows.add(new Flow(source, nextIP, targets));
        } else {
            for (Flow flow : flows) {
                if (flow.source.equals(source)) {
                    flow.targets.add(previousIP);
                }
            }
        }
    }

    public void addFlowServer(String IP) {
        if (this.flows.isEmpty()){
            List<String> targets = new ArrayList<>();
            targets.add(IP);
            this.flows.add(new Flow("", "", targets));
        } else {
            if (this.flows.get(0).targets.contains(IP)){
                return;
            } else{
                this.flows.get(0).targets.add(IP);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Flow flow : flows) {
            sb.append(flow.toString()).append("\n");
        }
        return sb.toString();
    }
}
