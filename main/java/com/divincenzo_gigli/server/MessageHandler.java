package com.divincenzo_gigli.server;

import java.util.HashMap;

public class MessageHandler {

    private HashMap<String, ServerThread> clientHashMap;

    public MessageHandler() {
        clientHashMap = new HashMap<>();
    }

    public boolean aggiungiClient(String clientUsername, ServerThread serverThread) {

        if (clientHashMap.putIfAbsent(clientUsername, serverThread) != null) {
            return true;
        }

        return false;
    }

    public void rimuoviClient(String clientUsername, ServerThread serverThread) {
        clientHashMap.remove(clientUsername, serverThread);
    }

    public void broadcastMessage(String mittente, String message) {
        if (clientHashMap.size() > 1) {
            for (String nome : clientHashMap.keySet()) {
                if (!nome.equals(mittente)) {
                    clientHashMap.get(nome).sendMessage(message);
                }
            }
        } else {
            for (String nome : clientHashMap.keySet()) {
                privateMessage("", nome, "Sei l'unico presente nella chat!");
            }
        }
    }

    public String getClientList() {
        String clientList = "";

        for (String clientName : clientHashMap.keySet()) {
            clientList += clientName + ", ";
        }
        return clientList;
    }

    public void privateMessage(String mittente, String destinatario, String messaggio) {

        String message = "";
        for (String clientName : clientHashMap.keySet()) {
            if (destinatario.equalsIgnoreCase(clientName)) {
                if (!mittente.equals("")) {
                    message = "(Privato) Da " + mittente + ": " + messaggio;
                } else {
                    message = messaggio;
                }
                clientHashMap.get(destinatario).sendMessage(message);
                break;
            }
        }
    }
}
