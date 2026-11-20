package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordMatcherTest {

    private final WordMatcher matcher = new WordMatcher();

    @Test
    void shouldReturnAllCorrectPositions() {
        String result = matcher.match("маска", "маска");

        assertEquals("+++++", result);
    }

    @Test
    void shouldReturnAllMisplacedLetters() {
        String result = matcher.match("герой", "ройге");

        assertEquals("^^^^^", result);
    }

    @Test
    void shouldReturnMixedFeedback() {
        String result = matcher.match("герой", "гонец");

        assertEquals("+^-^-", result);
    }

    @Test
    void shouldHandleRepeatedLetters() {
        String result = matcher.match("маска", "мамма");

        assertEquals("++--+", result);
    }

    @Test
    void shouldReturnAllAbsentLetters() {
        String result = matcher.match("маска", "герой");

        assertEquals("-----", result);
    }

    @Test
    void shouldRejectInvalidWordLength() {
        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.match("маска", "кот")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> matcher.match("кот", "маска")
        );
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(
                NullPointerException.class,
                () -> matcher.match(null, "маска")
        );

        assertThrows(
                NullPointerException.class,
                () -> matcher.match("маска", null)
        );
    }
}

