package com.divincenzo_gigli.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;



public class Client extends Thread{


    Socket socket;
    private Scanner scanner;
    BufferedReader inputBuffer;
    BufferedWriter outputBuffer;
 
    public Client(Socket socket) { //connessione del socket

        this.socket = socket;
        this.scanner = new Scanner(System.in);

        try {
            
            inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            closeConnections(socket, inputBuffer, outputBuffer);
        }
    }

    public void run(){
        inputDalServer();
        inviaMessaggiAlServer();
    }

    public void inputDalServer(){
                while(socket.isConnected()){
                    try {
                        System.out.println(inputBuffer.readLine());
                    } catch (Exception e) {
                        System.out.println("Errore: " + e.getMessage());
                        closeConnections(socket, inputBuffer, outputBuffer);
                    }
                }
            }

    public void inviaMessaggiAlServer(){
        String message;

        try {
            while(socket.isConnected()){
                message = scanner.nextLine();
                outputBuffer.write(message + '\n');
                outputBuffer.flush();
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            closeConnections(socket, inputBuffer, outputBuffer);
        }
    }
    
    public void closeConnections(Socket socket, BufferedReader inputBuffer, BufferedWriter outputBuffer){
        try {

            if(inputBuffer != null){
                inputBuffer.close();
            }

            if(outputBuffer != null){
                outputBuffer.close();
            }

            if(socket != null){
                socket.close();
            }

        } catch (IOException e) {    
            e.printStackTrace();
        }
    }
}
