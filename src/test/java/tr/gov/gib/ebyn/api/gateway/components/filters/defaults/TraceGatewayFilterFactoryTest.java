package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tr.gov.gib.ebyn.api.gateway.constants.HeaderConstant.X_TRACE_ID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tr.gov.gib.ebyn.api.gateway.components.generators.RandomTraceIdGenerator;

class TraceGatewayFilterFactoryTest {

  private TraceGatewayFilterFactory filterFactory;
  private RandomTraceIdGenerator randomTraceIdGenerator;

  @BeforeEach
  void setUp() {
    randomTraceIdGenerator = mock(RandomTraceIdGenerator.class);
    filterFactory = new TraceGatewayFilterFactory(randomTraceIdGenerator);
  }

  @Test
  void shouldAddTraceIdToRequestAndResponseHeaders() {
    // given
    var traceId = "test-trace-id-123";
    when(randomTraceIdGenerator.generate()).thenReturn(traceId);

    var request = MockServerHttpRequest.get("/api/example").build();
    var exchange = MockServerWebExchange.from(request);

    var chain = mock(GatewayFilterChain.class);
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    var config = new TraceGatewayFilterFactory.Config();
    var filter = filterFactory.apply(config);

    // when + then
    StepVerifier.create(filter.filter(exchange, chain))
        .verifyComplete();

    // assertions
    var responseHeaders = exchange.getResponse().getHeaders();
    assertThat(responseHeaders.getFirst(X_TRACE_ID)).isEqualTo(traceId);
    assertThat(exchange.getRequest().getHeaders().getFirst(X_TRACE_ID)).isEqualTo(traceId);

    verify(randomTraceIdGenerator, times(1)).generate();
    verify(chain, times(1)).filter(exchange);
  }
}
