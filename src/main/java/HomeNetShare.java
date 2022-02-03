import graphics.Window;
import common.Size;
import app.MainActivity;

public class HomeNetShare {
    public static void main(String[] args) {
        try {
            new Window(new Size(800, 600), "HomeNetShare", new MainActivity());
        }
        catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }
}
