package connection;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client extends Network {
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        try {
            socket = new Socket(ip, port);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            while (socket.isConnected()) {
                if (input.available() != 0) {
                    Broadcast fileBroadcast = Broadcast.readFromInputStream(input);
                    String[] split = fileBroadcast.getName().split(Pattern.quote("."), 0);
                    split[0] = split[0] + new Date().getTime();
                    System.out.println(String.join(".", split));
                    fileBroadcast.save(String.join(".", split));
                }
            }

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
