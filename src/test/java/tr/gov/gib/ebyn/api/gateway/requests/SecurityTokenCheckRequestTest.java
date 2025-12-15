package tr.gov.gib.ebyn.api.gateway.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityTokenCheckRequestTest {

  @Test
  void testNoArgsConstructor() {
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest();
    assertNull(request.getToken());
  }

  @Test
  void testAllArgsConstructor() {
    String testToken = "test-token-123";
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest(testToken);
    assertEquals(testToken, request.getToken());
  }

  @Test
  void testBuilder() {
    String testToken = "builder-token-456";
    SecurityTokenCheckRequest request = SecurityTokenCheckRequest.builder()
        .token(testToken)
        .build();

    assertEquals(testToken, request.getToken());
  }

  @Test
  void testGetterAndSetter() {
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest();
    String testToken = "setter-token-789";

    request.setToken(testToken);
    assertEquals(testToken, request.getToken());
  }

  @Test
  void testNullToken() {
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest();
    request.setToken(null);
    assertNull(request.getToken());
  }
}