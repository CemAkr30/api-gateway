package tr.gov.gib.ebyn.api.gateway.responses.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void testAllArgsConstructorAndGetters() {
    String code = "ERR_001";
    String message = "Invalid request";
    LocalDateTime timestamp = LocalDateTime.now();
    String path = "/api/test";

    ErrorResponse errorResponse = new ErrorResponse(code, message, timestamp, path);

    assertEquals(code, errorResponse.getCode());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(path, errorResponse.getPath());
  }

  @Test
  void testSettersAndNoArgsConstructor() {
    ErrorResponse errorResponse = new ErrorResponse();

    String code = "ERR_002";
    String message = "Unauthorized";
    LocalDateTime timestamp = LocalDateTime.now();
    String path = "/api/secure";

    errorResponse.setCode(code);
    errorResponse.setMessage(message);
    errorResponse.setTimestamp(timestamp);
    errorResponse.setPath(path);

    assertEquals(code, errorResponse.getCode());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(path, errorResponse.getPath());
  }
}
