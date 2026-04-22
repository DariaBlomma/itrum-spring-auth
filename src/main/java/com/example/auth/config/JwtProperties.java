package com.example.auth.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "jwt")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
    private String secret;

    @Builder.Default
    private Duration expiration = Duration.ofHours(24);

    public long getExpiration() {
        return expiration.toMillis();
    }
}
