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
    // 참가자 추가
    public void addParticipant(String name) {
        if (participants.size() >= MAX_PLAYERS) return;

        participants.add(name);
        refreshGrid();
    }

    // 참가자 제거
    public void removeParticipant(String name) {
        participants.remove(name);
        refreshGrid();
    }

    // 전체 초기화
    public void clearParticipants() {
        participants.clear();
        refreshGrid();
    }

    // 실제 UI 갱신
    private void refreshGrid() {
        removeAll();

        for (String name : participants) {
            add(createParticipantCard(name));
        }

        // 빈 칸은 빈 JPanel로 채우기 (2x2 유지)
        int emptySlots = MAX_PLAYERS - participants.size();
        for (int i = 0; i < emptySlots; i++) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            add(empty);
        }

        revalidate();
        repaint();
    }
    // 참가자 하나를 표현하는 카드
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
