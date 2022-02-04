package app;

import common.RemovableList;
import connection.Client;
import connection.Server;
import imgui.ImGui;
import graphics.Window;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.Vector;

public class MainActivity {
    private ImString ip = new ImString("192.168.178.30");
    private ImInt port = new ImInt(8080);
    private ImString fileToSend = new ImString("C:\\Users\\azare\\Desktop\\test\\pic.png");

    RemovableList<ImString> filesToShare = new RemovableList<>();
    Vector<String> vecFilesToShare = new Vector<>();

    private Server server;
    private Client client;

    // Client data
    private ImString imGuiOutputDir = new ImString("C:\\Users\\azare\\Desktop\\test", 512);
    private Client.Bridge clientBridge = new Client.Bridge();

    private Window window;

    public MainActivity(Window window) {
        this.window = window;

        fileToSend.resize(512);
        filesToShare.getList().add(new ImString("C:\\Users\\azare\\Desktop\\test\\pic.png"));
    }

    private void share() {
        vecFilesToShare.clear();
        for (ImString imString : filesToShare.getList()) {
            vecFilesToShare.add(imString.get());
        }
    }

    public void onUpdate() {
        ImGui.begin("Server");
            ImGui.text("Server info:");
            ImGui.inputText("IP", ip);
            ImGui.inputInt("Port", port);

            if (server == null) {
                if (ImGui.button("Start server")) {
                    server = new Server(ip.get(), port.get(), vecFilesToShare);
                    server.start();
                }
            } else {
                if (ImGui.button("Stop server")) {
                    server.close();
                    server = null;
                }
            }

            ImGui.text("Files to share:");

            if (ImGui.getIO().getWantCaptureMouse()) {
                if (window.getDragAndDropItems().size() != 0) {
                    for (String dragAndDropItem : window.getDragAndDropItems()) {
                        filesToShare.getList().add(new ImString(dragAndDropItem));
                        share();
                    }
                }
            }

            if (ImGui.button("Add")) {
                filesToShare.getList().add(new ImString(512));
                share();
            }

            for (int i = 0; i < filesToShare.getList().size(); ++i) {
                ImString item = filesToShare.getList().get(i);
                if (ImGui.inputText(String.format("File #%d", i), item)) {
                    share();
                }

                ImGui.sameLine();
                if (ImGui.button(String.format("x##%d", i))) {
                    filesToShare.enqueueRemove(i);
                }
            }

            if (filesToShare.remove()) {
                share();
            }

        ImGui.end();

        ImGui.begin("Client");
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
                    client.close();
                    client = null;
                }
            }
            if (ImGui.button("List shared files")) {
                client.commands.add("/getSharedFiles");
            }
            ImGui.separator();

            if (ImGui.inputText("Output dir", imGuiOutputDir)) {
                clientBridge.outputDir = imGuiOutputDir.get();
            }

            for (int i = 0; i < clientBridge.sharedFiles.size(); ++i) {
                if (!clientBridge.sharedFiles.get(i).isEmpty() && ImGui.button(clientBridge.sharedFiles.get(i))) {
                    String cmd = String.format("/getFile %s", clientBridge.sharedFiles.get(i));
                    client.commands.add(cmd);
                }
            }
        ImGui.end();
    }

    public void close() {
        if (server != null) { server.close(); }
        if (client != null) { client.close(); }
    }
}
