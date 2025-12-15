package tr.gov.gib.ebyn.api.gateway.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class HeaderConstantTest {

    @Test
    void testConstantsHaveCorrectValues() {
        assertEquals("X-Trace-Id", HeaderConstant.X_TRACE_ID);
        assertEquals("X-Span-Id", HeaderConstant.X_SPAN_ID);
    }

    @Test
    void testAllFieldsArePublicStaticFinal() throws NoSuchFieldException {
        Field[] fields = HeaderConstant.class.getDeclaredFields();

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
    void testAllConstantsAreStrings() {
        assertInstanceOf(String.class, HeaderConstant.X_TRACE_ID);
        assertInstanceOf(String.class, HeaderConstant.X_SPAN_ID);
    }

    @Test
    void testConstantsAreImmutable() throws NoSuchFieldException, IllegalAccessException {
        Field traceIdField = HeaderConstant.class.getDeclaredField("X_TRACE_ID");
        Field spanIdField = HeaderConstant.class.getDeclaredField("X_SPAN_ID");

        // Verify the fields are final
        assertTrue(Modifier.isFinal(traceIdField.getModifiers()));
        assertTrue(Modifier.isFinal(spanIdField.getModifiers()));

        // Verify the values can't be changed
        assertThrows(IllegalAccessException.class, () -> {
            traceIdField.setAccessible(true);
            traceIdField.set(null, "new-trace-id");
        });

        assertThrows(IllegalAccessException.class, () -> {
            spanIdField.setAccessible(true);
            spanIdField.set(null, "new-span-id");
        });
    }

    @Test
    void testConstantNamesFollowConvention() {
        assertTrue(HeaderConstant.X_TRACE_ID.matches("^X-[A-Z][a-zA-Z]*-[A-Z][a-zA-Z]*$"),
            "Trace ID constant should follow X-Header-Case convention");
        assertTrue(HeaderConstant.X_SPAN_ID.matches("^X-[A-Z][a-zA-Z]*-[A-Z][a-zA-Z]*$"),
            "Span ID constant should follow X-Header-Case convention");
    }
}