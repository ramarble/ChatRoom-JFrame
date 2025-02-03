package Loader;
import Client.ClientWindow;

import javax.swing.*;


public class MainLoader {
    public static void main(String[] args) throws InterruptedException {

        ClientWindow m = new ClientWindow();
        Thread t = new Thread(m);
        t.start();


        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}