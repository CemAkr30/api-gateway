package tr.gov.gib.ebyn.api.gateway.responses.errors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    @NotNull
    private String code;

    @NotNull
    private String message;

    @NotNull
    private LocalDateTime timestamp;

    @NotBlank
    private String path;
}
