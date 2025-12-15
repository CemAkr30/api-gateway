package tr.gov.gib.ebyn.api.gateway.utils;

import static tr.gov.gib.ebyn.api.gateway.constants.WhiteList.AUTH_WHITELIST;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


@Component
@Log4j2
public class SecurityUtil
{

    public boolean webSecurityCustomizer(String uri) {
        if (uri == null) {
            return false;
        }

        var whiteList = List.of(AUTH_WHITELIST);

        return whiteList.stream().anyMatch(pattern -> matchesPattern(uri, pattern));
    }

    private boolean matchesPattern(String uri, String pattern) {
        // Wildcard pattern matching
        if (pattern.endsWith("/**")) {
            var basePattern = pattern.substring(0, pattern.length() - 2); // remove "/**"
            return uri.contains(basePattern);
        }

        return uri.contains(pattern);
    }

}
