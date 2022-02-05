package app;

import connection.Client;
import connection.Server;
import graphics.ClientUI;
import graphics.ServerUI;
import imgui.ImGui;
import graphics.Window;

public class MainActivity {
    enum Mode {
        None,
        Server,
        Client,
        Both
    }

    private Mode mode = Mode.None;

    private Server server;
    private Client client;

    private ServerUI serverUI;
    private ClientUI clientUI;

    private final Client.Bridge clientBridge = new Client.Bridge();

    public MainActivity(Window window) {
        serverUI = new ServerUI(window, server);
        clientUI = new ClientUI(window, client, clientBridge);
    }

    public void onUpdate() {
        if (mode == Mode.None) {
            ImGui.begin("Start menu");
                if (ImGui.button("Share files")) {
                    mode = Mode.Server;
                }

                ImGui.sameLine();

                if (ImGui.button("Receive files")) {
                    mode = Mode.Client;
                }
            ImGui.end();
        }

        if (mode == Mode.Server || mode == Mode.Both) {
            serverUI.onUpdate();
        }

        if (mode == Mode.Client || mode == Mode.Both) {
            clientUI.onUpdate();
        }
    }

    public void close() {
        if (server != null) { server.close(); }

        if (client != null) {
            client.commands.add("/exit");
        }
        client = null;
    }
}