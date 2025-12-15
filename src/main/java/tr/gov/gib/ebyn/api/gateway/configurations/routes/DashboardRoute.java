package tr.gov.gib.ebyn.api.gateway.configurations.routes;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DashboardRoute {

  @Bean
  public RouterFunction<ServerResponse> dashboardRouter() {
    return RouterFunctions.resources("/dashboard/**",
            new ClassPathResource("static/"))
        .andRoute(GET("/dashboard"),
            request -> ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .bodyValue(new ClassPathResource("static/dashboard/index.html")));
  }
}
