/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Usuario
 */

/*Para saber que actividad realiza, le pasamos un entero, que corresponde a :
    0 -> Sale a descansar
    1 -> Entra entrada 1
    2 -> Entra entrada 2
    3 -> Abre entrada 1
    4 -> Abre entrada 2
    5 -> Limpia bandeja
    6 -> Entra a tirolina
    7 -> Entra soga
    8 -> Gana soga
    9 -> Pierde soga
    10-> Entra a merienda
    11-> Sale del campamento
*/
public class Logs {
    private String archivo = "evolucionCampamentoP1.txt";    //Archivo en el que se guardaran los datos
    private Lock lock = new ReentrantLock();
    
    public void resetearDatos(){
        /*
        * Resetea el contenido del .txt para que no surgan problemas si habia datos guardados anteriormente
        */
        try {
            lock.lock();
            PrintWriter out = new PrintWriter(archivo);
            out.print("");
            out.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally{
            lock.unlock();
        }  
    }
    public void guardarDatoN(Ninio n, int acti){
        /*
        * Guarda los datos de los ninios
        */
        try {
            lock.lock();
            try ( PrintWriter out = new PrintWriter(new FileWriter(archivo, true))) {

                String aEscribir = "";
                switch(acti){
                    case 0:
                        aEscribir = "El ninio" + n.getIdN() + "sale a descansar. " + LocalDateTime.now();
                        break;
                    case 1:
                        aEscribir = "El ninio" + n.getIdN() + "entra por la entrada 1. " + LocalDateTime.now();
                        break;
                    case 2:
                        aEscribir = "El ninio" + n.getIdN() + "entra por la entrada 2. " + LocalDateTime.now();
                        break;
                    case 6:
                        aEscribir = "El ninio" + n.getIdN() + "entra en la tirolina. " + LocalDateTime.now();
                        break;
                    case 7:
                        aEscribir = "El ninio" + n.getIdN() + "entra en la soga. " + LocalDateTime.now();
                        break;
                    case 8:
                        aEscribir = "El ninio" + n.getIdN() + "ha ganado la actividad de soga. " + LocalDateTime.now();
                        break;
                    case 9:
                        aEscribir = "El ninio" + n.getIdN() + "ha perdido la actividad de soga. " + LocalDateTime.now();
                        break;
                    case 10:
                        aEscribir = "El ninio" + n.getIdN() + "entra a merendar. " + LocalDateTime.now();
                        break;
                    case 11:
                        aEscribir = "El ninio" + n.getIdN() + "sale del campamento." + LocalDateTime.now();
                        break;
                    default:
                        break;
                }
                out.println(aEscribir);
                out.close();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally{
            lock.unlock();
        }
    }
    public void guardarDatoM(Monitor m, int acti){
        /*
        * Guarda los datos de los monitores
        */
        try {
            lock.lock();
            try ( PrintWriter out = new PrintWriter(new FileWriter(archivo, true))) {
                String aEscribir = "";
                switch(acti){
                    case 0:
                        aEscribir = "El monitor" + m.getIdM()  + "sale a descansar. " + LocalDateTime.now();
                        break;
                    case 1:
                        aEscribir = "El monitor" + m.getIdM() + "entra por la entrada 1. " + LocalDateTime.now();
                        break;
                    case 2:
                        aEscribir = "El monitor" + m.getIdM() + "entra por la entrada 2. " + LocalDateTime.now();
                        break;
                    case 3:
                        aEscribir = "El monitor" + m.getIdM() + "abre la entrada 1. " + LocalDateTime.now();
                        break;
                    case 4:
                        aEscribir = "El monitor" + m.getIdM() + "abre la entrada 2. " + LocalDateTime.now();
                        break;
                    case 5:
                        aEscribir = "El monitor" + m.getIdM() + "ha limpiado un plato. " + LocalDateTime.now();
                        break;
                    
                    default:
                        break;
                }
                out.println(aEscribir);
                out.close();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally{
            lock.unlock();
        }
    }
}
