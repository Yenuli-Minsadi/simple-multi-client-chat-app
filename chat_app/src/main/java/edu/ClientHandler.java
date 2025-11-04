package edu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error in ClientHandler during data transfer");
            e.getMessage();
            e.printStackTrace();
            closeEverything();
        }
    }

    @Override
    public void run() {
        try {
        while (socket.isConnected()) {
            String message = in.readUTF();
            broadcastMessage(message);
        }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeEverything();
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            if (client != null && client != this) {
                client.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            closeEverything();
        }
    }

    public  void closeEverything() {
        clients.remove(this);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isConnected()) socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
