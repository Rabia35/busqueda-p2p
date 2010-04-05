/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package busqueda;

import busqueda.utilidades.Utilidades;
import gui.ChatGUI;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class GUICommunicator {
    // La instacia de la clase
    private static GUICommunicator instancia;
    // Interfaz Grafica
    private ChatGUI gui;
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // JADE Communicator
    private JADECommunicator jadeCommunicator;
    // Puertos
    private String jxtaPort;
    private String jadePort;

    public static GUICommunicator getInstance() {
        if (instancia == null) {
            instancia = new GUICommunicator();
        }
        return instancia;
    }

    private GUICommunicator() {
        this.gui = null;
        this.jxtaCommunicator = JXTACommunicator.getInstance();
        this.jadeCommunicator = JADECommunicator.getInstance();
        this.jxtaCommunicator.setGuiCommunicator(this);
        this.jxtaCommunicator.setJadeCommunicator(jadeCommunicator);
        this.jadeCommunicator.setGuiCommunicator(this);
        this.jadeCommunicator.setJxtaCommunicator(jxtaCommunicator);
        this.jxtaPort = null;
        this.jadePort = null;
    }

    /**
     * @param gui the gui to set
     */
    public void setGui(ChatGUI gui) {
        this.gui = gui;
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
            //jxtaCommunicator = JXTACommunicator.getInstance();
            //jadeCommunicator = JADECommunicator.getInstance();
            //jxtaCommunicator.setJadeCommunicator(jadeCommunicator);
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
        Utilidades.eliminarCache(Utilidades.JADE_MTP);
        Utilidades.eliminarCache(Utilidades.JADE_AP);
        Utilidades.eliminarCache(Utilidades.JXTA_CACHE);
    }

    public String getAdvertisementsChat() {
        return jxtaCommunicator.getAdvertisementsChat();
    }

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        jadeCommunicator.enviarMensajeChat(mensaje);
    }

    public void mostrarMensaje(String remitente, String mensaje) {
        gui.mostrarMensaje(remitente, mensaje);
    }

}
