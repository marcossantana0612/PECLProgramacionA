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
    //Declaración de variables
    private String id;
    private Campamento campamento;
    private Parar paro;
    private int actividad;
    private int contador  = 0;

    //Constructor
    public Monitor(String id, Campamento campamento, Parar paro) {
        this.id = id;
        this.campamento = campamento;
        this.paro = paro;
    }
    //get de la variable 'id'
    public String getIdM() {
        return id;
    }

    @Override
    public void run(){
        Random r = new Random();                        //Valor aleatorio para decidir por que entrada accede el monitor
        paro.mirar();
        if(r.nextDouble()<0.5)                          //Acceso por la entrada 1
        {
            campamento.entrada1(this);
        }
        else{                                           //Acceso por la entrada 2
            campamento.entrada2(this);        
        }
        actividad = campamento.asignarMonitor(this);    //Asignación de monitor a una actividad
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
            if (contador >= 10){                                    //Si el monitor ya lleva 10 actividades:
                contador = 0;                                       //se reinicia su contador de actividades
                actividad = campamento.descansar(this, actividad);  //el monitor accede a la zona de descanso
                paro.mirar();
            }
        }
    }
}
