package client.Screen.util;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class RoomPanel extends JPanel {
    private final int MAX_PLAYERS = 4;
    private final Vector<String> participants = new Vector<>();

    public RoomPanel() {
        setOpaque(false);
        setLayout(new GridLayout(2, 2, 20, 20)); // 2x2 + 간격
    }

    public void addParticipant(String name) {
        if (participants.size() >= MAX_PLAYERS) return;

        participants.add(name);
        refreshGrid();
    }

    public void removeParticipant(String name) {
        participants.remove(name);
        refreshGrid();
    }

    public void clearParticipants() {
        participants.clear();
        refreshGrid();
    }

    private void refreshGrid() {
        removeAll();

        for (String name : participants) {
            add(createParticipantCard(name));
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

    private JPanel createParticipantCard(String name) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        RoundedPanel box = new RoundedPanel(20);
        box.setBackground(new Color(255, 255, 255, 180)); // 반투명 흰 박스
        box.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel label = new JLabel(name);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        box.add(label);

        card.add(box, BorderLayout.CENTER);
        return card;
    }
}
