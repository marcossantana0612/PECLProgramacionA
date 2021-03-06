/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Usuario
 */
public class Parar {
    private boolean cerrado = false;
    private Lock cerrojo = new ReentrantLock();
    private Condition parar = cerrojo.newCondition();

    public void mirar() {
        /*
        * Comprueba si se ha cerrado y realiza un await en la condicion
        */
        try {
            cerrojo.lock();
            while (cerrado) {
                try {
                    parar.await();
                } catch (InterruptedException ie) {
                }
            }
        } finally {
            cerrojo.unlock();
        }
    }
    
    public void abrir() {
        /*
        * Método para despertar todos los hilos que esten esperando la condicion
        */
        try {
            cerrojo.lock();
            cerrado = false; //Se cambia la condición por la que otros hilos podrían estar esperando
            parar.signalAll();
        } finally {

            cerrojo.unlock();
        }
    }

    public void cerrar() {
        try {
            cerrojo.lock();
            cerrado = true;
        } finally {
            cerrojo.unlock();
        }
    }
}
