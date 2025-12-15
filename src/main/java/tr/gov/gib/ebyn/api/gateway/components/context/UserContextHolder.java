package tr.gov.gib.ebyn.api.gateway.components.context;

import lombok.Getter;
import lombok.Setter;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.UserInfo;

public class UserContextHolder {

  private static final ThreadLocal<UserContext> userContext = ThreadLocal.withInitial(UserContext::new);

  private UserContextHolder() {
  }

  public static UserContext getContext() {
    return userContext.get();
  }

  public static void setContext(UserContext context) {
    userContext.set(context);
  }

  public static void clear() {
    userContext.remove();
  }

  @Getter
  @Setter
  public static class UserContext {
    private UserInfo userInfo;
  }
}
