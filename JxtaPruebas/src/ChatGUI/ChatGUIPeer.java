
package ChatGUI;

import java.io.IOException;
import net.jxta.exception.PeerGroupException;
import net.jxta.platform.NetworkManager;

/**
 *
 * @author almunoz
 */
public class ChatGUIPeer {
    NetworkManager manager;

    public ChatGUIPeer() {
        this.manager = null;
    }

    public void iniciarJXTA() {
        try {
            manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "PeerChat01");
            System.out.println("Iniciando Red JXTA");
            manager.startNetwork();
            System.out.println("Red JXTA Iniciada");
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        } catch (PeerGroupException pgex) {
            System.out.println("PGException: " + pgex.getMessage());
        }
    }

    public void terminarJXTA() {
        System.out.println("Terminando Red JXTA");
        manager.stopNetwork();
        System.out.println("Red JXTA Terminada");
    }

}
