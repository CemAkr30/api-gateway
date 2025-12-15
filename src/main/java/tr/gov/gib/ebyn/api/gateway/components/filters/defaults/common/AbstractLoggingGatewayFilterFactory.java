package tr.gov.gib.ebyn.api.gateway.components.filters.defaults.common;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tr.gov.gib.ebyn.api.gateway.core.services.ILogService;

public abstract class AbstractLoggingGatewayFilterFactory<C> extends AbstractGatewayFilterFactory<C> {

  protected final ILogService logService;

  protected AbstractLoggingGatewayFilterFactory(Class<C> configClass, ILogService logService) {
    super(configClass);
    this.logService = logService;
  }

  protected Mono<Void> wrapWithLogging(ServerWebExchange exchange, GatewayFilterChain chain,
      String className, String methodName) {

    logService.logRequest("Processing request", className, methodName,
        "Request URI: " + exchange.getRequest().getURI());

    return chain.filter(exchange)
        .doOnTerminate(() ->
            logService.logRequest("Request completed", className, methodName,
                "Response Status: " + exchange.getResponse().getStatusCode()))
        .doOnError(throwable ->
            logService.logError("Error occurred during request processing",
                className, methodName, "Error details", throwable));
  }
}
