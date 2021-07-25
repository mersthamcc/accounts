package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayCricketMatch {

    @JsonProperty("id")
    private int id;

    @JsonProperty("match_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate matchDate;

    @JsonProperty("competition_type")
    private String competitionType;

    @JsonProperty("home_club_name")
    private String homeClubName;

    @JsonProperty("home_team_name")
    private String homeTeamName;

    @JsonProperty("home_team_id")
    private int homeTeamId;

    @JsonProperty("home_club_id")
    private int homeClubId;

    @JsonProperty("away_club_name")
    private String awayClubName;

    @JsonProperty("away_team_name")
    private String awayTeamName;

    @JsonProperty("away_team_id")
    private int awayTeamId;

    @JsonProperty("away_club_id")
    private int awayClubId;

    @JsonProperty("players")
    private List<Map<String, List<PlayCricketPlayer>>> players;

    public int getId() {
        return id;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public String getHomeClubName() {
        return homeClubName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public int getHomeClubId() {
        return homeClubId;
    }

    public String getAwayClubName() {
        return awayClubName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public int getAwayClubId() {
        return awayClubId;
    }

    public List<Map<String, List<PlayCricketPlayer>>> getPlayers() {
        return players;
    }

    //    @JsonIgnoreProperties(ignoreUnknown = true)
    //    private class PlayerObject {
    //
    //        @JsonProperty("home_team")
    //        private List<PlayCricketPlayer> homeTeam;
    //
    //        @JsonProperty("away_team")
    //        private List<PlayCricketPlayer> awayTeam;
    //
    //        public boolean isHomeTeam() {
    //            return (homeTeam != null && homeTeam.size() > 0);
    //        }
    //
    //        public List<PlayCricketPlayer> getPlayers() {
    //            return isHomeTeam() ? homeTeam : awayTeam;
    //        }
    //    }
}
