package graphics;

import common.Size;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.glfw.Callbacks;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;
import org.lwjgl.opengl.GL;

abstract class IFrameWorker {
    abstract public void beginFrame();
    abstract public void endFrame();
}

class ImGuiDevice extends IFrameWorker {
    private final ImGuiImplGlfw implGlfw;
    private final ImGuiImplGl3 implGl3;
    private final long handle;

    public ImGuiDevice(long handle) {
        implGlfw = new ImGuiImplGlfw();
        implGl3 = new ImGuiImplGl3();
        this.handle = handle;

        ImGui.createContext();
        ImGui.getIO().setConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        implGlfw.init(this.handle, true);
        implGl3.init();
    }

    public void close() {
        implGlfw.dispose();
        implGl3.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(handle);
    }

    public void beginFrame() {
        implGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        implGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowHandle = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowHandle);
        }
    }
}

public class Window {
    private long handle;
    private ImGuiDevice imGuiDevice;

    public static class Flags {
        public static final int NONE = 0;
    }

    public static abstract class Activity {
        abstract public void onUpdate();
    }

    public Window(Size size, String title, Activity activity, int flags) throws RuntimeException {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize glfw.");
        }
        glfwDefaultWindowHints();
        handle = glfwCreateWindow((int)size.getWidth(), (int)size.getHeight(), title, 0, 0);
        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        GL.createCapabilities();

        imGuiDevice = new ImGuiDevice(handle);

        while (!glfwWindowShouldClose(handle)) {
            beginFrame();
            activity.onUpdate();
            endFrame();
        }

        imGuiDevice.close();

        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public Window(Size size, String title, Activity activity) {
        this(size, title, activity, Flags.NONE);
    }

    private void beginFrame() {
        glfwPollEvents();
        imGuiDevice.beginFrame();
    }

    private void endFrame() {
        glClear(GL_COLOR_BUFFER_BIT);

        imGuiDevice.endFrame();
        glfwSwapBuffers(handle);
    }
}
