package tr.gov.gib.ebyn.api.gateway.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tr.gov.gib.ebyn.api.gateway.components.context.UserContextHolder;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.SecurityProperty;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.ValidationServiceProperty;
import tr.gov.gib.ebyn.api.gateway.core.services.IAuthService;
import tr.gov.gib.ebyn.api.gateway.enums.ServiceStatusCode;
import tr.gov.gib.ebyn.api.gateway.requests.SecurityTokenCheckRequest;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements IAuthService {

  private final RestTemplate restTemplate;
  private final ValidationServiceProperty validationServiceProperty;
  private final SecurityProperty securityProperty;

  public boolean isAuthorized(String apiKey, String token) {
    return isValidApiKey(apiKey) || checkFixedToken(token) || isValidToken(token);
  }

  private boolean isValidApiKey(String apiKey) {
    return apiKey != null && apiKey.equals(securityProperty.getKey());
  }

  private boolean checkFixedToken(String token) {
    boolean status = Boolean.FALSE;
    final String fixedTokens = securityProperty.getFixedTokens();
    String[] tokens = fixedTokens.split(",");
    rec:
    for (final String checkToken : tokens) {
      if (checkToken.equals(token)) {
        status = Boolean.TRUE;
        break rec;
      }
    }

    return status;
  }

  private boolean isValidToken(String token) {
    if (token == null || !validateToken(token)) {
      return false;
    }
    return fetchUserInfoByToken(token);
  }

  private String buildValidationUrl(String endpoint) {
    return validationServiceProperty.getUrl() + endpoint;
  }

  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    String auth = validationServiceProperty.getBasicAuth().getUserName() + ":" +
        validationServiceProperty.getBasicAuth().getPassword();
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
    headers.set("Authorization", "Basic " + new String(encodedAuth));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private boolean validateToken(String token) {
    String url = buildValidationUrl(validationServiceProperty.getCheckTokenServiceEndpoint());
    HttpHeaders headers = createHeaders();
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest(token);
    return executeValidationRequest(url, headers, request);
  }

  private boolean fetchUserInfoByToken(String token) {
    String url = buildValidationUrl(validationServiceProperty.getUserInfoByTokenServiceEndpoint());
    HttpHeaders headers = createHeaders();
    SecurityTokenCheckRequest request = new SecurityTokenCheckRequest(token);

    try {
      ResponseEntity<SecurityTokenCheckResponse> responseEntity = restTemplate.exchange(
          url, HttpMethod.POST, new HttpEntity<>(request, headers), SecurityTokenCheckResponse.class);

      if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
        SecurityTokenCheckResponse response = responseEntity.getBody();
        String serviceStatusCode = response.getServiceStatus().getCode();
        ServiceStatusCode status = ServiceStatusCode.fromCode(serviceStatusCode);

        if (status == ServiceStatusCode.SUCCESS) {
          UserContextHolder.UserContext userContext = new UserContextHolder.UserContext();
          userContext.setUserInfo(response.getData().getUserInfo());
          UserContextHolder.setContext(userContext);
          return true;
        }
      }
    } catch (Exception e) {
      log.warn("Failed to fetch user info by token. Error: {}", e.getMessage());
    }
    return false;
  }

  private boolean executeValidationRequest(
      String url,
      HttpHeaders headers,
      SecurityTokenCheckRequest request
  ) {
    try {
      ResponseEntity<SecurityTokenCheckResponse> responseEntity = restTemplate.exchange(
          url, HttpMethod.POST, new HttpEntity<>(request, headers), SecurityTokenCheckResponse.class);

      if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
        SecurityTokenCheckResponse response = responseEntity.getBody();
        String serviceStatusCode = response.getServiceStatus().getCode();
        ServiceStatusCode status = ServiceStatusCode.fromCode(serviceStatusCode);
        return status == ServiceStatusCode.SUCCESS;
      }
    } catch (Exception e) {
      log.warn("Validation request failed. Error: {}", e.getMessage());
    }
    return false;
  }
}
