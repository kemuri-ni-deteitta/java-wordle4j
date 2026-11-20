package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private WordleDictionary dictionary;
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        log = new PrintWriter(System.out);

        dictionary = new WordleDictionary(
                List.of("КОШКА", "маска", "парус", "дом", "ПЧЁЛЫ"),
                log
        );
    }

    @Test
    void shouldNormalizeWord() {
        assertEquals("кошка", WordleDictionary.normalize("КОШКА"));
        assertEquals("пчелы", WordleDictionary.normalize("ПЧЁЛЫ"));
        assertEquals("маска", WordleDictionary.normalize("  маска  "));
    }

    @Test
    void shouldAcceptValidWordFormat() {
        assertTrue(WordleDictionary.isValidWord("кошка"));
        assertTrue(WordleDictionary.isValidWord("маска"));
    }

    @Test
    void shouldRejectInvalidWordFormat() {
        assertFalse(WordleDictionary.isValidWord(null));
        assertFalse(WordleDictionary.isValidWord("кот"));
        assertFalse(WordleDictionary.isValidWord("hello"));
        assertFalse(WordleDictionary.isValidWord("маск5"));
        assertFalse(WordleDictionary.isValidWord("МАСКА"));
    }

    @Test
    void shouldFilterInvalidWords() {
        assertEquals(
                List.of("кошка", "маска", "парус", "пчелы"),
                dictionary.getWords()
        );
    }

    @Test
    void shouldRemoveDuplicatesAfterNormalization() {
        WordleDictionary testDictionary = new WordleDictionary(
                List.of("КОШКА", "кошка", "ПЧЁЛЫ", "пчелы"),
                log
        );

        assertEquals(
                List.of("кошка", "пчелы"),
                testDictionary.getWords()
        );
    }

    @Test
    void shouldFindWordsAfterNormalization() {
        assertTrue(dictionary.contains("кошка"));
        assertTrue(dictionary.contains("КОШКА"));
        assertTrue(dictionary.contains("пчёлы"));
        assertTrue(dictionary.contains("  МАСКА  "));
    }

    @Test
    void shouldRejectMissingWords() {
        assertFalse(dictionary.contains("театр"));
        assertFalse(dictionary.contains("дом"));
        assertFalse(dictionary.contains(null));
    }

    @Test
    void shouldRejectDictionaryWithoutValidWords() {
        assertThrows(
                IllegalStateException.class,
                () -> new WordleDictionary(List.of("дом", "hello"), log)
        );

        assertThrows(
                IllegalStateException.class,
                () -> new WordleDictionary(List.of(), log)
        );
    }

    @Test
    void shouldRejectNullConstructorArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WordleDictionary(null, log)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new WordleDictionary(List.of("маска"), null)
        );
    }

    @Test
    void shouldReturnImmutableWordsList() {
        List<String> words = dictionary.getWords();

        assertThrows(
                UnsupportedOperationException.class,
                () -> words.add("театр")
        );
    }

    @Test
    void shouldReturnRandomWordFromDictionary() {
        WordleDictionary testDictionary = new WordleDictionary(
                List.of("маска"),
                log
        );

        assertEquals("маска", testDictionary.getRandomWord());
    }
}
