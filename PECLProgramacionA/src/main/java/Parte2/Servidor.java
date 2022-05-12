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
            servidor = new ServerSocket(5000);
            while (true){
                System.out.println("Esperando conexion...");
                conexion = servidor.accept();
                entrada = new DataInputStream(conexion.getInputStream());
                salida = new DataOutputStream(conexion.getOutputStream());
                String mensaje = entrada.readUTF();
                int devolver = 0;
                switch (mensaje){
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
                salida.writeInt(devolver);
                conexion.close();
            }
        } catch (IOException ex) {}
    }
}
