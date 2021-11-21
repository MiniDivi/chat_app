package com.divincenzo_gigli.client;

import java.io.IOException;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) throws IOException{
        
        try {
            Client client = new Client(new Socket("localhost", 5050));
            client.messageListener();
            client.inviaMessaggio();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}
