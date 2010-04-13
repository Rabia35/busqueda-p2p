
package gui;

import busqueda.GUICommunicator;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class ChatGUI extends javax.swing.JFrame {
    // GUI Communicator
    private GUICommunicator guiCommunicator;

    /** Creates new form ChatGUI */
    public ChatGUI(String args[]) {
        initComponents();
        this.guiCommunicator = GUICommunicator.getInstance();
        this.guiCommunicator.setGui(this);
        this.guiCommunicator.iniciar(args);
    }

    public void salir() {
        try {
            guiCommunicator.salir();
        } catch (StaleProxyException ex) {
            System.out.println("StaleProxyException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        } finally {
            System.exit(0);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelMensaje = new javax.swing.JLabel();
        jTextFieldMensaje = new javax.swing.JTextField();
        jButtonEnviar = new javax.swing.JButton();
        jButtonLimpiar = new javax.swing.JButton();
        jScrollPaneMensajes = new javax.swing.JScrollPane();
        jTextAreaMensajes = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArchivo = new javax.swing.JMenu();
        jMenuItemMostrarAdvs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
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

        jButtonLimpiar.setText("Limpiar");
        jButtonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimpiarActionPerformed(evt);
            }
        });

        jTextAreaMensajes.setColumns(20);
        jTextAreaMensajes.setRows(5);
        jTextAreaMensajes.setTabSize(4);
        jScrollPaneMensajes.setViewportView(jTextAreaMensajes);

        jMenuArchivo.setText("Archivo");

        jMenuItemMostrarAdvs.setText("Mostrar Advertisements");
        jMenuItemMostrarAdvs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMostrarAdvsActionPerformed(evt);
            }
        });
        jMenuArchivo.add(jMenuItemMostrarAdvs);
        jMenuArchivo.add(jSeparator1);

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
                    .addComponent(jScrollPaneMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelMensaje)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEnviar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonLimpiar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMensaje)
                    .addComponent(jTextFieldMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEnviar)
                    .addComponent(jButtonLimpiar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        salir();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        salir();
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jButtonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarActionPerformed
        enviarMensaje();
    }//GEN-LAST:event_jButtonEnviarActionPerformed

    private void jMenuItemMostrarAdvsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMostrarAdvsActionPerformed
        mostrarAdvertisements();
    }//GEN-LAST:event_jMenuItemMostrarAdvsActionPerformed

    private void jButtonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimpiarActionPerformed
        jTextAreaMensajes.setText("");
    }//GEN-LAST:event_jButtonLimpiarActionPerformed

    public void deshabilitar() {
        jTextFieldMensaje.setEnabled(false);
        jButtonEnviar.setEnabled(false);
    }

    public void mostrarAdvertisements() {
        jTextAreaMensajes.setText("");
        jTextAreaMensajes.setText(guiCommunicator.getAdvertisementsChat());
    }

    public void enviarMensaje() {
        try {
            String mensaje = jTextFieldMensaje.getText().trim();
            jTextAreaMensajes.append("\nYo digo: " + mensaje);
            guiCommunicator.enviarMensajeChat(mensaje);
        } catch (StaleProxyException ex) {
            System.out.println("Exception: " +  ex.getMessage());
        }
    }

    public void mostrarMensaje(final String remitente, final String mensaje) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTextAreaMensajes.append("\n" + remitente + " dice: " + mensaje);
            }
        });
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        final String params[] = args;
        final ChatGUI gui = new ChatGUI(params);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JButton jButtonLimpiar;
    private javax.swing.JLabel jLabelMensaje;
    private javax.swing.JMenu jMenuArchivo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemMostrarAdvs;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JScrollPane jScrollPaneMensajes;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextArea jTextAreaMensajes;
    private javax.swing.JTextField jTextFieldMensaje;
    // End of variables declaration//GEN-END:variables

}
