package tr.gov.gib.ebyn.api.gateway.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class SecureRandomConstantTest {

  @Test
  void testConstantsHaveCorrectValues() {
    assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
        SecureRandomConstant.CHARACTERS);
    assertEquals(5, SecureRandomConstant.TRACE_ID_LENGTH);
  }

  @Test
  void testAllFieldsArePublicStaticFinal() {
    Field[] fields = SecureRandomConstant.class.getDeclaredFields();

    assertEquals(2, fields.length, "Should have exactly 2 constants");

    for (Field field : fields) {
      assertTrue(Modifier.isPublic(field.getModifiers()),
          field.getName() + " should be public");
      assertTrue(Modifier.isStatic(field.getModifiers()),
          field.getName() + " should be static");
      assertTrue(Modifier.isFinal(field.getModifiers()),
          field.getName() + " should be final");
    }
  }

  @Test
  void testConstantTypes() {
    assertInstanceOf(String.class, SecureRandomConstant.CHARACTERS);
    assertInstanceOf(Integer.class, SecureRandomConstant.TRACE_ID_LENGTH);
  }

  @Test
  void testClassCannotBeInstantiated() throws Exception {
    Class<?> clazz = Class.forName("tr.gov.gib.ebyn.api.gateway.constants.SecureRandomConstant");
    assertFalse(Modifier.isFinal(clazz.getModifiers()), "Class should be final");

    // Verify no public constructor exists
    assertEquals(1, clazz.getDeclaredConstructors().length,
        "Utility class should have public constructors");
  }

  @Test
  void testConstantsAreImmutable() throws NoSuchFieldException {
    Field charactersField = SecureRandomConstant.class.getDeclaredField("CHARACTERS");
    Field traceIdLengthField = SecureRandomConstant.class.getDeclaredField("TRACE_ID_LENGTH");

    // Verify the fields are final
    assertTrue(Modifier.isFinal(charactersField.getModifiers()));
    assertTrue(Modifier.isFinal(traceIdLengthField.getModifiers()));

    // Verify the values can't be changed
    assertThrows(IllegalAccessException.class, () -> {
      charactersField.setAccessible(true);
      charactersField.set(null, "new-characters");
    });

    assertThrows(IllegalAccessException.class, () -> {
      traceIdLengthField.setAccessible(true);
      traceIdLengthField.set(null, 10);
    });
  }

  @Test
  void testCharactersConstantContent() {
    String characters = SecureRandomConstant.CHARACTERS;

    // Verify it contains all expected character groups
    assertTrue(characters.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    assertTrue(characters.contains("abcdefghijklmnopqrstuvwxyz"));
    assertTrue(characters.contains("0123456789"));

    // Verify length
    assertEquals(62, characters.length());

    // Verify no duplicates
    assertEquals(62, characters.chars().distinct().count());
  }

  @Test
  void testTraceIdLengthIsPositive() {
    assertTrue(SecureRandomConstant.TRACE_ID_LENGTH > 0,
        "TRACE_ID_LENGTH should be positive");
  }
}