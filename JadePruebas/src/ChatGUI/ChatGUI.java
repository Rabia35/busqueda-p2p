/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChatGUI.java
 *
 * Created on 3/03/2010, 09:13:55 PM
 */

package ChatGUI;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author almunoz
 */
public class ChatGUI extends javax.swing.JFrame {
    private AgentContainer mainContainer;
    private AgentController rma;
    private AgentController chat;
    
    /** Creates new form ChatGUI */
    public ChatGUI() {
        initComponents();
        iniciarJADE();
    }

    private void iniciarJADE() {
        try {
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl();
            mainContainer = runtime.createMainContainer(profile);
            // Crea e inicia el agente RMA: Remote Management Agent
            //rma = mainContainer.createNewAgent("RMA", "jade.tools.rma.rma", null);
            //rma.start();
            // Crea el agente de Chat
            Object[] parametros = {this};
            chat = mainContainer.createNewAgent("chat", "ChatGUI.ChatGUIAgent", parametros);
            chat.start();
        } catch (StaleProxyException ex) {
            System.out.println("Exception: " +  ex.getMessage());
        }
    }

    private void terminarJADE() {
        try {
            mainContainer.kill();
        } catch (StaleProxyException ex) {
            System.out.println("Exception: " +  ex.getMessage());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelMensaje = new javax.swing.JLabel();
        jTextFieldMensaje = new javax.swing.JTextField();
        jButtonEnviar = new javax.swing.JButton();
        jScrollPaneMensajes = new javax.swing.JScrollPane();
        jTextAreaMensajes = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArchivo = new javax.swing.JMenu();
        jMenuItemSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat GUI");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelMensaje.setText("Mensaje:");

        jTextFieldMensaje.setText("Hola");
        jTextFieldMensaje.setPreferredSize(new java.awt.Dimension(200, 20));

        jButtonEnviar.setText("Enviar");
        jButtonEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarActionPerformed(evt);
            }
        });

        jTextAreaMensajes.setColumns(20);
        jTextAreaMensajes.setRows(5);
        jScrollPaneMensajes.setViewportView(jTextAreaMensajes);

        jMenuArchivo.setText("Archivo");

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenuArchivo.add(jMenuItemSalir);

        jMenuBar1.add(jMenuArchivo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelMensaje)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEnviar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMensaje)
                    .addComponent(jTextFieldMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEnviar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        terminarJADE();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        terminarJADE();
        System.exit(0);
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jButtonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarActionPerformed
        enviarMensaje();
    }//GEN-LAST:event_jButtonEnviarActionPerformed

    private void enviarMensaje() {
        try {
            String mensaje = jTextFieldMensaje.getText().trim();
            jTextAreaMensajes.setText(jTextAreaMensajes.getText() + "\n" + chat.getName() + ": " + mensaje);
            chat.putO2AObject(mensaje, AgentController.ASYNC);
        } catch (StaleProxyException ex) {
            System.out.println("Exception: " +  ex.getMessage());
        }
    }

    public void recibirMensaje(final String mensaje) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTextAreaMensajes.setText(jTextAreaMensajes.getText() + "\n" + mensaje);
            }
        });
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JLabel jLabelMensaje;
    private javax.swing.JMenu jMenuArchivo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JScrollPane jScrollPaneMensajes;
    private javax.swing.JTextArea jTextAreaMensajes;
    private javax.swing.JTextField jTextFieldMensaje;
    // End of variables declaration//GEN-END:variables

}