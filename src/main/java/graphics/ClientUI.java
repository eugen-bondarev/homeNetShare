package graphics;

import app.MainActivity;
import common.Address;
import common.Cmd;
import common.ImGuiHelper;
import common.IndirectReference;
import connection.Client;
import connection.ConnectionHistory;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientUI extends UIInterface {
    private Client client;
    private Client.Bridge clientBridge;
    private ImString imGuiOutputDir;

    private void setOutputDir() {
        String outputDir = System.getProperty("user.home") + "/AppData/Local/homeNetShare/output";
        String settingsDir = System.getProperty("user.home") + "/AppData/Local/homeNetShare/settings";
        try {
            Files.createDirectories(Paths.get(outputDir));
            Files.createDirectories(Paths.get(settingsDir));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        imGuiOutputDir = new ImString(outputDir, 512);
        clientBridge = new Client.Bridge(imGuiOutputDir.get(), settingsDir);
    }

    public ClientUI(Window window) {
        super(window);
        setOutputDir();
    }

    public void onUpdate(IndirectReference<MainActivity.Mode> mode) {
        ImGuiHelper.maximizeNextWindow(window.getSize());

        ImGui.begin("Client", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.AlwaysAutoResize);

        ImGuiHelper.renderMenu(mode, window);

        ImGui.text("Client info:");

        ImGui.setNextItemWidth(ITEM_WIDTH);
        ImGui.inputText("IP", ip);
        ImGui.sameLine();
        if (ImGui.button("Saved connections")) {
            ImGui.openPopup("connection_history");
        }

        if (ImGui.beginPopup("connection_history")) {
            ImGui.text("Previous connections");
            ImGui.separator();
            for (ConnectionHistory.Connection connection : clientBridge.getConnectionHistory().getConnections()) {
                if (ImGui.button(connection.toString())) {
                    ip.set(connection.getIP());
                }
            }
            ImGui.endPopup();
        }

        ImGui.setNextItemWidth(ITEM_WIDTH);
        ImGui.inputInt("Port", port);

        if (client == null) {
            boolean disabled = !Address.isValidIP(ip.get());

            if (disabled) {
                ImGui.pushStyleVar(ImGuiStyleVar.Alpha, ImGui.getStyle().getAlpha() * 0.5f);

                ImVec4 col = ImGui.getStyle().getColor(ImGuiCol.Button);
                ImGui.pushStyleColor(ImGuiCol.Button, col.x, col.y, col.z, col.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, col.x, col.y, col.z, col.w);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, col.x, col.y, col.z, col.w);
            }

            if (ImGui.button("Connect to server", ITEM_WIDTH, 0)) {
                if (!disabled) {
                    client = new Client(ip.get(), port.get(), clientBridge);
                    client.start();
                }
            }

            if (disabled) {
                ImGui.popStyleColor(3);
                ImGui.popStyleVar();

                if (ImGui.isItemHovered())
                {
                    ImGui.beginTooltip();
                    ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
                    ImGui.textUnformatted("Please specify a valid server IP address.");
                    ImGui.popTextWrapPos();
                    ImGui.endTooltip();
                }
            }
        } else {
            if (client.getConnectionFailed()) {
                try {
                    client.join();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                client = null;
            }
            else {
                if (ImGui.button("Disconnect from server", ITEM_WIDTH, 0)) {
                    client.passCommand("/exit");
                    client = null;
                }
                if (ImGui.button("List shared files", ITEM_WIDTH, 0)) {
                    client.passCommand("/getSharedFiles");
                }
                ImGui.separator();

                if (ImGui.inputText("Output dir", imGuiOutputDir)) {
                    clientBridge.setOutputDir(imGuiOutputDir.get());
                }
                ImGui.sameLine();
                if (ImGui.button("Open")) {
                    try {
                        Desktop.getDesktop().open(new java.io.File(imGuiOutputDir.get()));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                ImGui.beginChild("Files");
                for (int i = 0; i < clientBridge.getSharedFiles().size(); ++i) {
                    if (!clientBridge.getSharedFiles().get(i).isEmpty() && ImGui.button(clientBridge.getSharedFiles().get(i))) {
                        Cmd cmd = new Cmd("/getFile", clientBridge.getSharedFiles().get(i));
                        client.passCommand(cmd.toString());
                    }
                }
                ImGui.endChild();
            }
        }
        ImGui.end();
    }

    public void close() {
        if (client == null) {
            return;
        }
        client.passCommand("/exit");
    }
}