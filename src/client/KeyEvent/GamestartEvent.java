package client.KeyEvent;

import client.network.ClientSender;
import client.network.ConnectionManager;
import server.ClientHandler;
import shared.model.PlayerState;
import shared.packet.GameStartRequestPacket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GamestartEvent implements ActionListener {
    private String title;
    private List<PlayerState> playerStateList;
    public GamestartEvent(String title, Collection<PlayerState> players) {
        this.title = title;
        playerStateList = new ArrayList<>(players) ;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClientSender.send(new GameStartRequestPacket(title, playerStateList));
    }
}
