package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Загружает слова из текстового файла UTF-8
 * и передаёт их в {@link WordleDictionary} для обработки.
 */
public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    /**
     * Читает все строки файла и создаёт словарь.
     * Нормализация и фильтрация слов выполняются в {@link WordleDictionary}.
     *
     * @param fileName путь к файлу словаря
     * @return словарь с обработанными словами
     * @throws IOException если файл не удалось открыть или прочитать
     * @throws IllegalStateException если файл пуст или после фильтрации
     *                               не осталось подходящих слов
     */
    public WordleDictionary load(String fileName) throws IOException {

        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(fileName, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        }

        if (words.isEmpty()) {
            throw new IllegalStateException("Файл словаря пуст");
        }

        log.println("Прочитано строк из словаря: " + words.size());

        return new WordleDictionary(words, log);
    }
}
