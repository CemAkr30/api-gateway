package tr.gov.gib.ebyn.api.gateway.configurations;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureRandomConfiguration {

    @Bean
    public SecureRandom secureRandom(){
        return new SecureRandom();
    }
}
