package tr.gov.gib.ebyn.api.gateway.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NotNull
public class AuthorizationException extends RuntimeException {

  private final int code;
  private final String message;
}
