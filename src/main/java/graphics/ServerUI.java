package graphics;

import common.RemovableList;
import connection.Server;
import java.util.Vector;
import imgui.type.ImString;
import imgui.ImGui;

public class ServerUI extends UIInterface {
    private Server server;

    private RemovableList<ImString> filesToShare = new RemovableList<>();
    private Vector<String> vecFilesToShare = new Vector<>();

    public ServerUI(Window window, Server server) {
        super(window);
        this.server = server;
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
    }
}