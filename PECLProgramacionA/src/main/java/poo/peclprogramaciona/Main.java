/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.peclprogramaciona;

import Interfaz.InterfazCampamento;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Usuario
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        InterfazCampamento interfaz = new InterfazCampamento();
        Campamento campamento = new Campamento(interfaz);
        for (int i = 0;i<4;i++){
            Monitor monitor = new Monitor("M"+i,campamento);
            monitor.start();
            
        }
        
        for (int i=0;i<20000;i++){
            Ninio ninio = new Ninio("N"+i, campamento);
            ninio.start();
            Random r = new Random();
            int intervalo = r.nextInt(3) + 1;
            TimeUnit.SECONDS.sleep(intervalo);
        }

    }
    
}
