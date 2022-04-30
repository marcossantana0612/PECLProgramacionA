/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.peclprogramaciona;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class Campamento {
    
    private ArrayList<Ninio> entrada1 = new ArrayList<>();
    private ArrayList<Ninio> entrada2 = new ArrayList<>();
    private ArrayList<Ninio> tirolina = new ArrayList<>();
    private ArrayList<Ninio> soga = new ArrayList<>();
    private ArrayList<Ninio> merienda = new ArrayList<>();
    private ArrayList<Ninio> zonaComun = new ArrayList<>();
    private ArrayList<Monitor> zonaComunM = new ArrayList<>();
    private ArrayList<Monitor> tirolinaM = new ArrayList<>();  
    private ArrayList<Monitor> sogaM = new ArrayList<>();
    private ArrayList<Monitor> meriendaM = new ArrayList<>();
    
    private final int capacidad = 50;
    private int alternancia = 0;
    private boolean abierta1 = false;
    private boolean abierta2 = false;
    private boolean enTirolina = false;
    
    Random r = new Random();
    
    Lock lock = new ReentrantLock();
    Condition e1 = lock.newCondition();
    Condition e2 = lock.newCondition();
    Condition empzAct = lock.newCondition();
    Condition prept = lock.newCondition();
    Condition termt = lock.newCondition();
    
    public int asignarMonitor(Monitor monitor){
        lock.lock();
        int zonaAsignar = 0;
        try {
            zonaComunM.remove(monitor);
            if (meriendaM.size() < 2){
                meriendaM.add(monitor);
                zonaAsignar =  0;
            } else if (sogaM.isEmpty()){
                sogaM.add(monitor);
                zonaAsignar = 1;
            } else {
                tirolinaM.add(monitor);
                zonaAsignar = 2;
            }
        } finally {
            lock.unlock();
        }
        return zonaAsignar;
    }
    
    public void entrada1(Monitor monitor){
        lock.lock();
        try{
           if(abierta1 == false){
                
                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta1 = true;
                if (entrada1.size() > 0){
                    e1.signal();
                }       
           }
           zonaComunM.add(monitor);
        }
        catch(InterruptedException e){}
        finally{
            lock.unlock();
        }
    }
    
    public void entrada2(Monitor monitor){
        lock.lock();
        try{
           if(abierta2 == false){
                
                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta2 = true;
                if (entrada2.size() > 0){
                    e2.signal();
                }       
           }
           zonaComunM.add(monitor); 
        }
        catch(InterruptedException e){}
        finally{
            lock.unlock();
        }
    }
    
    public int aforoActual(){
        return zonaComun.size() + tirolina.size() + soga.size() + merienda.size();
    }
    
    
    public void entrada1(Ninio ninio){
        lock.lock();
        try{
            entrada1.add(ninio);
            while(aforoActual() == capacidad || abierta1 == false)
            {
                e1.await();
                
            }
            entrada1.remove(ninio);
            zonaComun.add(ninio);
        }
        catch(InterruptedException e){}
        finally{
            lock.unlock();
        }
    }
    
    public void entrada2(Ninio ninio){
        lock.lock();
        try{
            entrada2.add(ninio);
            while(aforoActual() == capacidad || abierta2 == false)
            {
                e2.await();
            }
            entrada2.remove(ninio);
            zonaComun.add(ninio);
        }
        catch(InterruptedException e){}
        finally{
            lock.unlock();
        }
    }
    public void salir(Ninio ninio){
        lock.lock();
        try {
            zonaComun.remove(ninio);
            if(entrada1.size() > 0 && entrada2.size() > 0){
                //personas esperando en las dos entradas (alternancia)
                if(alternancia == 0)
                {
                    e2.signal();
                    alternancia = 1;
                }
                else{
                    e1.signal();
                    alternancia = 0;
                }
            }
            else{
                if(entrada1.size() > 0)
                    e1.signal();
                else if (entrada2.size() > 0)
                    e2.signal();
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void tirolina(Ninio ninio){
        lock.lock();
        try{
            zonaComun.remove(ninio);
            tirolina.add(ninio);
            while(enTirolina)
            {
                empzAct.await();
            }
            enTirolina = true;
            TimeUnit.SECONDS.sleep(3);
            TimeUnit.MILLISECONDS.sleep(500);
            prept.signal();
            

        }
        catch(InterruptedException e){}
        finally{
            lock.unlock();
        }
    }
    
    public void prepararTirolina(Monitor monitor) {
        lock.lock();
        try {
            while(!enTirolina){
                prept.await();
            }
            enTirolina = false; 
            TimeUnit.SECONDS.sleep(1);
            empzAct.signal();
        } 
        catch (InterruptedException ex) {}
        finally{
            lock.unlock();
        }
    }
    
    public boolean soga(Ninio ninio){
        return true;
    }
    
    public void prepararSoga(Monitor monitor){
        
    }
    
    public void merienda(Ninio ninio){
        
    }
    
    public void prepararMerienda(Monitor monitor){
        
    }
    
    public int descansar (Monitor monitor, int actividad){
        lock.lock();
        int nuevaZona = 0;
        try {
            switch (actividad) {
                    case 0:
                        meriendaM.remove(monitor);
                        break;
                    case 1:
                        sogaM.remove(monitor);
                        break;
                    case 2:
                        tirolinaM.remove(monitor);
                        break;
                }
            zonaComunM.add(monitor);
            TimeUnit.SECONDS.sleep(r.nextInt(2) + 1);
            nuevaZona = asignarMonitor(monitor);
        } catch (InterruptedException e){} 
        finally{
            lock.unlock();
        }
        return nuevaZona;
    }
}
