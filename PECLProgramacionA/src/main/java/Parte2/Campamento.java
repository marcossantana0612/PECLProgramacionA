/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte2;

import InterfazP2.InterfazCampamento;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Usuario
 */
public class Campamento {

   

    InterfazCampamento interfaz;
    Logs logs = new Logs();

    public Campamento(InterfazCampamento interfaz) {
        this.interfaz = interfaz;
        interfaz.setVisible(true);
        logs.resetearDatos();
        
    }
     public int getContadorTiro() {
        return contadorTiro;
    }

    public ArrayList<Ninio> getTirolina() {
        return tirolina;
    }

    public ArrayList<Ninio> getSoga() {
        return soga;
    }

    public ArrayList<Ninio> getMerendando() {
        return merendando;
    }

    public Semaphore getSucios() {
        return sucios;
    }

    public Semaphore getLimpios() {
        return limpios;
    }
    
    public int getNinio(String id){
        int actividades = estaEnArray(zonaComun, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(tirolina, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(soga, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(equipo1, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(equipo2, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(merienda, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(merendando, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(entrada1, id);
        if (actividades != -1){
            return actividades;
        }
        actividades = estaEnArray(entrada2, id);
        if (actividades != -1){
            return actividades;
        }
        return actividades;
    }
    public int estaEnArray(ArrayList<Ninio> arr, String id){
        int contador = -1;
        for (int i = 0;i<zonaComun.size();i++){
            if (id.equals(zonaComun.get(i).id)){
                contador = arr.get(i).getContador();
            }
        }
        return contador;
    }

    private final ArrayList<Ninio> entrada1 = new ArrayList<>();
    private final ArrayList<Ninio> entrada2 = new ArrayList<>();

    private final ArrayList<Ninio> tirolina = new ArrayList<>();
    private final ArrayList<Ninio> soga = new ArrayList<>();
    private final ArrayList<Ninio> equipo1 = new ArrayList<>();
    private final ArrayList<Ninio> equipo2 = new ArrayList<>();
    private final ArrayList<Ninio> merienda = new ArrayList<>();
    private final ArrayList<Ninio> merendando = new ArrayList<>();
    private final ArrayList<Ninio> zonaComun = new ArrayList<>();

    private final ArrayList<Monitor> zonaComunM = new ArrayList<>();
    private final ArrayList<Monitor> tirolinaM = new ArrayList<>();
    private final ArrayList<Monitor> sogaM = new ArrayList<>();
    private final ArrayList<Monitor> meriendaM = new ArrayList<>();

    private final int capacidad = 50;
    private int alternancia = 0;
    private int contadorTiro = 0;
    private boolean abierta1 = false;
    private boolean abierta2 = false;
    private boolean sogaLibre = true;
    private boolean ganador;

    Random r = new Random();

    Lock lock = new ReentrantLock();
    Lock entradas = new ReentrantLock();
    Lock entradasM = new ReentrantLock();
    Lock actiMeri = new ReentrantLock();

    Condition e1 = entradas.newCondition();
    Condition e2 = entradas.newCondition();

    final CyclicBarrier barreraTirolina = new CyclicBarrier(2);
    final CyclicBarrier barreraSoga = new CyclicBarrier(10);
    final CyclicBarrier barreraFinSoga = new CyclicBarrier(11);

    Semaphore semP1 = new Semaphore(0);
    Semaphore semP2 = new Semaphore(0);
    Semaphore semaforoTirolina = new Semaphore(0);
    Semaphore entrarTirolina = new Semaphore(0);
    Semaphore semaforoSoga = new Semaphore(0);
    Semaphore esperarGrupo = new Semaphore(0);
    Semaphore actiTiro = new Semaphore(1);

    Semaphore sucios = new Semaphore(25);
    Semaphore limpios = new Semaphore(0);
    Semaphore entrarMerienda = new Semaphore(20);

    public String obtenerIDsM(ArrayList<Monitor> array) {
        lock.lock();
        String IDs = "";
        if (!array.isEmpty()) {
            for (int i = 0; i < array.size(); i++) {
                IDs = IDs + array.get(i).id + ", ";
            }
        }
        lock.unlock();
        return IDs;
    }

    public String obtenerIDsN(ArrayList<Ninio> array) {
        lock.lock();
        String IDs = "";
        if (!array.isEmpty()) {
            for (int i = 0; i < array.size(); i++) {
                IDs = IDs + array.get(i).id + ", ";
            }
        }
        lock.unlock();
        return IDs;
    }

    public int asignarMonitor(Monitor monitor) {
        entradasM.lock();
        int zonaAsignar = 0;
        try {
            zonaComunM.remove(monitor);
            if (meriendaM.size() < 2) {
                meriendaM.add(monitor);
                zonaAsignar = 0;
            } else if (sogaM.isEmpty()) {
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
            entradasM.unlock();
        }
        return zonaAsignar;
    }

    public void entrada1(Monitor monitor) {
        entradasM.lock();
        try {
            if (abierta1 == false) {

                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta1 = true;
                logs.guardarDatoM(monitor, 3);
                if (entrada1.size() > 0) {
                    semP1.release();
                }
            }
            zonaComunM.add(monitor);
            logs.guardarDatoM(monitor, 1);
        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
    }

    public void entrada2(Monitor monitor) {
        entradasM.lock();
        try {
            if (abierta2 == false) {

                int tiempoAbrir = r.nextInt(2) + 1;
                TimeUnit.SECONDS.sleep(tiempoAbrir);
                abierta2 = true;
                logs.guardarDatoM(monitor, 4);
                if (entrada2.size() > 0) {
                    semP2.release();
                }
            }
            zonaComunM.add(monitor);
            logs.guardarDatoM(monitor, 2);
        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
    }

    public int aforoActual() {
        return zonaComun.size() + tirolina.size() + soga.size() + merienda.size();
    }

    public void entrada1(Ninio ninio) {
        
        try {
            entrada1.add(ninio);
            String puerta1 = obtenerIDsN(entrada1);
            interfaz.actualizarPuerta1(puerta1);
            entradas.lock();
            while (aforoActual() >= capacidad) {
                e1.await();
            }
            if (abierta1 == false){
                semP1.acquire();
            }
            entrada1.remove(ninio);
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            if (entrada1.size() > 0) {
                puerta1 = obtenerIDsN(entrada1);
                interfaz.actualizarPuerta1(puerta1);
            } else {
                interfaz.actualizarPuerta1("");
            }
            logs.guardarDatoN(ninio, 1);
        } catch (InterruptedException e) {
        } finally {
            entradas.unlock();
        }
    }

    public void entrada2(Ninio ninio) {
        
        try {
            entrada2.add(ninio);
            String puerta2 = obtenerIDsN(entrada2);
            interfaz.actualizarPuerta2(puerta2);
            entradas.lock();
            while (aforoActual() >= capacidad) {
                e2.await();
            }
            if (abierta2 == false){
                semP2.acquire();
            }
            entrada2.remove(ninio);
            if (entrada2.size() > 0) {
                puerta2 = obtenerIDsN(entrada2);
                interfaz.actualizarPuerta2(puerta2);
            } else {
                interfaz.actualizarPuerta2("");
            }
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
           logs.guardarDatoN(ninio, 2);
        } catch (InterruptedException e) {
        } finally {
            entradas.unlock();
        }
    }

    public void salir(Ninio ninio) {
        entradas.lock();
        try {
            zonaComun.remove(ninio);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            if (entrada1.size() > 0 && entrada2.size() > 0) {
                //personas esperando en las dos entradas (alternancia)
                if (alternancia == 0) {
                    e2.signal();
                    alternancia = 1;
                } else {
                    e1.signal();
                    alternancia = 0;
                }
            } else {
                if (entrada1.size() > 0) {
                    e1.signal();
                } else if (entrada2.size() > 0) {
                    e2.signal();
                }
            }
        } finally {
            logs.guardarDatoN(ninio, 11);
            entradas.unlock();
        }
    }

    public void tirolina(Ninio ninio) {
        try {
            zonaComun.remove(ninio);
            tirolina.add(ninio);

            String tiro = obtenerIDsN(tirolina);
            interfaz.actualizarColaTirolina(tiro);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);

            actiTiro.acquire();


            tirolina.remove(ninio);
            barreraTirolina.await();

            if (tirolina.size() > 0) {
                tiro = obtenerIDsN(tirolina);
                interfaz.actualizarColaTirolina(tiro);
            } else {
                interfaz.actualizarColaTirolina("");
            }
            interfaz.actualizarPreparacion(ninio.id);

            semaforoTirolina.acquire();

            interfaz.actualizarPreparacion("");
            interfaz.actualizarTirolina(ninio.id);

            logs.guardarDatoN(ninio, 6);
            TimeUnit.SECONDS.sleep(3);

            interfaz.actualizarTirolina("");
            interfaz.actualizarFinalizacion(ninio.id);

            TimeUnit.MILLISECONDS.sleep(500);

            interfaz.actualizarFinalizacion("");
            zonaComun.add(ninio);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);
            
            contadorTiro++;
            
            logs.guardarDatoN(ninio, 0);

            entrarTirolina.release();
            actiTiro.release();
        } catch (InterruptedException | BrokenBarrierException e) {}
    }

    public void prepararTirolina(Monitor monitor) {
        try {
            barreraTirolina.await();

            TimeUnit.SECONDS.sleep(1);

            semaforoTirolina.release();

            entrarTirolina.acquire();
        } catch (InterruptedException | BrokenBarrierException ex) {
        }
    }

    public boolean entrarSoga() {
        return sogaLibre;
    }

    public boolean soga(Ninio ninio) {
        boolean heGanado = false;
        try {

            zonaComun.remove(ninio);
            soga.add(ninio);

            String ids = obtenerIDsN(soga);
            interfaz.actualizarColaSoga(ids);
            String zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);


            barreraSoga.await();
            
            esperarGrupo.release();

            semaforoSoga.acquire();
            
            logs.guardarDatoN(ninio, 7);

            TimeUnit.SECONDS.sleep(7);

            barreraFinSoga.await();

            if (ganador && equipo1.contains(ninio)) {
                heGanado = true;
                equipo1.remove(ninio);
                logs.guardarDatoN(ninio, 8);
            } else if (!ganador && equipo1.contains(ninio)) {
                heGanado = false;
                equipo1.remove(ninio);
                logs.guardarDatoN(ninio, 9);
            } else if (ganador && equipo2.contains(ninio)) {
                heGanado = false;
                equipo2.remove(ninio);
                logs.guardarDatoN(ninio, 9);
            } else {
                heGanado = true;
                equipo2.remove(ninio);
                logs.guardarDatoN(ninio, 8);
            }

            soga.remove(ninio);
            zonaComun.add(ninio);
            
            logs.guardarDatoN(ninio, 0);

            interfaz.actualizarEquipos("", "");
            ids = obtenerIDsN(soga);
            interfaz.actualizarColaSoga(ids);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);

        } catch (InterruptedException | BrokenBarrierException e) {
        }

        return heGanado;
    }

    public void prepararSoga(Monitor monitor) {
        try {
            esperarGrupo.acquire(10);
            sogaLibre = false;
            List<Ninio> listatemp1;
            List<Ninio> listatemp2;

            listatemp1 = soga.subList(0, 5);
            listatemp2 = soga.subList(5, 10);
            equipo1.addAll(listatemp1);
            equipo2.addAll(listatemp2);
            soga.clear();

            String eq1 = obtenerIDsN(equipo1);
            String eq2 = obtenerIDsN(equipo2);
            interfaz.actualizarEquipos(eq1, eq2);
            interfaz.actualizarColaSoga("");

            semaforoSoga.release(10);

            ganador = r.nextDouble() < 0.5;

            barreraFinSoga.await();
            sogaLibre = true;

        } catch (InterruptedException | BrokenBarrierException ex) {
        }
    }

    public void merienda(Ninio ninio) {
        try {
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

            interfaz.actualizarLimpios(limpios.availablePermits());
            
            logs.guardarDatoN(ninio, 10);

            TimeUnit.SECONDS.sleep(7);

            sucios.release();

            interfaz.actualizarSucios(sucios.availablePermits());

            merendando.remove(ninio);
            zonaComun.add(ninio);

            logs.guardarDatoN(ninio, 0);
            
            ids = obtenerIDsN(merendando);
            interfaz.actualizarMerendando(ids);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);

            entrarMerienda.release();

        } catch (InterruptedException e) {
        }
    }

    public void prepararMerienda(Monitor monitor) {
        try {

            sucios.acquire();


            actiMeri.lock();
            interfaz.actualizarSucios(sucios.availablePermits());
            actiMeri.unlock();

            TimeUnit.SECONDS.sleep(r.nextInt(3) + 3);
            
            logs.guardarDatoM(monitor, 5);

            limpios.release();

            actiMeri.lock();
            interfaz.actualizarLimpios(limpios.availablePermits());
            actiMeri.unlock();
        } catch (InterruptedException e) {
        }
    }

    public int descansar(Monitor monitor, int actividad) {
        entradasM.lock();
        logs.guardarDatoM(monitor, 0);
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

        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
        return nuevaZona;
    }
}
