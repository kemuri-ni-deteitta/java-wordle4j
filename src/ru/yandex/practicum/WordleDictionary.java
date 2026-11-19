
package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class WordleDictionary {

    // Константа: допустимая длина слова — 5 символов
    private static final int WORD_LENGTH = 5;

    // Список слов для доступа по индексу и случайного выбора
    private final List<String> words;

    // Множество слов для быстрой проверки их наличия
    private final Set<String> wordSet;

    // Объект для записи сообщений о работе словаря
    private final PrintWriter log;

    // Конструктор получает исходный список слов и объект для логирования
    public WordleDictionary(List<String> rawWords, PrintWriter log) {

        // Проверяем, что аргументы конструктора не равны null
        if (rawWords == null || log == null) {
            throw new IllegalArgumentException(
                    "Список слов и журнал не должны быть null"
            );
        }

        // Сохраняем переданный PrintWriter в поле текущего объекта
        this.log = log;

        // Создаём множество для хранения уникальных слов
        // LinkedHashSet не допускает дубликатов и сохраняет порядок добавления
        Set<String> uniqueWords = new LinkedHashSet<>();

        // Перебираем каждое слово из исходного списка rawWords
        // rawWord — текущий элемент списка на каждой итерации
        for (String rawWord : rawWords) {

            // Если элемент равен null, пропускаем его
            // continue переходит к следующей итерации цикла
            if (rawWord == null) {
                continue;
            }

            // Нормализуем слово: удаляем пробелы по краям,
            // переводим в нижний регистр и заменяем 'ё' на 'е'
            String word = normalize(rawWord);

            // Проверяем, состоит ли слово ровно из 5 русских букв
            // isValidWord() возвращает boolean: true или false
            if (isValidWord(word)) {

                // Добавляем подходящее слово в множество
                // Если такое слово уже существует, дубликат не добавится
                uniqueWords.add(word);
            }
        }

        // Проверяем, остались ли подходящие слова после обработки
        // isEmpty() возвращает true, если множество пустое
        if (uniqueWords.isEmpty()) {
            throw new IllegalStateException(
                    "В словаре нет подходящих пятибуквенных слов"
            );
        }

        // Создаём ArrayList из подготовленных уникальных слов
        // Нужен для получения слова по индексу через get(index)
        this.words = new ArrayList<>(uniqueWords);

        // Создаём HashSet с теми же словами
        // Нужен для быстрой проверки наличия слова через contains()
        this.wordSet = new HashSet<>(uniqueWords);

        // Выводим количество слов, доступных для игры
        log.println("Загружено слов для игры: " + words.size());
    }

    // Приводим слово к единому формату
    // static — метод принадлежит классу, а не конкретному объекту
    public static String normalize(String word) {

        // strip() — удаляет пробельные символы по краям строки
        // toLowerCase(Locale.ROOT) — приводит буквы к нижнему регистру
        // replace() — заменяет все символы 'ё' на 'е'
        // Возвращаем новую нормализованную строку
        return word.strip()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е');
    }

    // Проверяем, подходит ли слово по формату для Wordle
    // Возвращаем true, если подходит, иначе false
    private boolean isValidWord(String word) {

        // Проверяем, что длина слова равна 5
        // Если длина другая, сразу завершаем метод
        if (word.length() != WORD_LENGTH) {
            return false;
        }

        // Перебираем каждый символ слова по индексам от 0 до 4
        for (int i = 0; i < WORD_LENGTH; i++) {

            // Получаем символ строки по текущему индексу i
            char letter = word.charAt(i);

            // Проверяем, находится ли символ вне диапазона 'а'–'я'
            // || означает ИЛИ: достаточно нарушения одной границы
            if (letter < 'а' || letter > 'я') {
                return false; // Недопустимый символ — завершаем метод
            }
        }

        // Все 5 символов прошли проверку
        return true;
    }

    // Проверяем, присутствует ли слово в словаре
    public boolean contains(String word) {

        // Если передали null, такого слова в словаре нет
        if (word == null) {
            return false;
        }

        // normalize(word) приводит слово к формату нашего словаря
        // wordSet.contains() ищет слово в HashSet
        // Возвращает true, если слово найдено, иначе false
        return wordSet.contains(normalize(word));
    }

    // Выбираем случайное слово из словаря
    public String getRandomWord() {

        // current() получает генератор для текущего потока
        // words.size() возвращает количество слов
        // nextInt(size) генерирует случайный индекс от 0 до size - 1
        int index = ThreadLocalRandom.current()
                .nextInt(words.size());

        // Получаем слово из ArrayList по случайному индексу
        // И возвращаем его из метода
        return words.get(index);
    }

    // Предоставляем доступ к словам словаря
    public List<String> getWords() {

        // Создаём неизменяемую копию списка
        // Вызывающий код не сможет изменить внутренний список words
        return List.copyOf(words);
    }
}
