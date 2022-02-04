package connection;

import common.File;
import connection.messages.FileMessage;
import connection.messages.TextMessage;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.regex.Pattern;

public class Client extends Network {
    public Vector<String> commands = new Vector<>();

    private String ip;
    private int port;

    public static class Bridge {
        public Vector<String> sharedFiles = new Vector<>();
        public String outputDir;
    }

    private Bridge bridge;

    public Client(String ip, int port, Bridge bridge) {
        this.ip = ip;
        this.port = port;
        this.bridge = bridge;
    }

    public void run() {
        try {
            socket = new Socket(ip, port);
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            Scanner keyboard = new Scanner(System.in);

            boolean shouldClose = false;
            while (!shouldClose)
            {
                if (commands.size() == 0) continue;

                String value = commands.get(0);

                TextMessage textMessage = new TextMessage(value);
                output.write(textMessage.getBytes());
                output.flush();

                String cmd = value.split(Pattern.quote(" "))[0];

                switch (cmd) {
                    case "/getSharedFiles" -> {
                        String msg = TextMessage.construct(input);
                        System.out.printf("Message from server: %s\n", msg);

                        bridge.sharedFiles.clear();
                        bridge.sharedFiles.addAll(Arrays.asList(msg.split(Pattern.quote("@"))));
                    }
                    case "/getFile" -> {
                        File file = FileMessage.construct(input);
                        String newName = file.getFilenameWithoutExtension() + new Date().getTime();
                        if (!file.getExtension().isEmpty()) {
                            newName = newName + String.format(".%s", file.getExtension());
                        }
                        file.save(String.format("%s/%s", bridge.outputDir, newName));
                    }
                    case "/exit" -> {
                        socket.close();
                        shouldClose = true;
                    }
                }

                commands.clear();
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
