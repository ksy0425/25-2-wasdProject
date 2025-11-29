package client.Screen.util;

import javax.swing.*;
import java.awt.*;

public class SpacerPanel extends JPanel {
    public SpacerPanel(int width, int height) {
        setOpaque(false);
        setPreferredSize(new Dimension(width, height));
    }
}
