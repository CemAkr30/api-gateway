package tr.gov.gib.ebyn.api.gateway.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tr.gov.gib.ebyn.api.gateway.model.BasicAuthDeclaration;

@Configuration
@ConfigurationProperties(prefix = "security-ws-validation.service")
@Getter
@Setter
public class ValidationServiceProperty {
  private String url;
  private String checkTokenServiceEndpoint;
  private String userInfoByTokenServiceEndpoint;
  private BasicAuthDeclaration basicAuth;
}
