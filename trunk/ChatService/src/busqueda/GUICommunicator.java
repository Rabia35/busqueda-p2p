/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package busqueda;

import gui.ChatGUI;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class GUICommunicator {
    // Interfaz Grafica
    private ChatGUI gui;
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // JADE Communicator
    private JADECommunicator jadeCommunicator;
    // Puertos
    private String jxtaPort;
    private String jadePort;

    public GUICommunicator(ChatGUI gui) {
        this.gui = gui;
        this.jxtaCommunicator = null;
        this.jadeCommunicator = null;
        this.jxtaPort = null;
        this.jadePort = null;
    }

    public void extraerArgumentos(String args[]) {
        for (int index = 0; index < args.length ; index++) {
            if (args[index].equals("-jadeport")) {
                this.jadePort = args[index + 1];
            }
            if (args[index].equals("-jxtaport")) {
                this.jxtaPort = args[index + 1];
            }
        }
    }

    public void iniciar(String args[]) {
        try {
            extraerArgumentos(args);
            jxtaCommunicator = new JXTACommunicator(this);
            jadeCommunicator = new JADECommunicator(jxtaCommunicator, this);
            jxtaCommunicator.setJadeCommunicator(jadeCommunicator);
            // Iniciar JXTA
            jxtaCommunicator.iniciarJXTA(jxtaPort);
            // Iniciar JADE
            jadeCommunicator.iniciarJADE(jadePort);
        } catch (StaleProxyException ex) {
            System.out.println("StaleProxyException: " + ex.getMessage());
            System.out.println("No se pudo iniciar la plataforma JADE.");
            System.exit(1);
        }
    }

    public void salir() throws StaleProxyException, IOException {
        jadeCommunicator.terminarJADE();
        jxtaCommunicator.terminarJXTA();
    }

    public String getAdvertisementsChat() {
        return jxtaCommunicator.getAdvertisementsChat();
    }

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        jadeCommunicator.enviarMensajeChat(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        gui.mostrarMensaje(mensaje);
    }

}
