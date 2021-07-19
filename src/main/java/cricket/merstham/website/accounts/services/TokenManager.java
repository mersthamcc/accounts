package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.model.TokenStore;

import java.time.LocalDateTime;

public class TokenManager {
    private final DynamoService dynamoService;

    public TokenManager(DynamoService dynamoService) {
        this.dynamoService = dynamoService;
    }

    public TokenStore getTokenStore() {
        return dynamoService.getToken();
    }

    public boolean isAccessTokenExpired() {
        return LocalDateTime.now()
                .isAfter(dynamoService.getToken().getAccessTokenExpiry().minusMinutes(1));
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
        return dynamoService.putToken(tokenStore);
    }
}
