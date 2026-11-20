package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Хранит нормализованные пятибуквенные слова для игры Wordle.
 * Обеспечивает проверку наличия слов и случайный выбор слова для матча.
 */
public class WordleDictionary {

    private static final int WORD_LENGTH = 5;

    private final List<String> words;

    private final Set<String> wordSet;

    private final PrintWriter log;

    /**
     * Формирует словарь из исходного списка, нормализуя слова,
     * исключая неподходящие значения и дубликаты.
     *
     * @param rawWords исходные слова для обработки
     * @param log журнал работы словаря
     * @throws IllegalArgumentException если список или журнал равен null
     * @throws IllegalStateException если после фильтрации не осталось подходящих слов
     */
    public WordleDictionary(List<String> rawWords, PrintWriter log) {

        if (rawWords == null || log == null) {
            throw new IllegalArgumentException(
                    "Список слов и журнал не должны быть null"
            );
        }

        this.log = log;

        // LinkedHashSet удаляет дубликаты, сохраняя порядок первого появления слов.
        Set<String> uniqueWords = new LinkedHashSet<>();

        for (String rawWord : rawWords) {

            if (rawWord == null) {
                continue;
            }

            String word = normalize(rawWord);

            if (isValidWord(word)) {
                uniqueWords.add(word);
            }
        }

        if (uniqueWords.isEmpty()) {
            throw new IllegalStateException(
                    "В словаре нет подходящих пятибуквенных слов"
            );
        }

        // List обеспечивает выбор по индексу, Set — быстрый поиск слова.
        this.words = new ArrayList<>(uniqueWords);

        this.wordSet = new HashSet<>(uniqueWords);

        log.println("Загружено слов для игры: " + words.size());
    }

    /**
     * Приводит слово к единому формату: удаляет пробелы по краям,
     * переводит в нижний регистр и заменяет «ё» на «е».
     *
     * @param word исходное слово, не равное null
     * @return нормализованное слово
     */
    public static String normalize(String word) {

        return word.strip()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е');
    }

    /**
     * Проверяет формат уже нормализованного слова.
     * Допускаются ровно пять строчных русских букв от «а» до «я».
     *
     * @param word слово для проверки
     * @return true, если слово соответствует формату; иначе false
     */
    public static boolean isValidWord(String word) {

        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            char letter = word.charAt(i);

            if (letter < 'а' || letter > 'я') {
                return false;
            }
        }

        return true;
    }

    /**
     * Проверяет наличие слова после его нормализации.
     * Для null возвращает false.
     *
     * @param word искомое слово
     * @return true, если слово присутствует в словаре; иначе false
     */
    public boolean contains(String word) {

        if (word == null) {
            return false;
        }

        return wordSet.contains(normalize(word));
    }

    public String getRandomWord() {

        int index = ThreadLocalRandom.current()
                .nextInt(words.size());

        return words.get(index);
    }

    /**
     * Возвращает неизменяемую копию списка слов.
     * Изменения возвращённого списка не допускаются.
     *
     * @return список нормализованных слов
     */
    public List<String> getWords() {

        return List.copyOf(words);
    }
}
