package com.divincenzo_gigli.client;

import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        try {

           Client client = new Client(new Socket("localhost", 8888));
           client.start();
           client.inviaMessaggiAlServer();

        } catch (Exception e) {
            System.out.println("Errore durante l'istanza del client: " + e.getMessage());
        }
    }

}
