package tr.gov.gib.ebyn.api.gateway.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.SecurityProperty;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.ValidationServiceProperty;
import tr.gov.gib.ebyn.api.gateway.model.BasicAuthDeclaration;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.DataResponse;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.ServiceStatus;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.UserInfo;
import tr.gov.gib.ebyn.api.gateway.services.AuthServiceImpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {
  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ValidationServiceProperty validationServiceProperty;

  @Mock
  private SecurityProperty securityProperty;

  @InjectMocks
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(securityProperty.getKey()).thenReturn("testApiKey");
    when(securityProperty.getFixedTokens()).thenReturn("token,token1");

    ValidationServiceProperty vsp = new ValidationServiceProperty();
    BasicAuthDeclaration basicAuthDeclaration = new BasicAuthDeclaration();
    basicAuthDeclaration.setUserName("user");
    basicAuthDeclaration.setPassword("pass");
    vsp.setBasicAuth(basicAuthDeclaration);

    when(validationServiceProperty.getBasicAuth()).thenReturn(vsp.getBasicAuth());
    when(validationServiceProperty.getUrl()).thenReturn("http://mockurl.com");
    when(validationServiceProperty.getUserInfoByTokenServiceEndpoint()).thenReturn("/validate");
  }

  @Test
  void testIsValidApiKey_Success() {
    boolean result = authService.isAuthorized("testApiKey", null);
    assertTrue(result, "API key should be valid.");
  }

  @Test
  void testIsValidApiKey_Failure() {
    boolean result = authService.isAuthorized("invalidApiKey", null);
    assertFalse(result, "Invalid API key should fail.");
  }

  @Test
  void testIsValidToken_Success() {
    SecurityTokenCheckResponse mockResponse = new SecurityTokenCheckResponse();
    mockResponse.setServiceStatus(new ServiceStatus("1000",
        "İşlem başarılı.",
        "İşlem başarılı.",
        "2025/01/17-14:05:23:206"
    ));
    mockResponse.setData(new DataResponse(
        new UserInfo(
            "99500120",
            "Ad",
            "Soyad",
            "12312312312",
            "123123",
            1,
            "Ad Soyad"
        )
    ));

    when(restTemplate.exchange(
        any(String.class),
        any(),
        any(),
        any(Class.class)
    )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

    boolean result = authService.isAuthorized(null, "validToken");
    assertTrue(result, "Valid token should be authorized.");
  }

  @Test
  void testIsValidToken_Failure() {
    when(restTemplate.exchange(
        any(String.class),
        any(),
        any(),
        any(Class.class)
    )).thenThrow(new RuntimeException("Mock exception"));

    boolean result = authService.isAuthorized(null, "invalidToken");
    assertFalse(result, "Invalid token should not be authorized.");
  }
}
