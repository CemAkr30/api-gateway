package tr.gov.gib.ebyn.api.gateway.configurations.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@ConfigurationProperties(prefix = "custom.http-method-timeouts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpMethodTimeoutProperty {
  private Map<String, Duration> timeouts = new HashMap<>();

  public Duration getTimeoutForMethod(HttpMethod method) {
    return timeouts.getOrDefault(method.name(), Duration.ofSeconds(30));
  }
}
