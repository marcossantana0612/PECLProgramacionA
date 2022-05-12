/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte1;

import java.util.Random;

/**
 *
 * @author Usuario
 */
public class Monitor extends Thread {
    
    public String id;
    private Campamento campamento;
    private Parar paro;
    private int actividad;
    private int contador  = 0;

    public Monitor(String id, Campamento campamento, Parar paro) {
        this.id = id;
        this.campamento = campamento;
        this.paro = paro;
    }

    
    
    @Override
    public void run(){
        Random r = new Random();
        paro.mirar();
        if(r.nextDouble()<0.5)
        {
            campamento.entrada1(this);
        }
        else{
            campamento.entrada2(this);        
        }
        actividad = campamento.asignarMonitor(this);
        while(true){
            paro.mirar();
            switch (actividad) {
                case 0:
                    campamento.prepararMerienda(this);
                    contador++;
                    break;
                case 1:
                    campamento.prepararSoga(this);
                    contador++;
                    break;
                case 2:
                    campamento.prepararTirolina(this);
                    contador++;
                    break;
            }
            if (contador >= 10){
                contador = 0;
                actividad = campamento.descansar(this, actividad);
                paro.mirar();
            }
        }
    }
}
