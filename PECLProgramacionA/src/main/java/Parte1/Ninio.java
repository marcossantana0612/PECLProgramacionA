/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte1;

/**
 *
 * @author Usuario
 */
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ninio extends Thread {
    //declaracion de variables
    private String id;
    private Campamento campamento;
    private Parar paro;
    private int contador = 0;
    
    //constructor
    public Ninio(String id, Campamento campamento, Parar paro){
        this.id = id;
        this.campamento = campamento;
        this.paro = paro;

    }
    //get de la variable 'id'
    public String getIdN() {
        return id;
    }
    
    @Override
    //Metodo run()
    public void run(){
        Random r = new Random();
        paro.mirar();
        if(r.nextDouble()<0.5)                    //Valor aleatorio para decidir por que entrada accede el ninio
        {
            campamento.entrada1(this);            //Acceso por la entrada 1
        }
        else{
            campamento.entrada2(this);            //Acceso por la entrada 2
        }
        while (contador<15){                      //Mientras el ninio lleve menos de 15 actividades: 
            paro.mirar();
            Random actividad = new Random();      //Valor aleatorio para la eleccion de actividad
            int eleccion = actividad.nextInt(3);
            switch (eleccion) {
                /* Switch con todas las actividades posibles
                *  Cada vez que el ninio termine una actividad su contador aumentara
                *  En caso de no llevar 3 actividades, no podra acceder a la actividad 'merienda'
                */
                case 0:
                    campamento.tirolina(this);
                    contador++;
                    break;
                case 1:
                    if (campamento.entrarSoga()){
                        if(campamento.soga(this)){
                            contador+=2;
                            break;
                        }
                        contador++;
                    }
                    break;
                case 2:
                    if (contador >=3){
                        campamento.merienda(this);
                        contador++;
                    }
                    break;
             }
            try {
                Random espera = new Random();              //valor aleatorio para la pausa entre actividades
                int numEspera = espera.nextInt(3) + 2;
                TimeUnit.SECONDS.sleep(numEspera);         //pausa
            } catch (InterruptedException ex) {
                Logger.getLogger(Ninio.class.getName()).log(Level.SEVERE, null, ex);
            }
            paro.mirar();
        }
        campamento.salir(this);                            //salida del campamento tras realizar las 15 actividades
        
    }

}
