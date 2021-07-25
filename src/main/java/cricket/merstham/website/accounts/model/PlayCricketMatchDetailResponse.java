package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PlayCricketMatchDetailResponse {
    @JsonProperty("match_details")
    private List<PlayCricketMatch> matchDetails;

    public List<PlayCricketMatch> getMatchDetails() {
        return matchDetails;
    }
}
