package tr.gov.gib.ebyn.api.gateway.components.generators;

import static tr.gov.gib.ebyn.api.gateway.constants.SecureRandomConstant.CHARACTERS;
import static tr.gov.gib.ebyn.api.gateway.constants.SecureRandomConstant.TRACE_ID_LENGTH;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomTraceIdGenerator {

    private final SecureRandom secureRandom;

    public String generate() {
        var traceId = new StringBuilder();

        long epochSecond = Instant.now().getEpochSecond();
        traceId.append(epochSecond);
        traceId.append("-");

        var characterList = new ArrayList<>();
        for (char c : CHARACTERS.toCharArray()) {
            characterList.add(c);
        }

        Collections.shuffle(characterList, secureRandom);

        for (int i = 0; i < TRACE_ID_LENGTH; i++) {
            secureRandom.setSeed(secureRandom.generateSeed(1));
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            traceId.append(characterList.get(randomIndex));
        }

        return traceId.toString();
    }


}
