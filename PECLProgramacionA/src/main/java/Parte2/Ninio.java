/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte2;

/**
 *
 * @author Usuario
 */
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ninio extends Thread {
    
    public String id;
    private Campamento campamento;
    private int contador = 0;

    public int getContador() {
        return contador;
    }
    
    
    public Ninio(String id, Campamento campamento){
        this.id = id;
        this.campamento = campamento;

    }
    @Override
    public void run(){
        Random r = new Random();
        if(r.nextDouble()<0.5)
        {
            campamento.entrada1(this);
        }
        else{
            campamento.entrada2(this);        
        }
        while (contador<15){
            
            Random actividad = new Random();
            int eleccion = actividad.nextInt(3);
            switch (eleccion) {
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
                Random espera = new Random();
                int numEspera = espera.nextInt(3) + 2;
                TimeUnit.SECONDS.sleep(numEspera);
            } catch (InterruptedException ex) {
                Logger.getLogger(Ninio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        campamento.salir(this);
        
    }

}