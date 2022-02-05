package app;

import common.ImGuiHelper;
import common.IndirectReference;
import graphics.Window;
import graphics.ClientUI;
import graphics.ServerUI;
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

    private ServerUI serverUI;
    private ClientUI clientUI;

    public MainActivity(Window window) {
        this.window = window;
        serverUI = new ServerUI(window);
        clientUI = new ClientUI(window);
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
        serverUI.close();
        clientUI.close();
    }
}