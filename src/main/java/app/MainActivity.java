package app;

import common.ImGuiHelper;
import graphics.Window;
import common.IndirectReference;
import common.Size;
import connection.Client;
import connection.Server;
import graphics.ClientUI;
import graphics.ServerUI;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.ImGui;

public class MainActivity {
    public enum Mode {
        None,
        Server,
        Client,
        Both
    }

    private IndirectReference<Mode> mode = new IndirectReference<>(Mode.Server);
    private Window window;

    private Server server;
    private Client client;

    private ServerUI serverUI;
    private ClientUI clientUI;

    private final Client.Bridge clientBridge = new Client.Bridge();

    public MainActivity(Window window) {
        this.window = window;
        serverUI = new ServerUI(window, server);
        clientUI = new ClientUI(window, client, clientBridge);
    }

    private void renderMenuUI() {

        ImGuiHelper.maximizeNextWindow(window.getSize());

        ImGui.begin("Start menu", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.AlwaysAutoResize);

        ImGuiHelper.renderMenu(mode, window, false);

        if (ImGui.button("Share files")) {
            mode.set(Mode.Server);
        }

        ImGui.sameLine();

        if (ImGui.button("Receive files")) {
            mode.set(Mode.Client);
        }
        ImGui.end();
    }

    public void onUpdate() {
        Mode currentFrameMode = mode.get();

        switch (currentFrameMode) {
            case Both -> {
                serverUI.onUpdate(mode);
                clientUI.onUpdate(mode);
                break;
            }
            case Server -> {
//                ImGui.showDemoWindow();
                serverUI.onUpdate(mode);
                break;
            }
            case Client -> {
                clientUI.onUpdate(mode);
                break;
            }
            case None -> {
                renderMenuUI();
                break;
            }
        }
    }

    public void close() {
        if (server != null) {
            server.close();
            server = null;
        }

        if (client != null) {
            client.commands.add("/exit");
            client = null;
        }
    }
}