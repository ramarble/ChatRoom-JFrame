package TCP;

import TCP.Loader.DebugLoader;

import javax.swing.*;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        DebugLoader d1 = new DebugLoader();
        Thread t2 = new Thread(d1);
        t2.start();
        d1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}