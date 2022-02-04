package app;

import connection.Client;
import connection.Server;
import imgui.ImGui;
import graphics.Window;
import imgui.type.ImInt;
import imgui.type.ImString;

public class MainActivity extends Window.Activity {
    private ImString ip = new ImString("192.168.178.30");
    private ImInt port = new ImInt(8080);
    private ImString fileToSend = new ImString("C:\\Users\\azare\\Desktop\\test\\pic.png");
    private ImString receiver = new ImString("192.168.178.30");

    private Server server;
    private Client client;

    public MainActivity() {
        fileToSend.resize(512);
    }

    public void onUpdate() {
        ImGui.begin("Window");
            ImGui.inputText("IP", ip);
            ImGui.inputInt("Port", port);

            if (server != null) {
                ImGui.text("Connected users:");
                for (int i = 0; i < server.getControllers().length; ++i) {
                    ImGui.text(server.getControllers()[i]);
                }
                ImGui.inputText("File to send", fileToSend);
                ImGui.inputText("Receiver", receiver);
                if (ImGui.button("Send")) {
                    server.sendFile(fileToSend.get(), receiver.get());
                }
            }

//            if (client == null) {
                if (server == null) {
                    if (ImGui.button("Start server")) {
                        server = new Server(ip.get(), port.get());
                        server.start();
                    }
                } else {
                    if (ImGui.button("Stop server")) {
                        server.close();
                        server = null;
                    }
                }
//            }

//            if (server == null) {
                if (client == null) {
                    if (ImGui.button("Connect to server")) {
                        client = new Client(ip.get(), port.get());
                        client.start();
                    }
                } else {
                    if (ImGui.button("Disconnect from server")) {
                        client.close();
                        client = null;
                    }
                }
//            }
        ImGui.end();
    }

    @Override
    public void close() {
        if (server != null) { server.close(); }
        if (client != null) { client.close(); }
    }
}
