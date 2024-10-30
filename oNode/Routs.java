/**
 * Esta classe serve para manter um mapa de rotas atualizado
 */

package oNode;

import java.util.HashMap;
import java.util.Map;

public class Routs {
    Map<String, Rout> routs;

    public Routs() {
        routs = new HashMap<>();
    }

    public String bestServer() {
        String IP = null;
        long delay = Long.MAX_VALUE;

        for (Map.Entry<String, Rout> entry : routs.entrySet()) {
            Rout rout = entry.getValue();
            if (rout.delay < delay) {
                delay = rout.delay;
                IP = entry.getKey();
            }
        }

        return IP;
    }

    public boolean insertRout(String IP, Rout rout) {
        if (!this.routs.containsKey(IP)) {
            this.routs.put(IP, rout);
            return true;
        }

        Rout temp = this.routs.get(IP);

        // A rota é a mesma, mas o delay mudou
        if (rout.nJumps == temp.nJumps && rout.previousIP.equals(temp.previousIP)) {
            this.routs.put(IP, rout);
            return true;
        }
        // É importante atualizar para depois decidir qual usar

        if (rout.delay < temp.delay) {
            this.routs.put(IP, rout);
            return true;
        }

        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Rout> entry : routs.entrySet()) {
            sb.append("Servidor: ").append(entry.getKey()).append("\n").append(entry.getValue().toString())
                    .append("\n");
        }
        return sb.toString();
    }
}
