package tr.gov.gib.ebyn.api.gateway.configurations.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api-security")
@Getter
@Setter
@NoArgsConstructor
public class SecurityProperty {

  private String key;
  private String fixedTokens;
}
