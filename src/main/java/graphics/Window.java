package graphics;

import common.Size;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import org.lwjgl.glfw.Callbacks;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

abstract class IFrameWorker {
    abstract public void beginFrame();
    abstract public void endFrame();
}

class ImGuiDevice extends IFrameWorker {
    private final ImGuiImplGlfw implGlfw;
    private final ImGuiImplGl3 implGl3;
    private final long handle;

    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ImGuiDevice(long handle) {
        implGlfw = new ImGuiImplGlfw();
        implGl3 = new ImGuiImplGl3();
        this.handle = handle;

        ImGui.createContext();
        ImGui.getIO().setConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        ImGui.getIO().setConfigFlags(ImGuiConfigFlags.DockingEnable);


        File f = getResourceAsFile("Roboto-Regular.ttf");
        try {
            byte[] content = Files.readAllBytes(f.toPath());
            ImFontConfig config = new ImFontConfig();
            config.setFontDataOwnedByAtlas(false);
            ImGui.getIO().getFonts().addFontFromMemoryTTF(content, 16, config);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

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
        glClear(GL_COLOR_BUFFER_BIT);

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
    private final long handle;
    private final ImGuiDevice imGuiDevice;

    public static class Flags {
        public static final int NONE = 0;
    }

    private List<String> dragAndDropItems = new ArrayList<>();
    private boolean dragAndDropFlag = false;

    public List<String> getDragAndDropItems() {
        return dragAndDropItems;
    }

    public Window(Size size, String title, int flags) throws RuntimeException {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize glfw.");
        }
        glfwDefaultWindowHints();
        handle = glfwCreateWindow((int)size.getWidth(), (int)size.getHeight(), title, 0, 0);
        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        glfwSetDropCallback(handle, (window, count, names) -> {
            for (int i = 0; i < count; ++i) {
                String name = GLFWDropCallback.getName(names, i);
                dragAndDropItems.add(name);
            }
            dragAndDropFlag = true;
        });

        GL.createCapabilities();

        imGuiDevice = new ImGuiDevice(handle);
    }

    public Window(Size size, String title) {
        this(size, title, Flags.NONE);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public Size getSize() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(handle, width, height);
        return new Size(width[0], height[0]);
    }

    public void beginFrame() {
        glfwPollEvents();
        imGuiDevice.beginFrame();
    }

    public void endFrame() {
        imGuiDevice.endFrame();
        glfwSwapBuffers(handle);

        dragAndDropItems.clear();
        dragAndDropFlag = false;
    }

    public void close() {
        imGuiDevice.close();
        glfwDestroyWindow(handle);
        glfwTerminate();
    }
}