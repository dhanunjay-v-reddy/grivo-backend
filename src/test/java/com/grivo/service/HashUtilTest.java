package com.grivo.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Verifies the core tamper-evidence guarantee the whole photo-evidence
 * feature depends on: hashing the same bytes always gives the same hash
 * (so a stored hash can be checked later), and changing even one byte
 * gives a completely different hash (so tampering is detectable).
 */
class HashUtilTest {

    private final HashUtil hashUtil = new HashUtil();

    @Test
    void sameInputProducesSameHash() {
        byte[] data = "this is a test photo's bytes".getBytes(StandardCharsets.UTF_8);

        String hash1 = hashUtil.sha256(data);
        String hash2 = hashUtil.sha256(data);

        assertEquals(hash1, hash2, "hashing identical bytes must always produce the identical hash");
    }

    @Test
    void differentInputProducesDifferentHash() {
        byte[] original = "original photo bytes".getBytes(StandardCharsets.UTF_8);
        byte[] tampered = "original photo bytee".getBytes(StandardCharsets.UTF_8); // one char changed

        String originalHash = hashUtil.sha256(original);
        String tamperedHash = hashUtil.sha256(tampered);

        assertNotEquals(originalHash, tamperedHash,
                "even a one-byte change must produce a completely different hash — this is what makes tampering detectable");
    }

    @Test
    void hashIsSixtyFourHexCharacters() {
        byte[] data = "any input".getBytes(StandardCharsets.UTF_8);
        String hash = hashUtil.sha256(data);

        assertEquals(64, hash.length(), "SHA-256 hex output should always be 64 characters");
        assertEquals(hash, hash.toLowerCase(), "hash should be lowercase hex for consistent comparisons");
    }
}
