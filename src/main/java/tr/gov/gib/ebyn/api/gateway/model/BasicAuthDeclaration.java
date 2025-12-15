package tr.gov.gib.ebyn.api.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicAuthDeclaration {
    private String userName;
    private String password;
}
