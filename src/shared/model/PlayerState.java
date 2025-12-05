// src/shared/model/PlayerState.java
package shared.model;

import java.io.Serializable;
import java.util.Objects;

public class PlayerState implements Serializable {

    private final int playerId;
    private final String nickname;
    private String keyRole = "";

    public PlayerState(String nickname, int playerId) {
        this.nickname = nickname;
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }


    public String getKeyRole() {
        return keyRole;
    }

    public void setKeyRole(String keyRole) {
        this.keyRole = keyRole;
    }
}
