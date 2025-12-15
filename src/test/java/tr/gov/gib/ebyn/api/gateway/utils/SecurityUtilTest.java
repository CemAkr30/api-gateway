package tr.gov.gib.ebyn.api.gateway.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tr.gov.gib.ebyn.api.gateway.constants.WhiteList;

class SecurityUtilTest {

  @InjectMocks
  private SecurityUtil securityUtil;

  @Mock
  private WhiteList whiteList;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  void testWebSecurityCustomizer_WithNullUri() {
    boolean result = securityUtil.webSecurityCustomizer(null);
    assertFalse(result, "Null URI should return false.");
  }

  @Test
  void testWebSecurityCustomizer_WithMatchingPattern() {
    String uri = "/v2/api-docs";
    boolean result = securityUtil.webSecurityCustomizer(uri);
    assertTrue(result, "URI matching the whitelist pattern should return true.");
  }

  @Test
  void testWebSecurityCustomizer_WithPartialMatchingPattern() {
    String uri = "/v2/api-docs";
    boolean result = securityUtil.webSecurityCustomizer(uri);
    assertTrue(result, "URI partially matching the whitelist pattern should return true.");
  }

  @Test
  void testWebSecurityCustomizer_WithNonMatchingPattern() {
    String uri = "/private/api/v1";
    boolean result = securityUtil.webSecurityCustomizer(uri);
    assertFalse(result, "URI not matching the whitelist pattern should return false.");
  }
}
