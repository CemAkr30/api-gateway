package tr.gov.gib.ebyn.api.gateway.components.filters.defaults;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import tr.gov.gib.ebyn.api.gateway.enums.filters.OrderEnum;

@Component
@Log4j2
public class StripBasePathGatewayFilterFactory
        extends AbstractGatewayFilterFactory<StripBasePathGatewayFilterFactory.Config> {

    public StripBasePathGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            log.info("StripBasePathGatewayFilterFactory");
            var originalRequest = exchange.getRequest();
            addOriginalRequestUrl(exchange, originalRequest.getURI());


            var modifiedRequest = modifyRequestPath(originalRequest,config.getContextPath());
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, modifiedRequest.getURI());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }, OrderEnum.STRIP_BASE_PATH_FILTER.ordinal());
    }

    private ServerHttpRequest modifyRequestPath(ServerHttpRequest originalRequest,String basePath) {
        var originalPath = originalRequest.getURI().getRawPath();
        var newPath = originalPath.replaceFirst(basePath, "");
        return originalRequest.mutate().path(newPath).contextPath("/").build();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("contextPath");
    }

    @Data
    static class Config {
        private String contextPath;
    }
}