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

    public void onUpdate() {
        ImGui.begin("Window");
            ImGui.inputText("IP", ip);
            ImGui.inputInt("Port", port);

            if (ImGui.button("Start server")) {
                new Server(ip.get(), port.get()).start();
            }

            if (ImGui.button("Connect to server")) {
                new Client(ip.get(), port.get()).start();
            }
        ImGui.end();
    }

    @Override
    public void close() {
    }
}
