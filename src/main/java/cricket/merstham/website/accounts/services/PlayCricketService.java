package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.model.PlayCricketMatchDetailResponse;
import cricket.merstham.website.accounts.model.PlayCricketMatchSummaryResponse;
import cricket.merstham.website.accounts.model.PlayCricketPlayer;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PlayCricketService {
    private static final Logger LOG = LoggerFactory.getLogger(PlayCricketService.class);

    private static final String BASE_URL = "https://play-cricket.com/api/v2/";
    private static final String HOME_TEAM = "home_team";
    private static final String AWAY_TEAM = "away_team";

    private final ConfigurationService configurationService;

    public PlayCricketService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public List<PlayCricketMatch> getMatches(
            LocalDate startDate, LocalDate endDate, List<String> teams) {
        Client client = JerseyClientBuilder.createClient();

        WebTarget target = client.target(URI.create(BASE_URL));
        LOG.info("Getting matches between {} and {} from PlayCricket", startDate, endDate);

        return target
                .queryParam("site_id", configurationService.getPlayCricketSiteId())
                .queryParam("api_token", configurationService.getPlayCricketApiToken())
                .queryParam("season", LocalDate.now().getYear())
                .path("matches.json")
                .request()
                .get(PlayCricketMatchSummaryResponse.class)
                .getMatches()
                .stream()
                .filter(
                        m ->
                                !(m.getMatchDate().isBefore(startDate)
                                        || m.getMatchDate().isAfter(endDate)))
                .filter(m -> teams.contains(getOurTeamName(m)))
                .collect(Collectors.toList());
    }

    public PlayCricketMatch getMatchDetail(int matchId) {
        Client client = JerseyClientBuilder.createClient();

        WebTarget target = client.target(URI.create(BASE_URL));

        Response response =
                target.queryParam("match_id", matchId)
                        .queryParam("api_token", configurationService.getPlayCricketApiToken())
                        .path("match_detail.json")
                        .request()
                        .get();
        LOG.info("Match details response status = {}", response.getStatus());
        return response.readEntity(PlayCricketMatchDetailResponse.class).getMatchDetails().get(0);
    }

    public List<PlayCricketPlayer> getOurPlayers(int matchId) {
        PlayCricketMatch detail = getMatchDetail(matchId);

        if (detail.getHomeClubId() == configurationService.getPlayCricketSiteId()) {
            return detail.getPlayers().stream()
                    .filter(m -> m.containsKey(HOME_TEAM))
                    .findFirst()
                    .orElseThrow()
                    .get(HOME_TEAM);
        }
        return detail.getPlayers().stream()
                .filter(m -> m.containsKey(AWAY_TEAM))
                .findFirst()
                .orElseThrow()
                .get(AWAY_TEAM);
    }

    public String getOurTeamName(PlayCricketMatch match) {
        if (match.getHomeClubId() == configurationService.getPlayCricketSiteId()) {
            return match.getHomeTeamName();
        }
        return match.getAwayTeamName();
    }
}
