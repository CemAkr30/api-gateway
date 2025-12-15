package tr.gov.gib.ebyn.api.gateway.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static tr.gov.gib.ebyn.api.gateway.constants.SecureRandomConstant.CHARACTERS;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tr.gov.gib.ebyn.api.gateway.components.generators.RandomTraceIdGenerator;

class RandomTraceIdGeneratorTest {

    @InjectMocks
    private RandomTraceIdGenerator randomTraceIdGenerator;

    @Mock
    private SecureRandom secureRandom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTraceIdFormat() {
        String traceId = randomTraceIdGenerator.generate();
        assertTrue(traceId.matches("\\d+-[A-Za-z0-9]{5}"), "Trace ID format is incorrect");
    }

    @Test
    void testGenerateUniqueTraceIdsInSameSecond() {
        Set<String> traceIds = new HashSet<>();
        var totalTraceIds = 100;
        when(secureRandom.nextInt(CHARACTERS.length())).thenAnswer(invocation -> {
            return (int) (Math.random() * CHARACTERS.length());
        });

        for (int i = 0; i < totalTraceIds; i++) {
            traceIds.add(randomTraceIdGenerator.generate());
        }

        assertEquals(totalTraceIds, traceIds.size(), "Trace IDs should be unique in the same second");
    }

    @Test
    void testGenerateDifferentTraceIdsWithDifferentRandom() {
        when(secureRandom.nextInt(CHARACTERS.length()))
                .thenReturn(0, 1, 2, 3, 4)
                .thenReturn(5, 6, 7, 8, 9);

        String traceId1 = randomTraceIdGenerator.generate();
        String traceId2 = randomTraceIdGenerator.generate();

        assertNotEquals(traceId1, traceId2, "Trace IDs should be different with different random values");
    }

    @Test
    void testGenerateTraceIdLength() {
        String traceId = randomTraceIdGenerator.generate();
        assertEquals(16, traceId.length(), "Trace ID length is incorrect");
    }
}
