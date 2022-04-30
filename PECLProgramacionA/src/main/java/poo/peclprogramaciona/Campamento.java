/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.peclprogramaciona;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private final int capacidad = 50;
    
    Lock lock = new ReentrantLock();
    Condition e1 = lock.newCondition();
    Condition e2 = lock.newCondition();
    
    public void entrada1(Ninio ninio){
        lock.lock();
        try{
            entrada1.add(ninio);
            while(zonaComun.size() == capacidad)
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
            while(zonaComun.size() == capacidad)
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
        } finally {
            lock.unlock();
        }
    }
    
    public void tirolina(Ninio ninio){
        
    }
    public boolean soga(Ninio ninio){
        return true;
    }
    public void merienda(Ninio ninio){
        
    }
}
