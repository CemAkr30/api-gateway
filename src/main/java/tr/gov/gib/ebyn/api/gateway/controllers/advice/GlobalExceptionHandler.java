package tr.gov.gib.ebyn.api.gateway.controllers.advice;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tr.gov.gib.ebyn.api.gateway.exceptions.AuthorizationException;
import tr.gov.gib.ebyn.api.gateway.exceptions.InternalServerError;
import tr.gov.gib.ebyn.api.gateway.responses.errors.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthorizationException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleAuthorizationException(
      AuthorizationException ex,
      ServerWebExchange exchange
  ) {
    ErrorResponse body = new ErrorResponse(
        Integer.toString(ex.getCode()),
        ex.getMessage(),
        LocalDateTime.now(),
        exchange.getRequest().getURI().toString()
    );

    return Mono.just(ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(body));
  }

  @ExceptionHandler({InternalServerError.class, Exception.class})
  public Mono<ResponseEntity<ErrorResponse>> handleInternalServerError(
      Throwable ex,
      ServerWebExchange exchange
  ) {
    int code = (ex instanceof InternalServerError) ? ((InternalServerError) ex).getCode() : 500;

    ErrorResponse body = new ErrorResponse(
        Integer.toString(code),
        ex.getMessage(),
        LocalDateTime.now(),
        exchange.getRequest().getURI().toString()
    );

    return Mono.just(ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(
      IllegalArgumentException ex,
      ServerWebExchange exchange
  ) {
    ErrorResponse body = new ErrorResponse(
        Integer.toString(HttpStatus.BAD_REQUEST.value()),
        ex.getMessage(),
        LocalDateTime.now(),
        exchange.getRequest().getURI().toString()
    );

    return Mono.just(ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(body));
  }
}
