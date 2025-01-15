package reviewme.config.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUserInfoResponse(
        @JsonProperty("login")
        String userName,

        @JsonProperty("email")
        String email
) {
}
