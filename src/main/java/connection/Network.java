package connection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Network extends Thread {
    protected Socket socket;

    protected Network() {

    }

    protected Network(Socket socket) {
        this.socket = socket;
    }
}
