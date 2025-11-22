package server;

import javax.swing.*;
import java.awt.*;

public class ServerWindow  extends JFrame {
    private JTextArea t_display;

    public ServerWindow() {
        super("Server");

        buildGUI();
        setSize(400, 1000);
        setLocation(1000, 0);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildGUI() {
        add(DisplayPanel(), BorderLayout.CENTER);
    }
    private JPanel DisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        t_display = new JTextArea();
        t_display.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(t_display);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}
