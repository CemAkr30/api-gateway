package tr.gov.gib.ebyn.api.gateway.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BasicAuthDeclarationTest {

  @Test
  void testAllArgsConstructorAndGetters() {
    String expectedUsername = "admin";
    String expectedPassword = "secret";

    BasicAuthDeclaration auth = new BasicAuthDeclaration(expectedUsername, expectedPassword);

    assertEquals(expectedUsername, auth.getUserName());
    assertEquals(expectedPassword, auth.getPassword());
  }

  @Test
  void testNoArgsConstructorAndSetters() {
    BasicAuthDeclaration auth = new BasicAuthDeclaration();

    String expectedUsername = "user";
    String expectedPassword = "1234";

    auth.setUserName(expectedUsername);
    auth.setPassword(expectedPassword);

    assertEquals(expectedUsername, auth.getUserName());
    assertEquals(expectedPassword, auth.getPassword());
  }
}
