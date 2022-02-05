package graphics;

import app.MainActivity;
import common.IndirectReference;
import common.RemovableList;
import connection.Server;
import java.util.Vector;

import imgui.ImColor;
import imgui.ImVec2;
import imgui.ImGui;
import imgui.type.ImString;

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

    public void onUpdate(IndirectReference<MainActivity.Mode> mode) {
        ImGui.begin("Server");
        if (ImGui.button("Back")) { mode.set(MainActivity.Mode.None); }
        ImGui.separator();
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

        ImGui.text("Drag and drop files here:");

        ImVec2 vMin = ImGui.getWindowContentRegionMin();
        ImVec2 vMax = ImGui.getWindowContentRegionMax();

        vMin.x += ImGui.getWindowPos().x;
        vMin.y += ImGui.getWindowPos().y;
        vMax.x += ImGui.getWindowPos().x;
        vMax.y += ImGui.getWindowPos().y;

        float cursorX = ImGui.getCursorPosX();
        float cursorY = ImGui.getCursorPosY();
        ImGui.getForegroundDrawList().addRect(cursorX, cursorY + 6, vMax.x, vMax.y, ImColor.intToColor(255, 255, 0, 255));
        ImGui.spacing();

        if (ImGui.getIO().getWantCaptureMouse()) {
            if (window.getDragAndDropItems().size() != 0) {
                for (String dragAndDropItem : window.getDragAndDropItems()) {
                    filesToShare.getList().add(new ImString(dragAndDropItem));
                    share();
                }
            }
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