package com.divincenzo_gigli.server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server(new ServerSocket(5050));
            server.startServer();
        } catch (Exception e) {
            System.out.println("Errore durante l'istanza del server");
        }
    }
}
