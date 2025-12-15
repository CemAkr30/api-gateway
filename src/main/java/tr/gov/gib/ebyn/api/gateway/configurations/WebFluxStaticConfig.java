package tr.gov.gib.ebyn.api.gateway.configurations;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebFluxStaticConfig implements WebFluxConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/dashboard/**")
        .addResourceLocations("classpath:/static/dashboard/")
        .setCacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)));
  }
}