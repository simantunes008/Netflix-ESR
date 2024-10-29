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

    int insertRout(String IP, Rout rout) {
        if (!this.routs.containsKey(IP)) {
            this.routs.put(IP, rout);
            return 0;
        }
        return -1;
    }
}
