package graphics;

import imgui.type.ImInt;
import imgui.type.ImString;

public class UIInterface {
    protected Window window;
    protected final ImString ip = new ImString("192.168.178.30");
    protected final ImInt port = new ImInt(8080);

    protected UIInterface(Window window) {
        this.window = window;
    }
}