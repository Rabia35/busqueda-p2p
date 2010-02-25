package Hola;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import jade.core.Agent;

/**
 *
 * @author almunoz
 */
public class HolaAgente extends Agent {
    private String nombre;

    @Override
    protected void setup() {
        System.out.println("El agente " + getAID().getName() + " se ha iniciado.");
        // Argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            nombre = (String) args[0];
            System.out.println("Hola " + nombre + "!!!");
        } else {
            System.out.println("Hola Mundo!!!");
        }
        // Hace que el agente termine inmediatamente
        //doDelete();
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getAID().getName() + " ha terminado.");
    }

}
