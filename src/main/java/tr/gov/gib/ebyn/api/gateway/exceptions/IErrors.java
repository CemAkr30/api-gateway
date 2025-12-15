package tr.gov.gib.ebyn.api.gateway.exceptions;

public interface IErrors {
  String getCode();

  String getMessage();

  default String getMessage(Object... args) {
    return String.format(this.getMessage(), args);
  }
}