package tr.gov.gib.ebyn.api.gateway.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ebyn.exception.err.IErrors;
import org.junit.jupiter.api.Test;

class ServiceErrorsTest {

  @Test
  void testEnumValues() {
    // Verify enum values exist
    ServiceErrors error = ServiceErrors.E03001;
    ServiceErrors error2 = ServiceErrors.E03002;
    assertNotNull(error);
    assertNotNull(error2);
    assertEquals(2, ServiceErrors.values().length);
  }

  @Test
  void testGetCode() {
    // Verify getCode returns the enum name
    assertEquals("E03001", ServiceErrors.E03001.getCode());
  }

  @Test
  void testGetMessage() {
    // Verify message format
    assertEquals("%s", ServiceErrors.E03001.getMessage());

    // Verify message formatting works
    String formattedMessage = String.format(ServiceErrors.E03001.getMessage(), "test error");
    assertEquals("test error", formattedMessage);
  }

  @Test
  void testImplementsIErrors() {
    // Verify interface implementation
    assertTrue(IErrors.class.isAssignableFrom(ServiceErrors.class));
    assertTrue(ServiceErrors.E03001 instanceof IErrors);
  }

  @Test
  void testMessageFormatting() {
    // Test various message formatting scenarios
    String formatted = String.format(ServiceErrors.E03001.getMessage(), "Database error");
    assertEquals("Database error", formatted);

    formatted = String.format(ServiceErrors.E03001.getMessage(), 404);
    assertEquals("404", formatted);
  }

  @Test
  void testEnumSpecificMethods() {
    // Test enum-specific methods
    ServiceErrors[] values = ServiceErrors.values();
    assertEquals(2, values.length);
    assertEquals(ServiceErrors.E03001, values[0]);
    assertEquals(ServiceErrors.E03002, values[1]);

    ServiceErrors parsed = ServiceErrors.valueOf("E03001");
    ServiceErrors parsed2 = ServiceErrors.valueOf("E03002");
    assertEquals(ServiceErrors.E03001, parsed);
    assertEquals(ServiceErrors.E03002, parsed2);
  }

  @Test
  void testToString() {
    // Verify toString behavior
    String toString = ServiceErrors.E03001.toString();
    assertTrue(toString.contains("E03001"));
  }
}