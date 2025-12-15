package tr.gov.gib.ebyn.api.gateway.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserInfoAccessExceptionTest {

  @Test
  void testUserInfoAccessExceptionFields() {
    int expectedCode = 403;
    String expectedMessage = "User does not have access";

    UserInfoAccessException exception = new UserInfoAccessException(expectedCode, expectedMessage);

    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedMessage, exception.getMessage());
  }
}
