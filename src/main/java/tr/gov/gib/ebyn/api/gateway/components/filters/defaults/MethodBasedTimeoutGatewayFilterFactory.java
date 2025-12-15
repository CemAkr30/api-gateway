package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static tr.gov.gib.ebyn.api.gateway.constants.HeaderConstant.X_TRACE_ID;

import java.time.Duration;
import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import tr.gov.gib.ebyn.api.gateway.components.generators.RandomTraceIdGenerator;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.HttpMethodTimeoutProperty;
import tr.gov.gib.ebyn.api.gateway.enums.filters.OrderEnum;

@Component
@Log4j2
public class MethodBasedTimeoutGatewayFilterFactory
        extends AbstractGatewayFilterFactory<MethodBasedTimeoutGatewayFilterFactory.Config> {

    private final HttpMethodTimeoutProperty property;


    public MethodBasedTimeoutGatewayFilterFactory(HttpMethodTimeoutProperty property) {
        super(Config.class);
        this.property = property;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            HttpMethod method = exchange.getRequest().getMethod();
            Duration timeout = property.getTimeoutForMethod(method);

            return chain.filter(exchange)
                .timeout(timeout);
        }, OrderEnum.TRACE_FILTER.ordinal());
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of();
    }

    @Data
    public static class Config {}
}
