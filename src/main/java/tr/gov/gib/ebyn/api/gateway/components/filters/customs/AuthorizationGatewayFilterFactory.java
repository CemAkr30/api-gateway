package tr.gov.gib.ebyn.api.gateway.components.filters.customs;


import static tr.gov.gib.ebyn.api.gateway.constants.AuthConstant.TOKEN;
import static tr.gov.gib.ebyn.api.gateway.constants.AuthConstant.X_API_KEY;

import java.util.List;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tr.gov.gib.ebyn.api.gateway.components.context.UserContextHolder;
import tr.gov.gib.ebyn.api.gateway.core.services.IAuthService;
import tr.gov.gib.ebyn.api.gateway.enums.filters.OrderEnum;
import tr.gov.gib.ebyn.api.gateway.exceptions.AuthorizationException;
import tr.gov.gib.ebyn.api.gateway.utils.HeaderUtil;
import tr.gov.gib.ebyn.api.gateway.utils.SecurityUtil;

@Component
@Log4j2
public class AuthorizationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {


    private final IAuthService authService;
    private final SecurityUtil securityUtil;
    private final HeaderUtil headerUtil;

    public AuthorizationGatewayFilterFactory(
        IAuthService authService,
        SecurityUtil securityUtil,
        HeaderUtil headerUtil
    ) {
        super(Config.class);
        this.authService = authService;
        this.securityUtil = securityUtil;
        this.headerUtil = headerUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            log.info("AuthorizationGatewayFilterFactory started.");

            if (config.isEnabled()) {
                var originalRequest = exchange.getRequest();
                var headers = originalRequest.getHeaders();
                var apiKey = headers.getFirst(X_API_KEY);
                var token = headers.getFirst(TOKEN);

                if (securityUtil.webSecurityCustomizer(originalRequest.getURI().getRawPath()) ||
                    authService.isAuthorized(apiKey, token)) {

                    var userInfo = UserContextHolder.getContext().getUserInfo();
                    if (userInfo != null) {
                        var modifiedHeaders = exchange.getRequest().mutate();
                        var requestHeadersMap = headerUtil.convertUserInfoToRequestHeadersInCase(userInfo);
                        requestHeadersMap.forEach(modifiedHeaders::header);
                        var modifiedRequest = modifiedHeaders.build();
                        exchange = exchange.mutate().request(modifiedRequest).build();

                        var responseHeadersMap = headerUtil.convertUserInfoToResponseHeadersInCase(userInfo);
                        exchange.getResponse().getHeaders().addAll(responseHeadersMap);

                        UserContextHolder.clear();
                    }

                    return chain.filter(exchange);
                }

                throw new AuthorizationException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access");
            }

            return chain.filter(exchange);
        }, OrderEnum.AUTHORIZATION_FILTER.ordinal());
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("enabled");
    }

    @Data
    static class Config {
        private boolean enabled;
    }
}
