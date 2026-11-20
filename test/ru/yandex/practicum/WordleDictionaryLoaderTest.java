package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    private WordleDictionaryLoader loader;

    @BeforeEach
    void setUp() {
        PrintWriter log = new PrintWriter(System.out);
        loader = new WordleDictionaryLoader(log);
    }

    @Test
    void shouldLoadDictionaryFromFile() throws IOException {
        Path file = tempDir.resolve("dictionary.txt");

        Files.write(file,
                List.of("кошка", "маска", "парус"),
                StandardCharsets.UTF_8);

        WordleDictionary dictionary = loader.load(file.toString());

        assertEquals(
                List.of("кошка", "маска", "парус"),
                dictionary.getWords()
        );
    }

    @Test
    void shouldReadUtf8Characters() throws IOException {
        Path file = tempDir.resolve("utf8.txt");

        Files.write(file,
                List.of("ПЧЁЛЫ", "ЁЖИКИ"),
                StandardCharsets.UTF_8);

        WordleDictionary dictionary = loader.load(file.toString());

        assertEquals(
                List.of("пчелы", "ежики"),
                dictionary.getWords()
        );
    }

    @Test
    void shouldLoadAndFilterMixedWords() throws IOException {
        Path file = tempDir.resolve("mixed.txt");

        Files.write(file,
                List.of("дом", "hello", "КОШКА",
                        "маска", "КОШКА", "парус"),
                StandardCharsets.UTF_8);

        WordleDictionary dictionary = loader.load(file.toString());

        assertEquals(
                List.of("кошка", "маска", "парус"),
                dictionary.getWords()
        );
    }

    @Test
    void shouldThrowExceptionForMissingFile() {
        Path file = tempDir.resolve("missing.txt");

        assertThrows(
                IOException.class,
                () -> loader.load(file.toString())
        );
    }

    @Test
    void shouldRejectEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.txt");

        Files.createFile(file);

        assertThrows(
                IllegalStateException.class,
                () -> loader.load(file.toString())
        );
    }

    @Test
    void shouldRejectFileWithoutValidWords() throws IOException {
        Path file = tempDir.resolve("invalid.txt");

        Files.write(file,
                List.of("дом", "hello", "12345"),
                StandardCharsets.UTF_8);

        assertThrows(
                IllegalStateException.class,
                () -> loader.load(file.toString())
        );
    }
}
