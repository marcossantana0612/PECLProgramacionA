/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parte2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author beasa
 */
public class Servidor extends Thread {
    
    private ServerSocket servidor;
    private Socket conexion;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private Campamento c;

    
    public Servidor(Campamento campamento){
        this.c = campamento;
    }
    
    public void run(){
        try {
            servidor = new ServerSocket(5000);                              //Creación del servidor
            while (true){
                System.out.println("Esperando conexion...");
                conexion = servidor.accept();                               //Estableciendo conexión
                entrada = new DataInputStream(conexion.getInputStream());   //Creación de flujo de entrada
                salida = new DataOutputStream(conexion.getOutputStream());  //Creación de flujo de salida
                String mensaje = entrada.readUTF();                         //Recepción de información
                int devolver = 0;
                switch (mensaje){
                    //Casos de petición de información posibles
                    //Se le asigna a la variable 'devolver' la cantidad solicitada en cada caso
                    case "0":
                        devolver = c.getTirolina().size();
                        break;
                    case "1":
                        devolver = c.getContadorTiro();
                        break;
                    case "2":
                        devolver = c.getMerendando().size();
                        break;
                    case "3":
                        devolver = c.getSucios().availablePermits();
                        break;
                    case "4":
                        devolver = c.getLimpios().availablePermits();
                        break;
                    case "5":
                        devolver = c.getSoga().size();
                        break;
                    default:
                        devolver = c.getNinio(mensaje);
                        break;
                }
                salida.writeInt(devolver);                                  //Envio de información
                conexion.close();                                           //Cierre de conexión
            }
        } catch (IOException ex) {}
    }
}
