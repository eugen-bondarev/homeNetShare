package connection;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

class ServerThread extends Thread {
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text = "";

            do {
                byte[] bytes = input.readNBytes(4);
                int num = ByteBuffer.wrap(bytes).getInt();
                System.out.println(num);

                byte[] content = new byte[num];
                input.readNBytes(content, 0, num);
                // OutputFile f = new OutputFile("C:\\Users\\azare\\Desktop\\test42.png", content);
                writer.println("Server: " + "reverseText");

            } while (!text.equals("bye"));

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

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ip))) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
