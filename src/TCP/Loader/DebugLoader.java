package TCP.Loader;

import TCP.Client.ChatWindow;
import TCP.Server.ServerMainThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DebugLoader extends JFrame implements Runnable {
    private JButton buttonLoadDebug;
    private JPanel panel1;
    private JButton buttonLoadServer;
    private JButton buttonLoadClient;


    public ActionListener LoadClientAction() {
        return e -> {
            ChatWindow m = new ChatWindow();
            Thread t = new Thread(m);
            t.start();};
    }
    public ActionListener LoadServerAction() {
        buttonLoadDebug.setEnabled(false);
        buttonLoadServer.setEnabled(false);
        return e -> {
            new Thread(new ServerMainThread()).start();};
    }

    public WindowFocusListener onFocusReloadButtons() {
        return new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (!ServerMainThread.isServerTerminated()) {
                    buttonLoadDebug.setEnabled(true);
                    buttonLoadServer.setEnabled(true);
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        };
    }

    @Override
    public void run() {
        this.addWindowFocusListener(onFocusReloadButtons());
        buttonLoadDebug.addActionListener(LoadClientAction());
        buttonLoadDebug.addActionListener(LoadClientAction());
        buttonLoadDebug.addActionListener(LoadServerAction());

        buttonLoadServer.addActionListener(LoadServerAction());
        buttonLoadClient.addActionListener(LoadClientAction());

        setPreferredSize(new Dimension(600,400));
        setMinimumSize(new Dimension(600,400));
        setContentPane(panel1);
        setVisible(true);
    }
}
