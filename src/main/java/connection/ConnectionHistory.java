package connection;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectionHistory {
    public static final String FILENAME = "connection-history.txt";

    public static class Connection {
        private String name;
        private String ip;

        private static final String SEPARATOR = " ";

        public Connection(String name, String ip) {
            this.name = name;
            this.ip = ip;
        }

        public String getName() {
            return name;
        }

        public String getIP() {
            return ip;
        }

        public String toString() {
            return String.format("%s%s%s", name, SEPARATOR, ip);
        }

        public boolean equals(Connection other) {
            return toString().equals(other.toString());
        }

        public static Connection fromString(String str) {
            String[] split = str.split(SEPARATOR);
            if (split.length == 2) {
                return new Connection(split[0], split[1]);
            }
            else {
                return new Connection("error", "error");
            }
        }
    }

    private List<Connection> connections = new ArrayList<>();

    public List<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Connection connection) {
        for (Connection item : connections) {
            if (item.equals(connection)) {
                return;
            }
        }
        connections.add(connection);
    }

    public void saveToFile(String path) throws IOException {
        File file = new File(path);
        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

        for (int i = 0; i < connections.size(); ++i) {
            bufferedWriter.write(connections.get(i).toString());
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
    }

    public void loadFromFile(String path) throws IOException {
        connections.clear();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        while (line != null) {
            Connection connection = Connection.fromString(line);
            connections.add(connection);
            line = reader.readLine();
        }
        reader.close();
    }
}
