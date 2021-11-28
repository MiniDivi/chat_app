package com.divincenzo_gigli.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerThread extends Thread {

    ServerSocket serverSocket;
    Socket socket;
    MessageHandler messageHandler;
    BufferedReader inputBuffer; // input dal client
    BufferedWriter outputBuffer; // output verso il client
    private String clientUsername;
    private String message;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){0,12}[a-zA-Z0-9]$";
    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    public ServerThread(Socket socket, ServerSocket serverSocket, MessageHandler messageHandler) throws IOException {
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.messageHandler = messageHandler;
        inputBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        try {
            chiediUsername();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            closeConnections(socket, inputBuffer, outputBuffer);
        }
    }

    public void chiediUsername() {
        try {

            sendMessage("Inserisci il tuo username: ");
            clientUsername = inputBuffer.readLine();

            while(!isUsernameValid(clientUsername)){
                sendMessage("Non puoi usare questo username, rispetta le seguenti regole: ");
                sendMessage("1. l'username deve contenere caratteri alfanumerici.");
                sendMessage("2. non puoi usare caratteri speciali come primo carattere.");
                sendMessage("3. i caratteri speciali non possono essere consecutivi.");
                sendMessage("4. L'username deve contenere minimo 2 caratteri e massimo 14 caratteri.");
                sendMessage("Inserisci un nuovo username: ");
                clientUsername = inputBuffer.readLine();
            }

            while (messageHandler.aggiungiClient(clientUsername, this)) {
                sendMessage("(Server): username già in uso, inseriscine un altro: ");
                clientUsername = inputBuffer.readLine();
            }


            messageHandler.privateMessage("", clientUsername, "Ciao " + clientUsername + ", benvenuto nella chat!");
            messageHandler.privateMessage("", clientUsername, "Lista utenti connessi: " + messageHandler.getClientList());
            message = "" + clientUsername + " si è unito alla chat!";
            messageHandler.broadcastMessage(clientUsername, message);
            outputBuffer.flush();

            messageListener();
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            closeConnections(socket, inputBuffer, outputBuffer);
        }

    }

    public void messageListener() {
        while (socket.isConnected()) {
            try {
                message = inputBuffer.readLine();

                if (message.startsWith("@esci")) {

                    messageHandler.rimuoviClient(clientUsername, this);

                    messageHandler.broadcastMessage("", clientUsername + " è uscito dalla chat!");
                    messageHandler.broadcastMessage("", "Lista utenti: " + messageHandler.getClientList());
                    
                    closeConnections(socket, inputBuffer, outputBuffer);

                } else if (message.startsWith("@lista")) {
                    messageHandler.privateMessage("", clientUsername, messageHandler.getClientList());

                } else if (message.startsWith("@privato")) {

                    // Il blocco di codice sottostante permette di inviare il messaggio completo
                    // anche in caso di un errore di scrittura da parte del client, infatti se esso
                    // scriverà:
                    // @privato@username@ciao come @va?
                    // verrà inviato comunque: ciao come va?
                    // al posto di: ciao come
                    String splittedMessage[] = message.split("@");
                    message = "";
                    for (int i = 3; i < splittedMessage.length; i++) {
                        message += splittedMessage[i];
                    }
                    messageHandler.privateMessage(clientUsername, splittedMessage[2], message);
                } else {
                    message = clientUsername + ": " + message;
                    messageHandler.broadcastMessage(clientUsername, message);
                }
            } catch (Exception e) {
                closeConnections(socket, inputBuffer, outputBuffer);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputBuffer.write(message + '\n');
            outputBuffer.flush();
        } catch (Exception e) {
            System.out.println("Errore durante l'invio del messaggio");
            closeConnections(socket, inputBuffer, outputBuffer);
        }

    }
    public void closeConnections(Socket socket, BufferedReader inputBuffer, BufferedWriter outputBuffer){

        messageHandler.rimuoviClient(clientUsername, this);
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

    public static boolean isUsernameValid(final String clientUsername) {
        Matcher matcher = pattern.matcher(clientUsername);
        return matcher.matches();
    }
}
