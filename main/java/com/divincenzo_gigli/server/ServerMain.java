package com.divincenzo_gigli.server;

public class ServerMain {
    
    public static void main(String[] args) {

        try {

            Server server = new Server();
            server.start();

        } catch (Exception e) {
            System.out.println("Errore durante l'istanza del server: " + e.getMessage());
        }
    }
}
