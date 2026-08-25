package lk.ijse.eca.surefix.evidence.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ObjectKeysTest {

    @Test
    void keysLiveUnderTheRunPrefix() {
        assertEquals("runs/abc123/shot.png", ObjectKeys.key("abc123", "shot.png"));
        assertArrayEquals(new String[] {"abc123", "shot.png"}, ObjectKeys.parse("runs/abc123/shot.png"));
        assertNull(ObjectKeys.parse("other/abc123/shot.png"));
    }

    @Test
    void pathTraversalAndOddCharactersAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> ObjectKeys.key("../etc", "passwd"));
        assertThrows(IllegalArgumentException.class, () -> ObjectKeys.key("run", "a/b.png"));
        assertThrows(IllegalArgumentException.class, () -> ObjectKeys.prefix(""));
    }

    @Test
    void generatedNamesKeepASafeExtensionOnly() {
        assertTrue(ObjectKeys.newFilename("Screen Shot.PNG").endsWith(".png"));
        assertTrue(ObjectKeys.newFilename("trace.har.json").endsWith(".json"));
        assertEquals(36, ObjectKeys.newFilename("weird.<script>").length());
        assertEquals(36, ObjectKeys.newFilename(null).length());
    }
}
