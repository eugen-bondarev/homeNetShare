package connection;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class ClientController {
    public Vector<Broadcast> broadcasts = new Vector<>();
    public void addBroadcast(Broadcast broadcast) {
        broadcasts.add(broadcast);
    }
}

class ClientThread extends Network {
    private ClientController controller;

    public ClientThread(Socket socket, ClientController controller) {
        super(socket);
        this.controller = controller;
    }

    private Vector<Broadcast> toRemove = new Vector<>();

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            while (socket.isConnected()) {
                for (Broadcast broadcast : controller.broadcasts) {
                    System.out.println("Sending file..");
                    broadcast.broadcast(output);
                    output.flush();
                    toRemove.add(broadcast);
                }

                for (Broadcast broadcast : toRemove) {
                    controller.broadcasts.remove(broadcast);
                }
                toRemove.clear();
            }

            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

public class Server extends Thread {
    private final String ip;
    private final int port;

    private Map<String, ClientThread> clientThreads;
    private Map<String, ClientController> clientControllerMap = new HashMap<>();

    public String[] getControllers() {
        return clientControllerMap.keySet().toArray(new String[0]);
    }

    public void sendFile(String path, String receiver) {
        ClientController match = clientControllerMap.get(receiver);
        if (match != null) {
            try {
                Broadcast broadcast = Broadcast.open(path);
                match.addBroadcast(broadcast);
            } catch (IOException exception) {
                System.err.printf("File not found: %s%n", path);
            }
        }
    }

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
        clientThreads = new HashMap<>();
    }

    ServerSocket serverSocket;

    public void run() {
        try {
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ip));
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                final String address = socket.getInetAddress().getHostAddress();

                {
                    System.out.println("1. Match found");
                    ClientThread match = clientThreads.get(address);
                    if (match != null) {
                        match.socket.close();
                        clientThreads.remove(address);
                    }
                }

                {
                    System.out.println("2. Match found");
                    ClientController match = clientControllerMap.get(address);
                    if (match != null) {
                        clientControllerMap.remove(address);
                    }
                }

                System.out.printf("New client connected, address: %s%n", address);

                clientControllerMap.put(address, new ClientController());

                ClientThread clientThread = new ClientThread(socket, clientControllerMap.get(address));
                clientThread.start();

                clientThreads.put(address, clientThread);
            }
        } catch (IOException ex) {
            System.out.println("Closing server..");
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
