import javax.swing.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        MainWindow m = new MainWindow();
        Thread t = new Thread(m);
        t.start();
        //System.exit(0);
    }
}