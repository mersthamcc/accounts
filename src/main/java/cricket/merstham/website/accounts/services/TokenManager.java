package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.model.TokenStore;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class TokenManager {
    private final DynamoService dynamoService;
    private TokenStore cached;

    public TokenManager(DynamoService dynamoService) {
        this.dynamoService = dynamoService;
    }

    public TokenStore getTokenStore() {
        if (isNull(cached)) {
            cached = dynamoService.getToken();
        }
        return cached;
    }

    public boolean isAccessTokenExpired() {
        if (isNull(cached)) getTokenStore();
        return LocalDateTime.now().isAfter(cached.getAccessTokenExpiry().minusMinutes(1));
    }

    public TokenStore update(
            String accessToken,
            LocalDateTime accessTokenExpiry,
            String refreshToken,
            LocalDateTime refreshTokenExpiry) {
        var tokenStore =
                dynamoService
                        .getToken()
                        .setAccessToken(accessToken)
                        .setAccessTokenExpiry(accessTokenExpiry)
                        .setRefreshToken(refreshToken)
                        .setRefreshTokenExpiry(refreshTokenExpiry);
        cached = dynamoService.putToken(tokenStore);
        return cached;
    }
}
