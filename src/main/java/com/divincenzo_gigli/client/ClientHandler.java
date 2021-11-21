package com.divincenzo_gigli.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String clientUsername;

    public ClientHandler(Socket socket) {

        try {
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = input.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " è entrato nella chat");
            broadcastMessage("SERVER: lista utenti: " + sendClientList());

        } catch (IOException e) {
            closeEverything(socket, input, output);
        }
    }

    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {
            try {

                messageFromClient = input.readLine();

                if (messageFromClient.contains("@msg")) {
                    String stringa = messageFromClient.substring(messageFromClient.indexOf(":") + 2); // ottengo il
                                                                                                      // messaggio senza
                                                                                                      // il nome del
                                                                                                      // mittente
                    if (stringa.startsWith("@msg")) {
                        String splittedMessage[] = stringa.split("@");
                        String message = "";
                        for (int i = 3; i < splittedMessage.length; i++) {
                            message += splittedMessage[i];
                        }
                        privateMessage(splittedMessage[2], message);
                    }
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, input, output);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.output.write(messageToSend + '\n');
                    clientHandler.output.flush(); // svuota il buffer
                }
            } catch (IOException e) {
                closeEverything(socket, input, output);
            }
        }
    }

    public void privateMessage(String nome, String messaggio) {
        System.out.println("sono nel metodo messaggi privati!");
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (nome.equals(clientHandler.clientUsername)) {
                    clientHandler.output.write("(messaggio privato): " + messaggio + '\n');
                    clientHandler.output.flush(); // svuota il buffer
                }
            } catch (IOException e) {
                closeEverything(socket, input, output);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " è uscito dalla chat");
    }

    public String sendClientList() {
        String clients = "";

        for (ClientHandler clientHandler : clientHandlers) {
            clients += clientHandler.clientUsername + ", ";
        }

        return clients;
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        removeClientHandler();

        try {

            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
