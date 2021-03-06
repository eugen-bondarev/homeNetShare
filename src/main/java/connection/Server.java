package connection;

import common.Address;
import common.Cmd;
import common.File;
import connection.messages.FileMessage;
import connection.messages.TextMessage;

import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.regex.Pattern;

class ClientThread extends Thread {
    private Socket socket;
    private Vector<String> filesToShare;

    ClientThread(Socket socket, Vector<String> filesToShare) {
        this.socket = socket;
        this.filesToShare = filesToShare;
    }

    private void getName(OutputStream outputStream, List<String> args) throws IOException {
        ConnectionHistory.Connection establishedConnection = new ConnectionHistory.Connection(
            Address.getName(),
            Address.getAddressInHomeNet()
        );

        TextMessage textMessage = new TextMessage(establishedConnection.toString());
        outputStream.write(textMessage.getBytes());
        outputStream.flush();
    }

    private void getSharedFiles(OutputStream outputStream, List<String> args) throws IOException {
        String joined = String.join("@", filesToShare);
        TextMessage textMessage = new TextMessage(joined);
        outputStream.write(textMessage.getBytes());
        outputStream.flush();
    }

    private void getFile(OutputStream outputStream, List<String> args) throws IOException {
        String filePath = args.get(0);
        File file = new File(filePath);
        FileMessage fileMessage = new FileMessage(file);
        outputStream.write(fileMessage.getBytes());
        outputStream.flush();
    }

    private void unknownCommand(OutputStream outputStream) throws IOException {
        TextMessage textMessage = new TextMessage("Unknown command");
        outputStream.write(textMessage.getBytes());
        outputStream.flush();
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            boolean shouldClose = false;
            while (!shouldClose) {
                String msg = TextMessage.construct(input);
                Cmd cmd = Cmd.fromString(msg);

                switch (cmd.getCmd()) {
                    case "/didStateChange" -> {
                        continue;
                    }
                    case "/getName" -> {
                        getName(output, cmd.getArgs());
                        continue;
                    }
                    case "/getSharedFiles" -> {
                        getSharedFiles(output, cmd.getArgs());
                        continue;
                    }
                    case "/getFile" -> {
                        getFile(output, cmd.getArgs());
                        continue;
                    }
                    case "/exit" -> {
                        socket.close();
                        shouldClose = true;
                        continue;
                    }
                }
                System.out.printf("Unknown msg: %s\n", msg);
                unknownCommand(output);
            }

        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}

public class Server extends Thread {
    private final String ip;
    private final int port;

    public Server(ServerSocket serverSocket, String ip, int port, Vector<String> filesToShare) {
        this.serverSocket = serverSocket;
        this.ip = ip;
        this.port = port;
        this.filesToShare = filesToShare;
    }

    private ServerSocket serverSocket;
    private Vector<String> filesToShare;

    public void run() {
        try {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.printf("New client connected, address: %s%n", socket.getInetAddress().getHostAddress());

                ClientThread clientThread = new ClientThread(socket, filesToShare);
                clientThread.start();
            }
        } catch (IOException ex) {
            System.out.println("Closing server..");
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException exception) {
        }
    }
}
