package graphics;

import common.Size;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import imgui.ImFontConfig;
import imgui.ImGuiStyle;
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

        File fontFile = getResourceAsFile("Roboto-Regular.ttf");
        if (fontFile != null) {
            try {
                byte[] fontFileContent = Files.readAllBytes(fontFile.toPath());
                ImFontConfig fontConfig = new ImFontConfig();
                fontConfig.setFontDataOwnedByAtlas(false);
                ImGui.getIO().getFonts().addFontFromMemoryTTF(fontFileContent, 14, fontConfig);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
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

    public static final String VERSION = "1.0.0a";

    public static class Flags {
        public static final int NONE = 0;
    }

    private List<String> dragAndDropItems = new ArrayList<>();
    public List<String> getDragAndDropItems() {
        return dragAndDropItems;
    }

    private boolean darkThemeIsSet = false;

    private void setLightTheme() {
        ImGui.styleColorsLight();
        ImGuiStyle style = ImGui.getStyle();
        style.setFrameRounding(3.0f);
        style.setFramePadding(10, 5);
        style.setWindowBorderSize(0);
        style.setFrameBorderSize(1);
        darkThemeIsSet = false;
    }

    private void setDarkTheme() {
        ImGui.styleColorsDark();
        ImGuiStyle style = ImGui.getStyle();
        style.setFrameRounding(3.0f);
        style.setFramePadding(10, 5);
        style.setWindowBorderSize(0);
        style.setFrameBorderSize(0);
        darkThemeIsSet = true;
    }

    public void toggleTheme() {
        if (darkThemeIsSet) {
            setLightTheme();
        } else {
            setDarkTheme();
        }
    }

    private long standardCursor;
    private long handCursor;

    public Window(Size size, String title, int flags) throws RuntimeException {
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize glfw.");
        }
        glfwDefaultWindowHints();
        handle = glfwCreateWindow((int)size.getWidth(), (int)size.getHeight(), String.format("%s v%s", title, VERSION), 0, 0);
        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        glfwSetDropCallback(handle, (window, count, names) -> {
            for (int i = 0; i < count; ++i) {
                String name = GLFWDropCallback.getName(names, i);
                dragAndDropItems.add(name);
            }
        });

        GL.createCapabilities();

        imGuiDevice = new ImGuiDevice(handle);
//        setDarkTheme();
        setLightTheme();

        standardCursor = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        handCursor = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
    }

    public Window(Size size, String title) {
        this(size, title, Flags.NONE);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void setDefaultCursor() {
        glfwSetCursor(handle, standardCursor);
    }

    public void setHandCursor() {
        glfwSetCursor(handle, handCursor);
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
    }

    public void close() {
        imGuiDevice.close();
        glfwDestroyWindow(handle);
        glfwTerminate();
    }
}