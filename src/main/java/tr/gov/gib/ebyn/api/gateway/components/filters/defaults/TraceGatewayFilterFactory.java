package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static tr.gov.gib.ebyn.api.gateway.constants.HeaderConstant.X_TRACE_ID;

import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import tr.gov.gib.ebyn.api.gateway.components.generators.RandomTraceIdGenerator;
import tr.gov.gib.ebyn.api.gateway.enums.filters.OrderEnum;

@Component
@Log4j2
public class TraceGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TraceGatewayFilterFactory.Config> {

    private final RandomTraceIdGenerator randomTraceIdGenerator;

    public TraceGatewayFilterFactory(RandomTraceIdGenerator randomTraceIdGenerator) {
        super(Config.class);
        this.randomTraceIdGenerator = randomTraceIdGenerator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            log.info("TraceGatewayFilterFactory");
            var traceId = randomTraceIdGenerator.generate();
            log.info("traceId: {}", traceId);

            exchange.getResponse().getHeaders().add(X_TRACE_ID, traceId);
            exchange.getRequest().mutate().header(X_TRACE_ID, traceId);

            return chain.filter(exchange);
        }, OrderEnum.TRACE_FILTER.ordinal());
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of();
    }

    @Data
    public static class Config {}
}
