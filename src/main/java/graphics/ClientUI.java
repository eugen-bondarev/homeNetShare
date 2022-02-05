package graphics;

import app.MainActivity;
import common.ImGuiHelper;
import common.IndirectReference;
import common.Size;
import connection.Client;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientUI extends UIInterface {
    private Client client;
    private Client.Bridge clientBridge;
    private ImString imGuiOutputDir;

    private void setOutputDir() {
        String path = System.getProperty("user.home") + "/AppData/Local/homeNetShare/output";
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        imGuiOutputDir = new ImString(path, 512);
        clientBridge.outputDir = imGuiOutputDir.get();
    }

    public ClientUI(Window window, Client client, Client.Bridge clientBridge) {
        super(window);
        this.client = client;
        this.clientBridge = clientBridge;
        setOutputDir();
    }

    public void onUpdate(IndirectReference<MainActivity.Mode> mode) {
        ImGuiHelper.maximizeNextWindow(window.getSize());

        ImGui.begin("Client", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.AlwaysAutoResize);

        ImGuiHelper.renderMenu(mode, window);

        ImGui.text("Client info:");
        ImGui.inputText("IP", ip);
        ImGui.inputInt("Port", port);

        if (client == null) {
            if (ImGui.button("Connect to server")) {
                client = new Client(ip.get(), port.get(), clientBridge);
                client.start();
            }
        } else {
            if (ImGui.button("Disconnect from server")) {
                client.commands.add("/exit");
                client = null;
            }
            if (ImGui.button("List shared files")) {
                client.commands.add("/getSharedFiles");
            }
            ImGui.separator();

            if (ImGui.inputText("Output dir", imGuiOutputDir)) {
                clientBridge.outputDir = imGuiOutputDir.get();
            }
            ImGui.sameLine();
            if (ImGui.button("Open")) {
                try {
                    Desktop.getDesktop().open(new java.io.File(imGuiOutputDir.get()));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            for (int i = 0; i < clientBridge.sharedFiles.size(); ++i) {
                if (!clientBridge.sharedFiles.get(i).isEmpty() && ImGui.button(clientBridge.sharedFiles.get(i))) {
                    String cmd = String.format("/getFile %s", clientBridge.sharedFiles.get(i));
                    client.commands.add(cmd);
                }
            }
        }
        ImGui.end();
    }
}