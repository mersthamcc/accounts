package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    @JsonProperty private String id;

    @JsonProperty private String message;

    @JsonProperty private int code;

    public String getId() {
        return id;
    }

    public ApiResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ApiResponse setCode(int code) {
        this.code = code;
        return this;
    }
}
