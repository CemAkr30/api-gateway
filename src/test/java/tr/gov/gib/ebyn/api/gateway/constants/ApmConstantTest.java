package tr.gov.gib.ebyn.api.gateway.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApmConstantTest {

  @Test
  void testConstantValues() {
    // Verify all constant values
    assertEquals("server_url", ApmConstant.SERVER_URL_KEY);
    assertEquals("service_name", ApmConstant.SERVICE_NAME_KEY);
    assertEquals("environment", ApmConstant.ENVIRONMENT_KEY);
    assertEquals("application_packages", ApmConstant.APPLICATION_PACKAGES_KEY);
    assertEquals("log_level", ApmConstant.LOG_LEVEL_KEY);
    assertEquals("enable_experimental_instrumentations", ApmConstant.ENABLE_EXPERIMENTAL_INSTRUMENTATIONS_KEY);
    assertEquals("transaction_sample_rate", ApmConstant.TRANSACTION_SAMPLE_RATE_KEY);
    assertEquals("span_frames_min_duration", ApmConstant.SPAN_FRAMES_MIN_DURATION_KEY);
    assertEquals("metrics_interval", ApmConstant.METRICS_INTERVAL_KEY);
    assertEquals("use_path_as_transaction_name", ApmConstant.USE_PATH_AS_TRANSACTION_NAME_KEY);
  }

  @Test
  void testConstantTypes() {
    // Verify all constants are of type String
    assertInstanceOf(String.class, ApmConstant.SERVER_URL_KEY);
    assertInstanceOf(String.class, ApmConstant.SERVICE_NAME_KEY);
    assertInstanceOf(String.class, ApmConstant.ENVIRONMENT_KEY);
    assertInstanceOf(String.class, ApmConstant.APPLICATION_PACKAGES_KEY);
    assertInstanceOf(String.class, ApmConstant.LOG_LEVEL_KEY);
    assertInstanceOf(String.class, ApmConstant.ENABLE_EXPERIMENTAL_INSTRUMENTATIONS_KEY);
    assertInstanceOf(String.class, ApmConstant.TRANSACTION_SAMPLE_RATE_KEY);
    assertInstanceOf(String.class, ApmConstant.SPAN_FRAMES_MIN_DURATION_KEY);
    assertInstanceOf(String.class, ApmConstant.METRICS_INTERVAL_KEY);
    assertInstanceOf(String.class, ApmConstant.USE_PATH_AS_TRANSACTION_NAME_KEY);
  }

  @Test
  void testConstantFinality() throws Exception {
    // Verify all fields are final (can't be modified)
    Class<?> clazz = ApmConstant.class;

    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("SERVER_URL_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("SERVICE_NAME_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("ENVIRONMENT_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("APPLICATION_PACKAGES_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("LOG_LEVEL_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("ENABLE_EXPERIMENTAL_INSTRUMENTATIONS_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("TRANSACTION_SAMPLE_RATE_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("SPAN_FRAMES_MIN_DURATION_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("METRICS_INTERVAL_KEY").getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isFinal(clazz.getField("USE_PATH_AS_TRANSACTION_NAME_KEY").getModifiers()));
  }
}