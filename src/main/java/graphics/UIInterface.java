package graphics;

import app.MainActivity;
import common.IndirectReference;
import imgui.type.ImInt;
import imgui.type.ImString;

abstract public class UIInterface {
    protected Window window;

    protected static final int ITEM_WIDTH = 140;

    protected final ImString ip = new ImString("192.168.", 15);
    protected final ImInt port = new ImInt(8080);

    protected UIInterface(Window window) {
        this.window = window;
    }

    abstract public void onUpdate(IndirectReference<MainActivity.Mode> mode);
}