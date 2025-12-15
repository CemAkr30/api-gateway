package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tr.gov.gib.ebyn.api.gateway.core.services.ILogService;

class PreLoggingGatewayFilterFactoryTest {

  private PreLoggingGatewayFilterFactory filterFactory;
  private ILogService logService;

  @BeforeEach
  void setUp() {
    logService = mock(ILogService.class);
    filterFactory = new PreLoggingGatewayFilterFactory(logService);
  }

  @Test
  void shouldNotFilter_whenDisabled() {
    var config = new PreLoggingGatewayFilterFactory.Config();
    config.setEnabled(false);

    var filter = filterFactory.apply(config);

    var request = MockServerHttpRequest.get("/test").build();
    var exchange = MockServerWebExchange.from(request);
    var chain = mock(GatewayFilterChain.class);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain))
        .verifyComplete();

    verify(chain, times(1)).filter(exchange);
    verifyNoInteractions(logService);
  }

  @Test
  void shouldLogRequest_whenEnabled() {
    var config = new PreLoggingGatewayFilterFactory.Config();
    config.setEnabled(true);

    var filter = filterFactory.apply(config);

    var request = MockServerHttpRequest.get("/test").build();
    var exchange = MockServerWebExchange.from(request);
    var chain = mock(GatewayFilterChain.class);

    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain))
        .verifyComplete();

    verify(logService, atLeastOnce()).logRequest(any(), any(), any(), any());
  }

  @Test
  void shouldLogError_whenExceptionThrown() {
    var config = new PreLoggingGatewayFilterFactory.Config();
    config.setEnabled(true);

    var filter = filterFactory.apply(config);

    var request = MockServerHttpRequest.get("/error").build();
    var exchange = MockServerWebExchange.from(request);
    var chain = mock(GatewayFilterChain.class);

    when(chain.filter(exchange)).thenReturn(Mono.error(new RuntimeException("Simulated error")));

    StepVerifier.create(filter.filter(exchange, chain))
        .expectError(RuntimeException.class)
        .verify();

    verify(logService, atLeastOnce()).logError(any(), any(), any(), any(), any());
  }
}
