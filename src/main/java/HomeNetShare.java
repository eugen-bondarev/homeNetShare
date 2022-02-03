import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class HomeNetShare {
    public static void main(String[] args) {
        try {
            if (!glfwInit()) {
                throw new Exception("Failed to load glfw.");
            }
            glfwDefaultWindowHints();
            long handle = glfwCreateWindow(800, 600, "Hello, world!", 0, 0);
            glfwMakeContextCurrent(handle);

            GL.createCapabilities();

            while (!glfwWindowShouldClose(handle)) {
                glfwPollEvents();
                glfwSwapBuffers(handle);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
