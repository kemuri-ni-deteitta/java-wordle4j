package ru.yandex.practicum;

/**
 * Сигнализирует об отсутствии введённого слова в игровом словаре.
 */
public class WordNotFoundInDictionary extends Exception {

    public WordNotFoundInDictionary(String word) {
        super("Слово \"" + word + "\" отсутствует в словаре");
    }
}
