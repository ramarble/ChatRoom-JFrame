import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame implements Runnable{

    private JPanel mainPanel;
    private JTextField textField1;

    private void initialize() {
        this.setSize(400,400);
        this.setContentPane(mainPanel);
        this.setTitle("ChatBox");
        this.setLayout(null);
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
                super.windowClosed(e);
            }
        });
    }

    @Override
    public void run() {

            initialize();



    }


}
