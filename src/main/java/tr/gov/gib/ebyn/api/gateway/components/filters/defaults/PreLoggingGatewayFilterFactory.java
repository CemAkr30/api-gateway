package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.stereotype.Component;
import tr.gov.gib.ebyn.api.gateway.components.filters.defaults.common.AbstractLoggingGatewayFilterFactory;
import tr.gov.gib.ebyn.api.gateway.core.services.ILogService;
import tr.gov.gib.ebyn.api.gateway.enums.filters.OrderEnum;

@Component
@Log4j2
public class PreLoggingGatewayFilterFactory
    extends AbstractLoggingGatewayFilterFactory<PreLoggingGatewayFilterFactory.Config> {

    public PreLoggingGatewayFilterFactory(ILogService logService) {
        super(PreLoggingGatewayFilterFactory.Config.class, logService);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            var className = this.getClass().getSimpleName();
            var methodName = "filter";

            return wrapWithLogging(exchange, chain, className, methodName);
        }, OrderEnum.PRE_LOGGING_FILTER.ordinal());
    }


    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("enabled");
    }

    @Getter
    @Setter
    public static class Config {
        private boolean enabled;
    }
}
