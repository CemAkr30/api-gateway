package tr.gov.gib.ebyn.api.gateway.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InternalServerErrorTest {

  @Test
  void testConstructorAndGetters() {
    // Given
    int expectedCode = 500;
    String expectedMessage = "Internal Server Error";

    // When
    InternalServerError exception = new InternalServerError(expectedCode, expectedMessage);

    // Then
    assertEquals(expectedCode, exception.getCode());
    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void testSuperClassBehavior() {
    // Given
    String message = "Test message";
    InternalServerError exception = new InternalServerError(500, message);

    // Then
    assertEquals(message, exception.getMessage());
    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void testSetters() {
    // Given
    InternalServerError exception = new InternalServerError(400, "Bad Request");

    // Then
    assertEquals(400, exception.getCode());
    assertEquals("Bad Request", exception.getMessage());
  }
}