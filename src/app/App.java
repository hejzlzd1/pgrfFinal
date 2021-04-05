package app;

import extension.global.LwjglWindow;
import render.Renderer;

public class App {
    public static void main(String[] args) {
        new LwjglWindow(new Renderer());
    }
}
