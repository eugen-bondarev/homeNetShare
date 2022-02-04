import graphics.Window;
import common.Size;
import app.MainActivity;

public class HomeNetShare {
    public static void main(String[] args) {
        try {
            Window window = new Window(new Size(800, 600), "HomeNetShare");

            MainActivity activity = new MainActivity(window);

            while (!window.shouldClose()) {
                window.beginFrame();
                activity.onUpdate();
                window.endFrame();
            }
            window.close();
            activity.close();
        }
        catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }
}
