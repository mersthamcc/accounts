package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayCricketMatchSummaryResponse {

    @JsonProperty("matches")
    private List<PlayCricketMatch> matches;

    public List<PlayCricketMatch> getMatches() {
        return matches;
    }
}
