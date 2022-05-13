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
   
    //Creacion de ArrayList para las entradas y las actividades
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

    //Declaracion de variables
    private final int capacidad = 50;
    private int alternancia = 0;
    private int contadorTiro = 0;
    private boolean abierta1 = false;
    private boolean abierta2 = false;
    private boolean sogaLibre = true;
    private boolean ganador;

    Random r = new Random();

    //Declaracion de Locks 
    Lock lock = new ReentrantLock();
    Lock entradas = new ReentrantLock();
    Lock entradasM = new ReentrantLock();
    Lock actiMeri = new ReentrantLock();

    Condition e1 = entradas.newCondition();
    Condition e2 = entradas.newCondition();

    //Declaracion de CiclicBarriers
    final CyclicBarrier barreraTirolina = new CyclicBarrier(2);
    final CyclicBarrier barreraSoga = new CyclicBarrier(10);
    final CyclicBarrier barreraFinSoga = new CyclicBarrier(11);

    //Declaracion de Semaforos
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

    //Getters
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
        /* 
        *  Se busca al ninio en los ArrayList de actividades y entradas
        *  Devuelve el numero de actividades que ha realizado usando la funcion 'estaEnArray()'
        */
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
        /*
        * Busca a un ninio en un ArrayList y devuelve el valor de su contador de actividades
        */
        int contador = -1;
        for (int i = 0;i<zonaComun.size();i++){
            if (id.equals(zonaComun.get(i).id)){
                contador = arr.get(i).getContador();
            }
        }
        return contador;
    }

    public String obtenerIDsM(ArrayList<Monitor> array) {
        /*
        * Devuelve los IDs de los monitores de un ArrayList de monitores en forma de String
        */
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
        /*
        * Devuelve los IDs de los ninios de un ArrayList de ninios en forma de String
        */
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
        /*
        * Se asigna un monitor de la zona comun a una actividad siempre que dicha actividad lo necesite
        */
        entradasM.lock();
        int zonaAsignar = 0;
        try {
            zonaComunM.remove(monitor);   //se saca al monitor de la zona comun para poder asignarlo a una actividad
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
        } finally {   //se obtienen los IDs de los menitores asignados a cada zona y se actualiza la interfaz
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
        /*
        * Acceso de los monitores al campanento por la entrada1
        */
        entradasM.lock();
        try {
            if (abierta1 == false) {                   //Si la entrada esta cerrada:

                int tiempoAbrir = r.nextInt(2) + 1;    //Valor aleatorio para la apertura de la entrada
                TimeUnit.SECONDS.sleep(tiempoAbrir);   //Abriendo la entrada
                abierta1 = true;                       //Entrada abierta
                logs.guardarDatoM(monitor, 3);         //Se registra la apertura en el log
                if (entrada1.size() > 0) {
                    semP1.release();                   //Una vaz la puerta este abierta, se permite el acceso
                }
            }
            zonaComunM.add(monitor);                   //Se añade el monitor a la zona comun
            logs.guardarDatoM(monitor, 1);             //Se registra en el log
        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
    }

    public void entrada2(Monitor monitor) {
        /*
        * Acceso de los monitores al campanento por la entrada2
        */
        entradasM.lock();
        try {
            if (abierta2 == false) {                   //Si la entrada esta cerrada:

                int tiempoAbrir = r.nextInt(2) + 1;    //Valor aleatorio para la apertura de la entrada
                TimeUnit.SECONDS.sleep(tiempoAbrir);   //Abriendo la entrada
                abierta2 = true;                       //Entrada abierta
                logs.guardarDatoM(monitor, 4);         //Se registra la apertura en el log
                if (entrada2.size() > 0) {
                    semP2.release();                   //Una vaz la puerta este abierta, se permite el acceso
                }
            }
            zonaComunM.add(monitor);                   //Se añade el monitor a la zona comun
            logs.guardarDatoM(monitor, 2);             //Se registra en el log
        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
    }

    public int aforoActual() {
       /*
       * Suma de la cantidad de ninios en cada zona para obtener el aforo actual
       */
        return zonaComun.size() + tirolina.size() + soga.size() + merienda.size();
    }

    public void entrada1(Ninio ninio) {
        /*
        * Acceso de los ninios al campanento por la entrada1
        */
        try {
            entrada1.add(ninio);                        //Se añade el ninio a la entrada
            String puerta1 = obtenerIDsN(entrada1);     //Obtencion de los IDs de los ninios en la entrada1
            interfaz.actualizarPuerta1(puerta1);        //Actialización de la interfaz
            entradas.lock();
            while (aforoActual() >= capacidad) {        //Comprobacion de aforo para restringir el acceso
                e1.await();
            }
            if (abierta1 == false){                     //Si la puerta está cerrada se restringe el acceso
                semP1.acquire();
            }
            entrada1.remove(ninio);                     //Se quita al ninio de la entrada y se añade a la zona comun
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);      //Obtencion de los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);
            if (entrada1.size() > 0) {
                puerta1 = obtenerIDsN(entrada1);
                interfaz.actualizarPuerta1(puerta1);    //Actialización de la interfaz
            } else {
                interfaz.actualizarPuerta1("");         //Actialización de la interfaz
            }
            logs.guardarDatoN(ninio, 1);                //Se registra en el log
        } catch (InterruptedException e) {
        } finally {
            entradas.unlock();
        }
    }

    public void entrada2(Ninio ninio) {
        /*
        * Acceso de los ninios al campanento por la entrada2
        */
        try {
            entrada2.add(ninio);                        //Se añade el ninio a la entrada
            String puerta2 = obtenerIDsN(entrada2);     //Obtencion de los IDs de los ninios en la entrada2
            interfaz.actualizarPuerta2(puerta2);        //Actialización de la interfaz
            entradas.lock();
            while (aforoActual() >= capacidad) {        //Comprobacion de aforo para restringir el acceso
                e2.await();
            }
            if (abierta2 == false){                     //Si la puerta está cerrada se restringe el acceso
                semP2.acquire();
            }
            entrada2.remove(ninio);                     //Se quita al ninio de la entrada y se añade a la zona comun
            if (entrada2.size() > 0) {
                puerta2 = obtenerIDsN(entrada2);
                interfaz.actualizarPuerta2(puerta2);    //Actialización de la interfaz
            } else {
                interfaz.actualizarPuerta2("");         //Actialización de la interfaz
            }
            zonaComun.add(ninio);
            String zonaC = obtenerIDsN(zonaComun);      //Obtencion de los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);        //Actialización de la interfaz
           logs.guardarDatoN(ninio, 2);                 //Se registra en el log
        } catch (InterruptedException e) {
        } finally {
            entradas.unlock();
        }
    }

    public void salir(Ninio ninio) {
        entradas.lock();
        try {
            zonaComun.remove(ninio);                    //Se quita al ninio de la zona comun
            String zonaC = obtenerIDsN(zonaComun);      //Obtencion de los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);        //Actialización de la interfaz
            if (entrada1.size() > 0 && entrada2.size() > 0) {
                //Ninios esperando en las dos entradas (alternancia)
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
            logs.guardarDatoN(ninio, 11);                //Se registra en el log
            entradas.unlock();
        }
    }

    public void tirolina(Ninio ninio) {
       /*
       * Funcion para la actividad tirolina
       */
        try {
            zonaComun.remove(ninio);                   //saca al ninio de la zona comun
            tirolina.add(ninio);                       //añade al ninio a la cola de la actividad tirolina

            String tiro = obtenerIDsN(tirolina);       //obtiene los IDs de los ninios en la cola de la actividad tirolina
            interfaz.actualizarColaTirolina(tiro);     //actualizacion de la interfaz
            String zonaC = obtenerIDsN(zonaComun);     //obtiene los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);       //actualizacion de la interfaz

            actiTiro.acquire();

            tirolina.remove(ninio);                    //saca al ninio a la cola de la actividad tirolina
            barreraTirolina.await();

            if (tirolina.size() > 0) {
                tiro = obtenerIDsN(tirolina);          //obtiene los IDs de los ninios en la cola de la actividad tirolina
                interfaz.actualizarColaTirolina(tiro); //actualizacion de la interfaz
            } else {
                interfaz.actualizarColaTirolina("");   //actualizacion de la interfaz, el ninio sale de la cola 
            }
            interfaz.actualizarPreparacion(ninio.id);  //actualizacion de la interfaz, poniendo al ninio en la celda de preparacion

            semaforoTirolina.acquire();

            interfaz.actualizarPreparacion("");        //actualizacion de la interfaz, el ninio ya se ha preparado
            interfaz.actualizarTirolina(ninio.id);

            logs.guardarDatoN(ninio, 6);               //Se registra en el log
            TimeUnit.SECONDS.sleep(3);                 //espera a que el ninio se tire por la tirolina

            interfaz.actualizarTirolina("");           //actualizacion de la interfaz, el ninio ya se ha tirado
            interfaz.actualizarFinalizacion(ninio.id); //el ninio se esta bajando

            TimeUnit.MILLISECONDS.sleep(500);          //espera a que el ninio baje de la tirolina

            interfaz.actualizarFinalizacion("");       //actualizacion de la interfaz, el ninio se ha bajado y termina la actividad
            zonaComun.add(ninio);                      //se añade el ninio a la zona comun
            zonaC = obtenerIDsN(zonaComun);            //obtiene los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);       //actualizacion de la interfaz
            
            contadorTiro++;
            
            logs.guardarDatoN(ninio, 0);               //Se registra en el log

            entrarTirolina.release();
            actiTiro.release();
        } catch (InterruptedException | BrokenBarrierException e) {}
    }

    public void prepararTirolina(Monitor monitor) {
       /*
       * Funcion para preparacion la actividad tirolina
       */
        try {
            barreraTirolina.await();

            TimeUnit.SECONDS.sleep(1);

            semaforoTirolina.release();

            entrarTirolina.acquire();
        } catch (InterruptedException | BrokenBarrierException ex) {
        }
    }

    public boolean entrarSoga() {
       /*
       * Funcion para entrar a la actividad soga
       */
        return sogaLibre;
    }

    public boolean soga(Ninio ninio) {
       /*
       * Funcion para la actividad soga
       */
        boolean heGanado = false;                  //variable para declarar al ganador de la actividad
        try {

            zonaComun.remove(ninio);               //se saca al ninio de la zona comun
            soga.add(ninio);                       //se añade al ninio a la cola de la actividad soga

            String ids = obtenerIDsN(soga);        //obtencion los IDs de los ninios en la cola de la actividad soga
            interfaz.actualizarColaSoga(ids);      //actualizacion de la interfaz
            String zonaC = obtenerIDsN(zonaComun); //obtencion los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);   //actualizacion de la interfaz

            //Se espera a que llegue un grupo de 10 ninios para comenzar la actividad
            barreraSoga.await();
            
            esperarGrupo.release();

            semaforoSoga.acquire();
            
            logs.guardarDatoN(ninio, 7);

            TimeUnit.SECONDS.sleep(7);

            barreraFinSoga.await();

            //Busca si ninio han ganado o perdido dependiendo de a que equipo pertenece y se registra en el log
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

            soga.remove(ninio);                   //Se saca al ninio de la actividad soga
            zonaComun.add(ninio);                 //Se añade al ninio a la zona comun
            
            logs.guardarDatoN(ninio, 0);          //Se registra en el log

            interfaz.actualizarEquipos("", "");   //Actualizacion de la interfaz
            ids = obtenerIDsN(soga);              //Obtencion los IDs de los ninios en la actividad soga
            interfaz.actualizarColaSoga(ids);     //Actualizacion de la interfaz
            zonaC = obtenerIDsN(zonaComun);       //Obtencion los IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);  //Actualizacion de la interfaz

        } catch (InterruptedException | BrokenBarrierException e) {}

        return heGanado;                          //devuelve el resultado de la actividad
    }

    public void prepararSoga(Monitor monitor) {
        /*
        * Funcion para preparar la actividad soga
        */
        try {
            esperarGrupo.acquire(10);             //Una vez tiene 10 ninios:
            sogaLibre = false;                    //Ahora la soga esta ocupada
            List<Ninio> listatemp1;               //Declaracion de las listas temporales de equipos
            List<Ninio> listatemp2;

            listatemp1 = soga.subList(0, 5);      //Los 5 primros ninios forman el equipo 1; se añaden a la listatemp1
            listatemp2 = soga.subList(5, 10);     //Los 5 ultimos ninios forman el equipo 2; se añaden a la listatemp2
            equipo1.addAll(listatemp1);           //Se añaden de la listatemp1 al equipo 1
            equipo2.addAll(listatemp2);           //Se añaden de la listatemp2 al equipo 2
            soga.clear();

            String eq1 = obtenerIDsN(equipo1);    //Obtencion de IDs de los ninios en cada equipo
            String eq2 = obtenerIDsN(equipo2);
            interfaz.actualizarEquipos(eq1, eq2); //Actualizacion de interfaz
            interfaz.actualizarColaSoga("");

            semaforoSoga.release(10);

            ganador = r.nextDouble() < 0.5;       //Equipo ganador aleatorio

            barreraFinSoga.await();
            sogaLibre = true;                     //Ahora la soga esta libre

        } catch (InterruptedException | BrokenBarrierException ex) {}
    }

    public void merienda(Ninio ninio) {
        /*
        * Funcion para la actividad merienda
        */
        try {
            zonaComun.remove(ninio);                //se saca al ninio de la zona comun
            merienda.add(ninio);                    //se añade al ninio a la cola de la actividad merienda

            String meri = obtenerIDsN(merienda);    //Obtencion de IDs de los ninios en la actividad merienda
            interfaz.actualizarColaMerienda(meri);  //Actualizacion de interfaz
            String zonaC = obtenerIDsN(zonaComun);  //Obtencion de IDs de los ninios en la zona comun
            interfaz.actualizarZonaComun(zonaC);    //Actualizacion de interfaz

            entrarMerienda.acquire();

            merienda.remove(ninio);                 //se saca al ninio de la cola de la actividad merienda
            merendando.add(ninio);                  //se añade al ninio a merendando

            meri = obtenerIDsN(merienda);           //Obtencion de IDs de los ninios en la zona comun
            interfaz.actualizarColaMerienda(meri);  //Actualizacion de interfaz
            String ids = obtenerIDsN(merendando);   //Obtencion de IDs de los ninios merendando
            interfaz.actualizarMerendando(ids);     //Actualizacion de interfaz

            limpios.acquire();                      //Se utiliza un plato limpio

            interfaz.actualizarLimpios(limpios.availablePermits()); //Actualizacion de interfaz
            
            logs.guardarDatoN(ninio, 10);           //Se registra en el log

            TimeUnit.SECONDS.sleep(7);              //El ninio come durante 7 segundos

            sucios.release();                       //Se añade el plato a sucios

            interfaz.actualizarSucios(sucios.availablePermits()); //Actualizacion de interfaz

            merendando.remove(ninio);               //se saca al ninio de merendando
            zonaComun.add(ninio);                   //se añade al ninio a la zona comun

            logs.guardarDatoN(ninio, 0);            //Se registra en el log
            
            ids = obtenerIDsN(merendando);          //Obtencion de IDs y actualizacion de interfaz
            interfaz.actualizarMerendando(ids);
            zonaC = obtenerIDsN(zonaComun);
            interfaz.actualizarZonaComun(zonaC);

            entrarMerienda.release();

        } catch (InterruptedException e) {}
    }

    public void prepararMerienda(Monitor monitor) {
        /*
        * Funcion para la preparacion de la merienda
        */
        try {
           //Se coge un plato sucio y se actualiza la interfaz
            sucios.acquire();

            actiMeri.lock();
            interfaz.actualizarSucios(sucios.availablePermits());
            actiMeri.unlock();
            //Se limpia el plato
            TimeUnit.SECONDS.sleep(r.nextInt(3) + 3); //Tiempo aleatorio (3-5s) para limpiar el plato
            
            logs.guardarDatoM(monitor, 5); //Se registra en el log
            //Se añade el plato a la pila de limpios
            limpios.release();

            actiMeri.lock();
            interfaz.actualizarLimpios(limpios.availablePermits());
            actiMeri.unlock();
        } catch (InterruptedException e) {}
    }

    public int descansar(Monitor monitor, int actividad) {
        /*
        * Funcion para el descanso de los monitores entre actividades
        */
        entradasM.lock();
        logs.guardarDatoM(monitor, 0);            //Se registra en el log
        int nuevaZona = 0;
        try {                                     //Se saca al monitor de la actividad en la que se encuentra
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
            zonaComunM.add(monitor);              //Se añade al monitor a la zona comun
            //Obtencion de los IDs de los monitores en cada zona
            String monitoresMerienda = obtenerIDsM(meriendaM);
            String monitoresSoga = obtenerIDsM(sogaM);
            String monitoresTirolina = obtenerIDsM(tirolinaM);
            String monitoresZonaC = obtenerIDsM(zonaComunM);
            //Actualizacion de la interfaz
            interfaz.actualizarMonitores(monitoresMerienda, monitoresSoga, monitoresTirolina, monitoresZonaC);
            //Tiempo aleatorio de descanso entre 1 y 2 segundos
            TimeUnit.SECONDS.sleep(r.nextInt(2) + 1);
            //Asignacion de la nueva zona para el monitor
            nuevaZona = asignarMonitor(monitor);

        } catch (InterruptedException e) {
        } finally {
            entradasM.unlock();
        }
        return nuevaZona;
    }
}
