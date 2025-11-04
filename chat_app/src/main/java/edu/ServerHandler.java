package edu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
    private ServerSocket serverSocket;
    private Socket socket;
    private static ServerHandler serverHandler;

    private List<ClientHandler> clientHandlerList = new ArrayList<>();

    private ServerHandler() throws IOException {
        serverSocket = new ServerSocket(5000);
    }

    public static ServerHandler getInstance() throws IOException {
        if (serverHandler == null) {
            serverHandler = new ServerHandler();
        }
        return serverHandler;
    }

    public void createSocket() {
        while (!serverSocket.isClosed()) {
            try {
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clientHandlerList);
                clientHandlerList.add(clientHandler);
                new Thread(clientHandler).start();
                System.out.println("New Client Connected " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.getMessage();
                System.out.println("Couldn't accept client connection, Server continues...");
                e.printStackTrace();
            }
        }
    }
}
