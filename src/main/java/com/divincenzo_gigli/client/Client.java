package com.divincenzo_gigli.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    BufferedReader bufferedReader; //input dal server
    BufferedWriter bufferedWriter; //output dal server
    Socket socket;
    String username;

    public Client(Socket socket){

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Inserisci il tuo nome: "); 
            this.username = scanner.nextLine();
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (Exception e) {
            
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void inviaMessaggio(){

        Scanner scanner = new Scanner(System.in);
       
        try {
            
            bufferedWriter.write(username + '\n');
            bufferedWriter.flush();

            while(socket.isConnected()){

                String message = scanner.nextLine();
                bufferedWriter.write(username + ": " + message + '\n');
                bufferedWriter.flush();
            }

        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void messageListener() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                String msgFromGroupChat;

                while (socket.isConnected()) {

                    try {

                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);

                    } catch (IOException e) {

                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    
    public void closeEverything(Socket socket, BufferedReader inputDalServer, BufferedWriter bufferedWriter){

        try {
            
            if(inputDalServer != null){

                inputDalServer.close();
            }
            if(bufferedWriter != null){

                bufferedWriter.close();
            }
            if(socket != null){

                socket.close();
            }

        } catch (IOException e) {
            
            e.printStackTrace();
        }

    }
}
