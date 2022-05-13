/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte1;

import InterfazP1.InterfazCampamento;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Usuario
 */
public class CreadorDeHilos extends Thread {

    InterfazCampamento c;
    Parar paro;

    //Constructor
    public CreadorDeHilos(InterfazCampamento c, Parar paro) {
        this.c = c;
        this.paro = paro;
    }

    //Metodo run()
    public void run() {
        try {
            Campamento campamento = new Campamento(c);
            
            //Asignacion de IDs para los monitores
            for (int i = 0; i < 4; i++) {
                Monitor monitor = new Monitor("M" + i, campamento, paro);
                monitor.start();
            }

            //Asignacion de IDs para los ninios
            for (int i = 0; i < 20000; i++) {
                Ninio ninio = new Ninio("N" + i, campamento, paro);
                ninio.start();
                Random r = new Random();
                int intervalo = r.nextInt(3) + 1;
                TimeUnit.SECONDS.sleep(intervalo);
            }
        } catch (InterruptedException e) {
        }
    }
}
