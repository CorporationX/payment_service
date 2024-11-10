package faang.school.paymentservice.utils.idempotencyKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdempotencyKeyGeneratorTest {

    @Test
    @DisplayName("Generates a 64-character idempotency key")
    void whenInputIsValidThenGenerate64CharacterIdempotencyKey() {
        String input = "sample-input-string";
        String idempotencyKey = IdempotencyKeyGenerator.hash(input);

        assertNotNull(idempotencyKey, "Idempotency key should not be null");
        assertEquals(64, idempotencyKey.length(), "Idempotency key should be exactly 64 characters long");
    }

    @Test
    @DisplayName("Generates consistent idempotency key for the same input")
    void whenInputIsSameThenIdempotencyKeyIsConsistent() {
        String input = "consistent-input";
        String idempotencyKey1 = IdempotencyKeyGenerator.hash(input);
        String idempotencyKey2 = IdempotencyKeyGenerator.hash(input);

        assertEquals(idempotencyKey1, idempotencyKey2, "Idempotency key should be the same for the same input");
    }

    @Test
    @DisplayName("Generates unique idempotency keys for different inputs")
    void whenInputsAreDifferentThenIdempotencyKeysAreUnique() {
        String input1 = "unique-input-1";
        String input2 = "unique-input-2";

        String idempotencyKey1 = IdempotencyKeyGenerator.hash(input1);
        String idempotencyKey2 = IdempotencyKeyGenerator.hash(input2);

        assertNotEquals(idempotencyKey1, idempotencyKey2, "Idempotency keys should be different for different inputs");
    }
}

