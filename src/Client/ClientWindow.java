package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ClientWindow extends JFrame implements Runnable{

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
    private JButton LoginButton;
    private JTextArea textAreaChatHistory;
    private JLabel label1;
    private ClientConnection clientConnection;


    private void setJFrameDesign() {
        getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        textAreaUserList.setFont(new javax.swing.plaf.FontUIResource("Noto Sans",Font.PLAIN,14));
        setFont(new javax.swing.plaf.FontUIResource("Noto Sans",Font.PLAIN,14));
        setMinimumSize(new Dimension(1280,720));
        userListPanel.setMinimumSize(new Dimension(getWidth()/10, getHeight()));
        userListPanel.setPreferredSize(new Dimension(getWidth()/10, getHeight()));
        splitLoginPane.setDividerLocation(loginPanel.getWidth()/2);
        setTitle("ChatBox");
        setContentPane(mainPanel);

    }
    private void initialize() {
        setJFrameDesign();

        setVisible(true);

        LoginButton.addActionListener(e -> {
            try {
                clientConnection = ClientConnection.createConnection(this);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        chatInputTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        clientConnection.sendMessage(chatInputTextField.getText());
                        chatInputTextField.setText("");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                super.keyPressed(e);
            }
        });

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
                System.exit(0);
                super.windowClosed(e);

            }
        });
    }

    public JTextArea getTextAreaChatHistory() {
        return textAreaChatHistory;
    }

    @Override
    public void run() {

            initialize();

    }


}
