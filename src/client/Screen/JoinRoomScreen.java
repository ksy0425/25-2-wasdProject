package client.Screen;

import client.KeyEvent.InputRoomEvent;
import client.KeyEvent.JoinRoomEvent;
import client.Screen.util.BackgroundPanel;
import client.Screen.util.RoundedPanel;
import client.Screen.util.SpacerPanel;
import client.network.ClientSender;
import client.network.ConnectionManager;
import shared.packet.RoomListRequestPacket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

public class JoinRoomScreen extends JPanel {
    private ClientWindow window;
    private JTextField t_roomTitle;

    // 추가 구현
    private Vector<String> roomVector = new Vector<>();
    private JList<String> roomList = new JList<>(roomVector);

    public JoinRoomScreen(ClientWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setOpaque(false);

        BackgroundPanel bgPanel = new BackgroundPanel("/Main_Background.png");
        bgPanel.setLayout(new BorderLayout());
        add(bgPanel, BorderLayout.CENTER);

        buildGUI(bgPanel);
    }

    private void buildGUI(BackgroundPanel bgPanel) {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel verticalPanel = new JPanel(new BorderLayout());
        verticalPanel.setOpaque(false);
        centerPanel.add(verticalPanel);

        verticalPanel.add(createLabelPanel(), BorderLayout.NORTH);
        verticalPanel.add(createInputPanel(), BorderLayout.CENTER);
        verticalPanel.add(createSouthButtonSection(), BorderLayout.SOUTH);
    }

    private JPanel createLabelPanel() {
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);

        labelPanel.add(new SpacerPanel(1, 60), BorderLayout.NORTH);

        JLabel label = new JLabel("참가하기");
        label.setFont(new Font("Dialog", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        labelPanel.add(label, BorderLayout.CENTER);

        JButton b_refresh = new JButton("새로고침");
        b_refresh.setFont(new Font("Dialog", Font.BOLD, 40));
        b_refresh.setBackground(Color.GREEN);
        labelPanel.add(b_refresh, BorderLayout.EAST);
        b_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshRoomList();
            }
        });

        labelPanel.add(new SpacerPanel(1, 50), BorderLayout.SOUTH);

        return labelPanel;
    }

    private JPanel createInputPanel() { // 외부 참조
        RoundedPanel containerPanel = new RoundedPanel(30);
        containerPanel.setBackground(new Color(255, 255, 255, 220));
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setPreferredSize(new Dimension(1000, 500));

        JPanel flowTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flowTitlePanel.setOpaque(false);
        flowTitlePanel.add(new SpacerPanel(900,10));

        RoundedPanel titlePanel = new RoundedPanel(30);
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(180, 140, 100));
        titlePanel.setPreferredSize(new Dimension(800, 70));

        t_roomTitle = new JTextField("참가할 방 제목을 입력하세요.", 22);
        t_roomTitle.setOpaque(false);
        t_roomTitle.setFont(new Font("Dialog", Font.BOLD, 40));
        t_roomTitle.setForeground(new Color(245, 245, 220));
        t_roomTitle.setBorder(new EmptyBorder(3,10,0,0));
        t_roomTitle.addMouseListener(new InputRoomEvent(t_roomTitle));
        t_roomTitle.addActionListener(new JoinRoomEvent(t_roomTitle, window));

        titlePanel.add(t_roomTitle);
        flowTitlePanel.add(titlePanel);
        flowTitlePanel.add(createRoomListPanel());

        JPanel flowCreatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flowCreatePanel.setOpaque(false);

        JButton b_join = new JButton("참가하기");
        b_join.setFont(new Font("Dialog", Font.BOLD, 40));
        b_join.setBackground(Color.GREEN);
        b_join.addActionListener(new JoinRoomEvent(t_roomTitle, window));

        flowCreatePanel.add(b_join);

        containerPanel.add(flowTitlePanel, BorderLayout.CENTER);
        containerPanel.add(flowCreatePanel, BorderLayout.SOUTH);

        return containerPanel;
    }

    // 추가 구현
    public JPanel createRoomListPanel() {
        RoundedPanel listPanel = new RoundedPanel(30);
        listPanel.setBackground((new Color(255, 255, 255, 220)));
        listPanel.setLayout(new BorderLayout());
        listPanel.setPreferredSize(new Dimension(1000, 330));

        roomList.setVisibleRowCount(12);
        roomList.setFixedCellHeight(36);
        roomList.setFont(new Font("Malgun Gothic", Font.PLAIN, 25));
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        listPanel.add(scroll, BorderLayout.CENTER);

        roomList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = roomList.getSelectedValue();
                    if (selected != null) {
                        String titleOnly = selected.split("\\(")[0].trim();
                        t_roomTitle.setText(titleOnly);
                    }
                }
            }
        });

        return listPanel;
    }
    public void refreshRoomList() {
        updateRoomList(ConnectionManager.getHandler().getRoomList());
        ClientSender.send(new RoomListRequestPacket());
    }
    public void updateRoomList(Map<String, Integer> rooms) {
        roomVector.clear();
        if (rooms == null || rooms.isEmpty()) {
            roomVector.addElement("(현재 생성된 방이 없습니다)");
            return;
        }
        for (Map.Entry<String, Integer> entry : rooms.entrySet()) {
            roomVector.add(entry.getKey() + "                                   (" + entry.getValue() + "/4)");
        }
        roomList.setListData(roomVector);
    }


    private JPanel createSouthButtonSection() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(new SpacerPanel(1, 5), BorderLayout.NORTH);

        JPanel ExitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ExitPanel.setOpaque(false);
        JButton exitButton = new JButton("   나가기   ");
        exitButton.setFont(new Font("Dialog", Font.BOLD, 40));
        exitButton.setBackground(Color.GREEN);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.showScreen("main");
            }
        });
        ExitPanel.add(exitButton);

        southPanel.add(ExitPanel, BorderLayout.SOUTH);

        return southPanel;
    }
}
