
package busqueda.jxta;

import busqueda.jxta.chat.ChatPeer;
import gui.ChatGUI;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class JXTAManager {
    // Chat GUI
    //private ChatGUI gui;
    private String puerto;
    // Chat Peer
    private ChatPeer chat;

    public JXTAManager(ChatGUI gui) {
        //this.gui = gui;
        this.chat = new ChatPeer(gui);
    }

    public void iniciar() {
        iniciar("9701");
    }

    public void iniciar(String port) {
        this.puerto = port;
        chat.iniciarJXTA(this.puerto);
    }

    public void terminar() throws IOException {
        chat.terminarJXTA();
    }

    /* CHAT */

    public void publicarAdvertisementChat(String nombre, String descripcion) {
        chat.publicarAdvertisement(nombre, descripcion);
    }

    public void buscarAdvertisementChat(String nombre) {
        chat.buscarAdvertisement(nombre);
    }

    public void enviarMensajeChat(String mensaje) {
        chat.enviarMensaje(mensaje);
    }

    public void mostrarAdvertisementsChat() {
        chat.mostrarAdvertisements();
    }

}
