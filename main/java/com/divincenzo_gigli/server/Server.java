package com.divincenzo_gigli.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    MessageHandler messageHandler;
    ServerSocket serverSocket;
    Socket socket;
    ServerThread serverThread;

    public void start() {
        this.messageHandler = new MessageHandler();
        try {

            this.serverSocket = new ServerSocket(8888);

            while (true) {
                this.socket = serverSocket.accept();
                System.out.println("Si è connesso un nuovo client.");
                this.serverThread = new ServerThread(socket, serverSocket, messageHandler);
                serverThread.start();
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            try {
                serverSocket.close();
            } catch (Exception v) {
                v.printStackTrace();
            }
        }
    }
}