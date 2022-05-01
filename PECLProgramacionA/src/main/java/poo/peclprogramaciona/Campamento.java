/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.peclprogramaciona;


import Interfaz.InterfazCampamento;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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
    
    InterfazCampamento interfaz ;

    public Campamento(InterfazCampamento interfaz) {
        this.interfaz = interfaz;
        interfaz.setVisible(true);
    }
    
    private ArrayList<Ninio> entrada1 = new ArrayList<>();
    private ArrayList<Ninio> entrada2 = new ArrayList<>();
    private ArrayList<Ninio> tirolina = new ArrayList<>();
    private ArrayList<Ninio> soga = new ArrayList<>();
    private ArrayList<Ninio> merienda = new ArrayList<>();
    private ArrayList<Ninio> merendando = new ArrayList<>();
    private ArrayList<Ninio> zonaComun = new ArrayList<>();
    private ArrayList<Monitor> zonaComunM = new ArrayList<>();
    private ArrayList<Monitor> tirolinaM = new ArrayList<>();  
    private ArrayList<Monitor> sogaM = new ArrayList<>();
    private ArrayList<Monitor> meriendaM = new ArrayList<>();
    
    
    private final int capacidad = 50;
    private int alternancia = 0;
    private boolean abierta1 = false;
    private boolean abierta2 = false;

    
    Random r = new Random();
    
    Lock lock = new ReentrantLock();
    Lock entradas = new ReentrantLock();
    Lock actiTiro = new ReentrantLock();
    Lock actiMeri = new ReentrantLock(); 
    
    Condition e1 = entradas.newCondition();
    Condition e2 = entradas.newCondition();
    
    final CyclicBarrier barreraTirolina = new CyclicBarrier(2);
    
    Semaphore semaforoTirolina = new Semaphore(0);
    Semaphore entrarTirolina = new Semaphore(0);
    
    Semaphore sucios = new Semaphore(25);
    Semaphore limpios = new Semaphore(0);
    Semaphore entrarMerienda = new Semaphore(20);
    
    
    public String obtenerIDsM (ArrayList<Monitor> array){
        String IDs = "";
        for (int i = 0;i<array.size();i++){
            IDs = IDs + array.get(i).id + ", ";
        }
        return IDs;
    }
     public String obtenerIDsN (ArrayList<Ninio> array){
        String IDs = "";
        for (int i = 0;i<array.size();i++){
            IDs = IDs + array.get(i).id + ", ";
        }
        return IDs;
    }
    
    public int asignarMonitor(Monitor monitor) {
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
            String monitoresMerienda = obtenerIDsM(meriendaM);
            String monitoresSoga = obtenerIDsM(sogaM);
            String monitoresTirolina = obtenerIDsM(tirolinaM);
            String monitoresZonaC = obtenerIDsM(zonaComunM);
            interfaz.actualizarMonitores(monitoresMerienda, monitoresSoga, monitoresTirolina, monitoresZonaC);
            lock.unlock();
        }
        return zonaAsignar;
    }
    
    public void entrada1(Monitor monitor){
        entradas.lock();
        try{
           if(abierta1 == false){
                
                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta1 = true;
                System.out.println("El monitor " + monitor.id + " ha abierto la puerta 1");
                if (entrada1.size() > 0){
                    e1.signal();
                }       
           }
           zonaComunM.add(monitor);
           System.out.println("El monitor " + monitor.id + " ha entrado por la puerta 1");
        }
        catch(InterruptedException e){}
        finally{
            entradas.unlock();
        }
    }
    
    public void entrada2(Monitor monitor){
        entradas.lock();
        try{
           if(abierta2 == false){
                
                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta2 = true;
                System.out.println("El monitor " + monitor.id + " ha abierto la puerta 2");
                if (entrada2.size() > 0){
                    e2.signal();
                }       
           }
           zonaComunM.add(monitor);
           System.out.println("El monitor " + monitor.id + " ha entrado por la puerta 2");
        }
        catch(InterruptedException e){}
        finally{
            entradas.unlock();
        }
    }
    
    public int aforoActual(){
        return zonaComun.size() + tirolina.size() + soga.size() + merienda.size();
    }
    
    
    public void entrada1(Ninio ninio){
        entradas.lock();
        try{
            entrada1.add(ninio);
            String puerta1 = obtenerIDsN(entrada1);
            interfaz.actualizarPuerta1(puerta1);
            while(aforoActual() == capacidad || abierta1 == false)
            {
                e1.await();
                
            }
            entrada1.remove(ninio);
            if (entrada1.size() > 0){
                puerta1 = obtenerIDsN(entrada1);
                interfaz.actualizarPuerta1(puerta1);
            } else {
                interfaz.actualizarPuerta1("");
            }
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            System.out.println("El ninio " + ninio.id + " ha entrado por la puerta 1");
        }
        catch(InterruptedException e){}
        finally{
            entradas.unlock();
        }
    }
    
    public void entrada2(Ninio ninio){
        entradas.lock();
        try{
            entrada2.add(ninio);
            String puerta2 = obtenerIDsN(entrada2);
            interfaz.actualizarPuerta2(puerta2);
            while(aforoActual() == capacidad || abierta2 == false)
            {
                e2.await();
            }
            entrada2.remove(ninio);
            if (entrada2.size() > 0){
                puerta2 = obtenerIDsN(entrada2);
                interfaz.actualizarPuerta2(puerta2);  
            } else {
                interfaz.actualizarPuerta2("");
            }
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            System.out.println("El ninio " + ninio.id + " ha entrado por la puerta 2");
        }
        catch(InterruptedException e){}
        finally{
            entradas.unlock();
        }
    }
    public void salir(Ninio ninio){
        entradas.lock();
        try {
            zonaComun.remove(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
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
            System.out.println("El ninio " + ninio.id + " sale del campamento");
            entradas.unlock();
        }
    }
    
    public void tirolina(Ninio ninio){
        try{
            zonaComun.remove(ninio);
            tirolina.add(ninio);
            
            String tiro = obtenerIDsN(tirolina);
            interfaz.actualizarColaTirolina(tiro);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            
            actiTiro.lock();
            
            System.out.println("El ninio " + ninio.id + " entra a la tirolina");
            
            tirolina.remove(ninio);
            barreraTirolina.await();
            
            if (tirolina.size() > 0){
                tiro = obtenerIDsN(tirolina);
                interfaz.actualizarColaTirolina(tiro);   
            } else {
                interfaz.actualizarColaTirolina(""); 
            }
            interfaz.actualizarPreparacion(ninio.id);
            
            semaforoTirolina.acquire();
            
            interfaz.actualizarPreparacion("");
            interfaz.actualizarTirolina(ninio.id);
            
            System.out.println("El ninio " + ninio.id + " se tira por la tirolina");
            TimeUnit.SECONDS.sleep(3);
            
            interfaz.actualizarTirolina("");
            interfaz.actualizarFinalizacion(ninio.id);
            
            System.out.println("El ninio " + ninio.id + " se baja por la tirolina");
            TimeUnit.MILLISECONDS.sleep(500);
            
            interfaz.actualizarFinalizacion("");
            zonaComun.add(ninio);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);

            entrarTirolina.release();
        }
        catch(InterruptedException | BrokenBarrierException e ){}
        finally {
            actiTiro.unlock();
        }
    }
    
    public void prepararTirolina(Monitor monitor) {
        try {
            barreraTirolina.await();
            
            System.out.println("El monitor " + monitor.id + " prepara la tirolina");
            TimeUnit.SECONDS.sleep(1);
            
            semaforoTirolina.release();
            
            entrarTirolina.acquire();
        } 
        catch (InterruptedException | BrokenBarrierException ex) {}
    }
    
    public boolean soga(Ninio ninio){
        return true;
    }
    
    public void prepararSoga(Monitor monitor){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            Logger.getLogger(Campamento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public void merienda(Ninio ninio){
        try{
            zonaComun.remove(ninio);
            merienda.add(ninio);
            
            String meri = obtenerIDsN(merienda);
            interfaz.actualizarColaMerienda(meri);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            
            entrarMerienda.acquire();
            
            merienda.remove(ninio);
            merendando.add(ninio);
            
            meri = obtenerIDsN(merienda);
            interfaz.actualizarColaMerienda(meri);
            String ids = obtenerIDsN(merendando);
            interfaz.actualizarMerendando(ids);
 
            limpios.acquire();
            
            actiMeri.lock();
            interfaz.actualizarLimpios(limpios.availablePermits());
            actiMeri.unlock();
            
            TimeUnit.SECONDS.sleep(7);
            
            sucios.release();
            
            actiMeri.lock();
            interfaz.actualizarSucios(sucios.availablePermits());
            actiMeri.unlock();
            
            merendando.remove(ninio);
            zonaComun.add(ninio);
            
            ids = obtenerIDsN(merendando);
            interfaz.actualizarMerendando(ids);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            entrarMerienda.release();
            
        } catch (InterruptedException e){}
    }
    
    public void prepararMerienda(Monitor monitor){
        try {
            
            sucios.acquire();
            
            actiMeri.lock();
            interfaz.actualizarSucios(sucios.availablePermits());
            actiMeri.unlock();
            
            TimeUnit.SECONDS.sleep(r.nextInt(3) + 3);
            
            limpios.release();
            
            actiMeri.lock();
            interfaz.actualizarLimpios(limpios.availablePermits());
            actiMeri.unlock();
        } catch (InterruptedException e) {}
    }
    
    public int descansar (Monitor monitor, int actividad){
        lock.lock();
        System.out.println("El monitor " + monitor.id + " empieza su descanso");
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
            String monitoresMerienda = obtenerIDsM(meriendaM);
            String monitoresSoga = obtenerIDsM(sogaM);
            String monitoresTirolina = obtenerIDsM(tirolinaM);
            String monitoresZonaC = obtenerIDsM(zonaComunM);
            interfaz.actualizarMonitores(monitoresMerienda, monitoresSoga, monitoresTirolina, monitoresZonaC);
            TimeUnit.SECONDS.sleep(r.nextInt(2) + 1);
            nuevaZona = asignarMonitor(monitor);
            System.out.println("El monitor " + monitor.id + " termina su descanso");
        } catch (InterruptedException e){}
        finally{
            lock.unlock();
        }
        return nuevaZona;
    }
}
