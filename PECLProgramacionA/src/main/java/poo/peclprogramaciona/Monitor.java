/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poo.peclprogramaciona;

/**
 *
 * @author Usuario
 */
public class Monitor extends Thread {
    
    private String id;
    private Campamento campamento;
    
    public void Monitor(String id, Campamento campamento){
        this.id = id;
        this.campamento = campamento;
    }
    @Override
    public void run(){
        
    }
}
