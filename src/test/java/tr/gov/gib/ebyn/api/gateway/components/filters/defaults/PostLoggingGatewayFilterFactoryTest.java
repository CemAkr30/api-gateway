package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tr.gov.gib.ebyn.api.gateway.core.services.ILogService;

class PostLoggingGatewayFilterFactoryTest {

  private ILogService logService;
  private PostLoggingGatewayFilterFactory factory;
  private GatewayFilterChain chain;

  @BeforeEach
  void setup() {
    logService = mock(ILogService.class);
    factory = new PostLoggingGatewayFilterFactory(logService);
    chain = mock(GatewayFilterChain.class);
  }

  @Test
  void shouldLogRequestWhenEnabledTrue() {
    var config = new PostLoggingGatewayFilterFactory.Config();
    config.setEnabled(true);

    var request = MockServerHttpRequest.get("/test").build();
    var exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    var filter = factory.apply(config);
    StepVerifier.create(filter.filter(exchange, chain))
        .verifyComplete();

    verify(logService, atLeastOnce()).logRequest(
        anyString(), anyString(), anyString(), contains("/test")
    );
  }

  @Test
  void shouldSkipFilterWhenDisabled() {
    var config = new PostLoggingGatewayFilterFactory.Config();
    config.setEnabled(false);

    var request = MockServerHttpRequest.get("/test-disabled").build();
    var exchange = MockServerWebExchange.from(request);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    var filter = factory.apply(config);
    StepVerifier.create(filter.filter(exchange, chain))
        .verifyComplete();

    // logService should never be called when disabled
    verify(logService, never()).logRequest(any(), any(), any(), any());
    verify(logService, never()).logError(any(), any(), any(), any(), any());
  }

  @Test
  void shouldLogErrorWhenExceptionOccurs() {
    var config = new PostLoggingGatewayFilterFactory.Config();
    config.setEnabled(true);

    var request = MockServerHttpRequest.get("/test-error").build();
    var exchange = MockServerWebExchange.from(request);

    RuntimeException error = new RuntimeException("Simulated error");
    when(chain.filter(exchange)).thenReturn(Mono.error(error));

    var filter = factory.apply(config);
    StepVerifier.create(filter.filter(exchange, chain))
        .expectError(RuntimeException.class)
        .verify();

    verify(logService).logError(
        eq("Error occurred during request processing"),
        contains("PostLoggingGatewayFilterFactory"),
        eq("filter"),
        eq("Error details"),
        eq(error)
    );
  }
}
