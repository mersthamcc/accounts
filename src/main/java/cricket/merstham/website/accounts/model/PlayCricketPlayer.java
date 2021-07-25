package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayCricketPlayer {

    @JsonProperty("position")
    private int position;

    @JsonProperty("player_name")
    private String playerName;

    @JsonProperty("player_id")
    private int playerId;

    @JsonProperty("captain")
    private boolean captain;

    public int getPosition() {
        return position;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isCaptain() {
        return captain;
    }
}
