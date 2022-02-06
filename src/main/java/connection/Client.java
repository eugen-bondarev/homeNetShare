package connection;

import common.Address;
import common.Cmd;
import common.File;
import connection.messages.FileMessage;
import connection.messages.TextMessage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class Client extends Network {
    private Vector<Cmd> commands = new Vector<>();

    private String ip;
    private int port;
    private Bridge bridge;

    private long startTime;
    private long elapsedTime;

    public static class Bridge {
        private Vector<String> sharedFiles = new Vector<>();
        private String outputDir;
        private String settingsDir;
        private ConnectionHistory connectionHistory = new ConnectionHistory();

        public Vector<String> getSharedFiles() {
            return sharedFiles;
        }

        public void setOutputDir(String outputDir) {
            this.outputDir = outputDir;
        }

        public Bridge(String outputDir, String settingsDir) {
            this.outputDir = outputDir;
            this.settingsDir = settingsDir;
            System.out.printf("Connection history file: %s\n", getConnectionHistoryFilePath());
            if (Files.exists(Path.of(getConnectionHistoryFilePath()))) {
                try {
                    connectionHistory.loadFromFile(getConnectionHistoryFilePath());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        public ConnectionHistory getConnectionHistory() {
            return connectionHistory;
        }

        public String getConnectionHistoryFilePath() {
            return String.format("%s/%s", settingsDir, ConnectionHistory.FILENAME);
        }
    }

    public void passCommand(Cmd command) {
        commands.add(command);
    }

    public Client(String ip, int port, Bridge bridge) {
        this.ip = ip;
        this.port = port;
        this.bridge = bridge;
    }

    private boolean connectionFailed = false;

    public boolean getConnectionFailed() {
        return connectionFailed;
    }

    private void pollState() {
        long current = System.currentTimeMillis();
        long deltaTime = current - startTime;
        startTime = current;

        if (elapsedTime >= 1000) {
            commands.add(new Cmd("/getSharedFiles"));
//            commands.add("/getSharedFiles");
            elapsedTime = 0;
        } else {
            elapsedTime += deltaTime;
        }
    }

    private void getName(InputStream inputStream) throws IOException {
        String msg = TextMessage.construct(inputStream);
        ConnectionHistory.Connection establishedConnection = ConnectionHistory.Connection.fromString(msg);
        // CONSOLE
        System.out.printf("Connection established with %s!\n", establishedConnection);
        bridge.connectionHistory.addConnection(establishedConnection);
        bridge.connectionHistory.saveToFile(bridge.getConnectionHistoryFilePath());
    }

    private void getSharedFiles(InputStream inputStream) throws IOException {
        String msg = TextMessage.construct(inputStream);
        bridge.sharedFiles.clear();
        bridge.sharedFiles.addAll(Arrays.asList(msg.split(Pattern.quote("@"))));
    }

    private void getFile(InputStream inputStream) throws IOException {
        File file = FileMessage.construct(inputStream);
        String newName = file.getFilenameWithoutExtension() + new Date().getTime();
        if (!file.getExtension().isEmpty()) {
            newName = newName + String.format(".%s", file.getExtension());
        }
        file.save(String.format("%s/%s", bridge.outputDir, newName));
    }

    public void run() {
        try {
            socket = new Socket(ip, port);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            boolean shouldClose = false;

            // Get to know each other.
            commands.add(new Cmd("/getName", Address.getName()));

            startTime = System.currentTimeMillis();
            while (!shouldClose)
            {
                pollState();
                if (commands.size() == 0) continue;

                Cmd value = commands.get(0);

                TextMessage textMessage = new TextMessage(value.toString());
                output.write(textMessage.getBytes());
                output.flush();

                switch (value.getCmd()) {
                    case "/getName" -> {
                        getName(input);
                    }
                    case "/getSharedFiles" -> {
                        getSharedFiles(input);
                    }
                    case "/getFile" -> {
                        getFile(input);
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
        connectionFailed = true;
    }
}
