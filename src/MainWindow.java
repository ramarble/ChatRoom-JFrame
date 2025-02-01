import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements Runnable{

    private JPanel mainPanel;

    private JPanel userList;
    private JPanel chatBox;
    private JTextField chatInputTextField;
    private JTextArea textAreaUserList;
    private JPanel chatHistory;


    private void initialize() {
        setContentPane(mainPanel);

        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setMinimumSize(new Dimension(640,480));
        textAreaUserList.setFont(new javax.swing.plaf.FontUIResource("Noto Sans",Font.PLAIN,16));

        setTitle("ChatBox");
        setVisible(true);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                super.componentResized(e);
            }

        });





        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
                super.windowClosed(e);
            }
        });
    }


    public Dimension getAllocatedPercentageOfScreen(JPanel frameComponent, int divideWidth, int divideHeight) {
        return new Dimension(frameComponent.getWidth()/divideWidth,frameComponent.getHeight()/divideHeight);
    }
    @Override
    public void run() {

            initialize();

    }


}
