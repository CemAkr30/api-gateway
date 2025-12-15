package tr.gov.gib.ebyn.api.gateway.exceptions;

public enum ServiceErrors implements IErrors {
  E03001("%s"),
  E03002("Error accessing userInfo fields"),
  ;

  private final String message;

  ServiceErrors(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return this.name();
  }

  @Override
  public String getMessage() {
    return this.message;
  }

}
