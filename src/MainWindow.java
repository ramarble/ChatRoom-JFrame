import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame implements Runnable{

    private JPanel mainPanel;

    private JPanel userListPanel;
    private JPanel chatBox;
    private JTextField chatInputTextField;
    private JTextArea textAreaUserList;
    private JPanel chatHistoryPanel;
    private JPanel loginPanel;
    private JSplitPane splitLoginPane;
    private JPanel loginUserPanel;
    private JPanel loginPassPanel;
    private JTextField textFieldUser;
    private JTextField textFieldPass;
    private JLabel labelUser;
    private JButton button1;
    private JLabel label1;


    private void initialize() {
        setContentPane(mainPanel);

        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        textAreaUserList.setFont(new javax.swing.plaf.FontUIResource("Noto Sans",Font.PLAIN,14));
        setFont(new javax.swing.plaf.FontUIResource("Noto Sans",Font.PLAIN,14));

        setMinimumSize(new Dimension(1280,720));
        userListPanel.setMinimumSize(new Dimension(getWidth()/10, getHeight()));
        userListPanel.setPreferredSize(new Dimension(getWidth()/10, getHeight()));

        splitLoginPane.setDividerLocation(loginPanel.getWidth()/2);

        setTitle("ChatBox");
        setVisible(true);



        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                splitLoginPane.setDividerLocation(loginPanel.getWidth()/2);
                super.componentResized(e);
            }

        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

                super.windowClosed(e);

            }
        });
    }

    @Override
    public void run() {

            initialize();

    }


}
