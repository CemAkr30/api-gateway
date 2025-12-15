package tr.gov.gib.ebyn.api.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static tr.gov.gib.ebyn.api.gateway.constants.AuthConstant.X_API_KEY;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tr.gov.gib.ebyn.api.gateway.setup.MockServerSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"TEST"})
class GatewayDynamicRouteTest
        extends MockServerSetup {

    @Autowired private WebTestClient webTestClient;
    @Autowired private RouteLocator routeLocator;

    @BeforeEach
    public void setUp() {
        startMockServer();
        setupMockResponses();
    }


    private void setupMockResponses() {
        setupMockServer("/test",
                HttpStatusCode.OK_200.code(),
                HttpMethod.GET.name(),
                securityProperty.getKey(),
                null
        );
        setupMockServer("/test",
                HttpStatusCode.CREATED_201.code(),
                HttpMethod.POST.name(),
                securityProperty.getKey(),
                "{\"message\":\"Test\"}"
                );
    }

    @AfterEach
    public void tearDown() {
        stopMockServer();
    }

    @Test
    void testGetServiceSuccess() {
        webTestClient.get()
                .uri("/test")
                .header(X_API_KEY, securityProperty.getKey())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testPostServiceSuccess() {
        webTestClient.post()
                .uri("/test")
                .bodyValue("{\"message\":\"Test\"}")
                .header(X_API_KEY, securityProperty.getKey())
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testClientError() {
        webTestClient.get()
                .uri("/error-endpoint")
                .header(X_API_KEY, securityProperty.getKey())
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void testServiceForbiddance() {
        webTestClient.get()
                .uri("/test")
                .header(X_API_KEY, "")
                .exchange()
                .expectStatus().isEqualTo(401);
    }

    @Test
    void testRoutes() {
        Flux<Route> routes = routeLocator.getRoutes();
        List<Route> routeList = routes.collectList().block();
        assertThat(routeList).isNotEmpty();
    }

}
