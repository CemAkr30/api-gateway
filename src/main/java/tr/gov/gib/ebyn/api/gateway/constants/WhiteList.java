package tr.gov.gib.ebyn.api.gateway.constants;

public  class WhiteList {

    // Make this member "protected"
    public static final String[] AUTH_WHITELIST = {
        // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",

        // -- Swagger UI v3 (OpenAPI)
        "/api-docs/**",
        "/v3/api-docs/**",
        "/api-docs",
        "/swagger-ui/**",

        // other public endpoints of your API may be appended to this array

        // -- Actuator
        "/actuator/**",
        "/actuator/health"
    };
}
