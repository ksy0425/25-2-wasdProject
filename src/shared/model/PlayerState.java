// src/shared/model/PlayerState.java
package shared.model;

import java.io.Serializable;
import java.util.Objects;

public class PlayerState implements Serializable {

    private final int playerId;
    private final String nickname;

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

//    @Override
//    public String toString() {
//        return "PlayerState{" +
//                "playerId=" + playerId +
//                ", nickname='" + nickname + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof PlayerState)) return false;
//        PlayerState that = (PlayerState) o;
//        return playerId == that.playerId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(playerId);
//    }
}
