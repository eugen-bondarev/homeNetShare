package common;

import app.MainActivity;
import graphics.Window;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;

public class ImGuiHelper {
    public static void maximizeNextWindow(Size windowSize) {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(windowSize.getWidth(), windowSize.getHeight());
    }

//    private static boolean aboutPopup = false;
    private static ImBoolean aboutPopup = new ImBoolean(false);

    public static void renderMenu(IndirectReference<MainActivity.Mode> mode, Window window, boolean showBackButton) {
        if (ImGui.beginMenuBar()) {
            if (showBackButton) {
                // Illusion
                {
                    ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
                    ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 0);
                    ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
                    ImGui.setCursorPos(ImGui.getCursorPosX() - 4,ImGui.getCursorPosY() + 3);
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 8, 2);
                    if (ImGui.button("Back")) {
                        mode.set(MainActivity.Mode.None);
                    }
                    ImGui.popStyleVar();
                    ImGui.popStyleVar();
                    ImGui.popStyleVar();
                    ImGui.popStyleColor();
                    ImGui.setCursorPosY(ImGui.getCursorPosY() - 3);
                }
            }

            if (ImGui.beginMenu("Options")) {
                if (ImGui.menuItem("Toggle dark theme")) {
                    window.toggleTheme();
                }
                if (ImGui.menuItem("About")) {
                    aboutPopup.set(true);
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        if (aboutPopup.get()) {
            ImGui.openPopup("popup");
        }
        if (ImGui.beginPopupModal("popup", aboutPopup)) {
            ImGui.text("All those beautiful files will be deleted.\nThis operation cannot be undone!\n\n");

            ImGui.endPopup();
        }
    }

    public static void renderMenu(IndirectReference<MainActivity.Mode> mode, Window window) {
        renderMenu(mode, window, true);
    }
}
