
package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws IOException {

        // Создаём пустой список для хранения слов из файла
        List<String> words = new ArrayList<>();

        // Открываем файл в кодировке UTF-8
        // BufferedReader позволяет читать файл построчно
        try (BufferedReader reader = new BufferedReader(
                new FileReader(fileName, StandardCharsets.UTF_8))) {

            // Переменная для хранения текущей прочитанной строки
            String line;

            // readLine() читает следующую строку и записывает её в line
            // Если достигнут конец файла, readLine() возвращает null
            // Цикл выполняется, пока line != null
            while ((line = reader.readLine()) != null) {

                // Добавляем прочитанную строку в конец списка words
                words.add(line);
            }
        }
        // try-with-resources автоматически закрывает BufferedReader

        // Проверяем, что список не остался пустым
        if (words.isEmpty()) {
            throw new IllegalStateException("Файл словаря пуст");
        }

        // Выводим количество прочитанных строк
        log.println("Прочитано строк из словаря: " + words.size());

        // Создаём объект словаря и возвращаем его из метода
        return new WordleDictionary(words, log);
    }
}
