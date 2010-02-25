package Hola;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 *
 * @author almunoz
 */
public class HolaBehaviourAgente extends Agent {
    private String nombre;

    public HolaBehaviourAgente() {
        nombre = "Alejandro";
    }

    @Override
    protected void setup() {
        System.out.println("El agente " + getAID().getName() + " se ha iniciado.");
        addBehaviour(new HolaOneShotBehaviour());
    }

    @Override
    protected void takeDown() {
        System.out.println("El agente " + getAID().getName() + " ha terminado.");
    }

    public class HolaOneShotBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            if (nombre != null) {
                System.out.println("Hola " + nombre + "!!! con Behaviours.");
            } else {
                System.out.println("Hola Mundo!!! con Behaviours.");
            }
        }
        
    }

}
