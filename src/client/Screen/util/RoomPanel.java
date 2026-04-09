package client.Screen.util;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class RoomPanel extends JPanel {
    public final int MAX_PLAYERS = 4;
    private final Vector<String> participants = new Vector<>();
    private final Vector<String> roles = new Vector<>();

    public RoomPanel() {
        setOpaque(false);
        setLayout(new GridLayout(2, 2, 20, 20));
    }

    public void addParticipant(String name, String role) {
        if (participants.size() >= MAX_PLAYERS && roles.size() >= MAX_PLAYERS) return;

        participants.add(name);
        roles.add(role);
        refreshGrid();
    }

    public void removeParticipant(String name) {
        participants.remove(name);
        refreshGrid();
    }

    public void removeRoles(String role) {
        roles.remove(role);
        refreshGrid();
    }

    public void clearParticipants() {
        participants.clear();
        refreshGrid();
    }

    public void clearRoles() {
        roles.clear();
        refreshGrid();
    }

    private void refreshGrid() {
        removeAll();

        for (int i = 0; i < participants.size(); i++) {
            String name = participants.get(i);
            String role = (i < roles.size()) ? roles.get(i) : "";
            add(createParticipantCard(name, role));
        }

        int emptySlots = MAX_PLAYERS - participants.size();
        for (int i = 0; i < emptySlots; i++) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            add(empty);
        }

        revalidate();
        repaint();
    }

    private JPanel createParticipantCard(String name, String role) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        RoundedPanel box = new RoundedPanel(20);
        box.setBackground(new Color(255, 255, 255, 180));
        box.setLayout(new BorderLayout());

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        namePanel.setOpaque(false);

        JLabel label = new JLabel(name);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        namePanel.add(label);

        JPanel squarePanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        squarePanelWrapper.setOpaque(false);

        int size = 100;

        JPanel squarePanel = new JPanel(new BorderLayout());
        squarePanel.setPreferredSize(new Dimension(size, size));
        squarePanel.setBackground(new Color(220, 220, 220));

        String imgPath = getRoleImagePath(role);
        if (imgPath != null) {
            ImagePanel roleImagePanel = new ImagePanel(imgPath, size, size);
            squarePanel.add(roleImagePanel, BorderLayout.CENTER);
        }

        squarePanelWrapper.add(squarePanel);

        box.add(namePanel, BorderLayout.NORTH);
        box.add(squarePanelWrapper, BorderLayout.CENTER);

        card.add(box, BorderLayout.CENTER);
        return card;
    }

    private String getRoleImagePath(String role) {
        if (role == null) return null;

        switch (role.toLowerCase()) {
            case "w":
                return "/W.png";
            case "a":
                return "/A.png";
            case "s":
                return "/S.png";
            case "d":
                return "/D.png";
            default:
                return null;
        }
    }
}
