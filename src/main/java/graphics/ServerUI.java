package graphics;

import app.MainActivity;
import common.*;
import connection.Server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Vector;

import imgui.*;
import imgui.flag.*;
import imgui.type.ImString;

public class ServerUI extends UIInterface {
    private Server server;

    private RemovableList<ImString> filesToShare = new RemovableList<>();
    private Vector<String> vecFilesToShare = new Vector<>();

    private ServerSocket serverSocket;

    public ServerUI(Window window) {
        super(window);
        ip.set(Address.getAddressInHomeNet());
    }

    private void share() {
        vecFilesToShare.clear();
        for (ImString imString : filesToShare.getList()) {
            vecFilesToShare.add(imString.get());
        }
    }

    public void onUpdate(IndirectReference<MainActivity.Mode> mode) {
        ImGuiHelper.maximizeNextWindow(window.getSize());
        ImGui.begin("Server", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse);

        ImGuiHelper.renderMenu(mode, window);

        ImGui.text("Server info:");
        ImGui.inputText("IP", ip);
        ImGui.inputInt("Port", port);

        if (server == null) {
            if (ImGui.button("Start server")) {
                try {
                    serverSocket = new ServerSocket(port.get(), 0, InetAddress.getByName(ip.get()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                server = new Server(serverSocket, ip.get(), port.get(), vecFilesToShare);
                server.start();
            }
        } else {
            if (ImGui.button("Stop server")) {
                try {
                    serverSocket.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
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

        float cursorY = ImGui.getCursorPosY();

        ImVec4 col = ImGui.getStyle().getColor(ImGuiCol.TabHovered);
        ImGui.getForegroundDrawList().addRect(vMin.x, cursorY, vMax.x, vMax.y, ImColor.floatToColor(col.x, col.y, col.z, col.w));

        ImGui.beginChild("DnD");
        {
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
                ImGui.setCursorPosX(ImGui.getCursorPosX() + 4);
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

            ImGui.endChild();
        }

        ImGui.end();
    }

    public void close() {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}