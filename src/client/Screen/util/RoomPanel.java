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
        setLayout(new GridLayout(2, 2, 20, 20)); // 2x2 + 간격
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

//        for (String name : participants) {
//            add(createParticipantCard(name, role));
//        }
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
//        JPanel card = new JPanel();
//        card.setOpaque(false);
//        card.setLayout(new BorderLayout());
//
//        RoundedPanel box = new RoundedPanel(20);
//        box.setBackground(new Color(255, 255, 255, 180)); // 반투명 흰 박스
//        box.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
//
//        JLabel label = new JLabel(name);
//        label.setFont(new Font("Dialog", Font.BOLD, 18));
//        box.add(label);
//
//        card.add(box, BorderLayout.CENTER);
//        return card;

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BorderLayout());

        // 바깥 둥근 박스 패널
        RoundedPanel box = new RoundedPanel(20);
        box.setBackground(new Color(255, 255, 255, 180)); // 반투명 흰 박스
        box.setLayout(new BorderLayout()); // 세로로 위/아래 나누기 (닉네임 / 정사각형)

        // ----- 닉네임 영역 (위쪽) -----
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        namePanel.setOpaque(false);

        JLabel label = new JLabel(name);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        namePanel.add(label);

        // ----- 정사각형 영역 (아래쪽) -----
        JPanel squarePanelWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        squarePanelWrapper.setOpaque(false);

        JPanel squarePanel = new JPanel();
        int size = 100; // 정사각형 한 변 길이
        squarePanel.setPreferredSize(new Dimension(size, size));
        squarePanel.setBackground(new Color(220, 220, 220)); // 기본 색
        squarePanelWrapper.add(squarePanel);

        JLabel roleLabel = new JLabel(role);
        squarePanel.add(roleLabel);

        // 박스에 위/아래로 배치
        box.add(namePanel, BorderLayout.NORTH);
        box.add(squarePanelWrapper, BorderLayout.CENTER);

        card.add(box, BorderLayout.CENTER);
        return card;
    }

//    public void setRole(String role) {
//        this.role = role;
//    }
}
