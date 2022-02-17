package common;

import app.MainActivity;
import graphics.Window;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ImGuiHelper {
    public static void maximizeNextWindow(Size windowSize) {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(windowSize.getWidth(), windowSize.getHeight());
    }

    public static boolean urlText(String url, String title) {
        boolean hovered = false;

        ImVec4 col = ImGui.getStyle().getColor(ImGuiCol.ButtonHovered);
        ImGui.pushStyleColor(ImGuiCol.Text, col.x, col.y, col.z, col.w);
        ImGui.text(title == null ? url : title);
        ImGui.popStyleColor();
        if (ImGui.isItemHovered())
        {
            hovered = true;
            if( ImGui.isMouseClicked(0) )
            {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (URISyntaxException exception) {
                        exception.printStackTrace();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            {
                ImVec2 min = ImGui.getItemRectMin();
                ImVec2 max = ImGui.getItemRectMax();
                min.y = max.y;

                ImGui.getWindowDrawList().addLine(min.x, min.y, max.x, max.y, ImColor.floatToColor(col.x, col.y, col.z, col.w));
            }
        }
        return hovered;
    }

    public static boolean urlText(String url) {
        return urlText(url, null);
    }

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
            ImGui.openPopup("About");
        }

        int flags = ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove;

        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center.x, center.y, ImGuiCond.Appearing, 0.5f, 0.5f);

        ImGui.setNextWindowSize(0, 0);
        if (ImGui.beginPopupModal("About", aboutPopup, flags)) {
            boolean linkIsHovered = false;

            ImGui.text("HomeNetShare - A simple utility for sharing files using Wi-Fi.");
            ImGui.text("Created by"); ImGui.sameLine();
            ImGui.setCursorPosX(ImGui.getCursorPosX() - 4);
            if (ImGuiHelper.urlText("https://github.com/eugen-bondarev", "Eugene Bondarev")) {
                linkIsHovered = true;
            }
            ImGui.text(String.format("Version: %s", Window.VERSION));
            ImGui.separator();

            ImGui.text("Project on GitHub:"); ImGui.sameLine();
            String projectUrl = "https://github.com/eugen-bondarev/homeNetShare";
            if (ImGuiHelper.urlText(projectUrl)) { linkIsHovered = true; }
            ImGui.text("Author:"); ImGui.sameLine();
            String authorUrl = "https://github.com/eugen-bondarev";
            if (ImGuiHelper.urlText(authorUrl)) { linkIsHovered = true; }

            if (linkIsHovered) {
                window.setHandCursor();
            }

            ImGui.endPopup();
        }
    }

    public static void renderMenu(IndirectReference<MainActivity.Mode> mode, Window window) {
        renderMenu(mode, window, true);
    }
}
