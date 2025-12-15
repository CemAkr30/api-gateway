package tr.gov.gib.ebyn.api.gateway.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class AuthConstantTest {

  @Test
  void testConstantsHaveCorrectValues() {
    assertEquals("x-api-key", AuthConstant.X_API_KEY);
    assertEquals("token", AuthConstant.TOKEN);
    assertEquals("Authorization", AuthConstant.AUTH);
  }

  @Test
  void testAllFieldsArePublicStaticFinal() {
    Field[] fields = AuthConstant.class.getDeclaredFields();

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
  void testAllConstantsAreStrings() {
    assertInstanceOf(String.class, AuthConstant.X_API_KEY);
    assertInstanceOf(String.class, AuthConstant.TOKEN);
    assertInstanceOf(String.class, AuthConstant.AUTH);
  }

  @Test
  void testConstantsAreImmutable() throws NoSuchFieldException {
    Field xApiKeyField = AuthConstant.class.getDeclaredField("X_API_KEY");
    Field tokenField = AuthConstant.class.getDeclaredField("TOKEN");
    Field authField = AuthConstant.class.getDeclaredField("AUTH");

    // Verify the fields are final (can't be modified)
    assertTrue(Modifier.isFinal(xApiKeyField.getModifiers()));
    assertTrue(Modifier.isFinal(tokenField.getModifiers()));
    assertTrue(Modifier.isFinal(authField.getModifiers()));

    // Verify the actual values can't be changed (String immutability)
    String originalXApiKey = AuthConstant.X_API_KEY;

    assertThrows(Exception.class, () -> {
      xApiKeyField.setAccessible(true);
      xApiKeyField.set(null, "new-value");
    });

    assertEquals(AuthConstant.X_API_KEY, originalXApiKey);
  }
}