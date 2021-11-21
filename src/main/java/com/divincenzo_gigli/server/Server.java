package com.divincenzo_gigli.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.divincenzo_gigli.client.ClientHandler;


public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) throws IOException{
        try {
            this.serverSocket = serverSocket;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void startServer() {
        try {
            
            while (!serverSocket.isClosed()) {
                
                Socket socket = serverSocket.accept();
                System.out.println("Si è connesso un nuovo client");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch (IOException e) {

            closeServerSocket();
        }
    }

    public void closeServerSocket() {

        try {

            if (serverSocket != null) {

                serverSocket.close();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
