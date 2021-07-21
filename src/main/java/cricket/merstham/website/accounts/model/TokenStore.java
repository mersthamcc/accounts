package cricket.merstham.website.accounts.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import cricket.merstham.website.accounts.helpers.LocalDateTimeConverter;

import java.time.LocalDateTime;

@DynamoDBDocument
@DynamoDBTable(tableName = "token")
public class TokenStore {

    private String id;
    private String state;
    private String accessToken;
    private LocalDateTime accessTokenExpiry;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public TokenStore setId(String id) {
        this.id = id;
        return this;
    }

    @DynamoDBAttribute(attributeName = "state")
    public String getState() {
        return state;
    }

    public TokenStore setState(String state) {
        this.state = state;
        return this;
    }

    @DynamoDBAttribute(attributeName = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public TokenStore setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    @DynamoDBAttribute(attributeName = "expiry")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getAccessTokenExpiry() {
        return accessTokenExpiry;
    }

    public TokenStore setAccessTokenExpiry(LocalDateTime accessTokenExpiry) {
        this.accessTokenExpiry = accessTokenExpiry;
        return this;
    }

    @DynamoDBAttribute(attributeName = "refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    public TokenStore setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @DynamoDBAttribute(attributeName = "refresh_token_expiry")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public TokenStore setRefreshTokenExpiry(LocalDateTime refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
        return this;
    }
}
