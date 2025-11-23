package client.Screen;

import javax.swing.*;
import java.awt.*;

public class HostScreen extends JPanel{
    private ClientWindow window;
    private JButton b_back;

    public HostScreen(ClientWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setOpaque(false);

        BackgroundPanel bgPanel = new BackgroundPanel("/Main_Background.png");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        buildGUI(bgPanel);
    }

    private void buildGUI(JPanel bgPanel) {

    }
}
