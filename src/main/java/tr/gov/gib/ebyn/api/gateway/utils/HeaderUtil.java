package tr.gov.gib.ebyn.api.gateway.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tr.gov.gib.ebyn.api.gateway.exceptions.UserInfoAccessException;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.UserInfo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static tr.gov.gib.ebyn.api.gateway.exceptions.ServiceErrors.E03002;


@Component
@Log4j2
public class HeaderUtil {

  public Map<String, String> convertUserInfoToRequestHeadersInCase(UserInfo userInfo) {
    Map<String, String> headers = new HashMap<>();
    try {
      Field[] fields = userInfo.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        Object value = field.get(userInfo);
        if (value != null) {
          String key = toCaseWithHyphen(field.getName());
          headers.put(key, String.valueOf(value));
        }
      }
    } catch (IllegalAccessException e) {
      log.error("Error accessing userInfo fields: {}", e.getMessage(), e);
      throw new UserInfoAccessException(Integer.parseInt(E03002.getCode()), e.getMessage());
    }
    return headers;
  }

  public MultiValueMap<String, String> convertUserInfoToResponseHeadersInCase(UserInfo userInfo) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    try {
      Field[] fields = userInfo.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        Object value = field.get(userInfo);
        if (value != null) {
          String key = toCaseWithHyphen(field.getName());
          headers.put(key, Collections.singletonList(String.valueOf(value)));
        }
      }
    } catch (IllegalAccessException e) {
      log.error("Error accessing userInfo fields: {}", e.getMessage(), e);
      throw new UserInfoAccessException(Integer.parseInt(E03002.getCode()), e.getMessage());
    }
    return headers;
  }

  public String toCaseWithHyphen(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    StringBuilder result = new StringBuilder();
    char[] chars = input.toCharArray();
    boolean isFirst = true;

    for (char c : chars) {
      if (Character.isUpperCase(c)) {
        result.append('-').append(c);
      } else {
        if (isFirst) {
          result.append(Character.toUpperCase(c));
          isFirst = false;
        } else {
          result.append(c);
        }
      }
    }

    return result.toString();
  }
}
