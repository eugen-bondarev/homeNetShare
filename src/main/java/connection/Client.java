package connection;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Client extends Thread {
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() {
        try (Socket socket = new Socket(ip, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text;

            Scanner keyboard = new Scanner(System.in);

            do {
                text = keyboard.nextLine();

//                InputFile f = new InputFile("C:\\Users\\azare\\Desktop\\test.png");
//                byte[] bytes = ByteBuffer.allocate(4).putInt(f.getBinaryData().length).array();
//                output.write(bytes);
//                output.write(f.getBinaryData());
//                output.flush();

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String time = reader.readLine();

                System.out.println(time);

            } while (!text.equals("bye"));

            socket.close();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
