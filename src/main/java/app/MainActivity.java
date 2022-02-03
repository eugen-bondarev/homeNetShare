package app;

import imgui.ImGui;

import graphics.Window;

public class MainActivity extends Window.Activity {
    public void onUpdate() {
        ImGui.begin("Window");
        ImGui.end();

        ImGui.showDemoWindow();
    }
}
