package ru.yandex.practicum;

import java.util.Objects;

/**
 * Сравнивает загаданное слово с попыткой игрока по правилам Wordle.
 * Учитывает позиции букв и их повторения, не допуская
 * многократного использования одной буквы ответа.
 */
public class WordMatcher {

    private static final int WORD_LENGTH = 5;

    /**
     * Формирует подсказку для попытки игрока.
     * Точные совпадения имеют приоритет перед совпадениями на других позициях.
     * Нормализация слов и проверка допустимых символов не выполняются.
     *
     * @param answer загаданное слово из пяти символов
     * @param guess слово игрока из пяти символов
     * @return строка из пяти символов: '+' — точная позиция,
     *         '^' — буква на другой позиции, '-' — отсутствие совпадения
     * @throws NullPointerException если один из аргументов равен null
     * @throws IllegalArgumentException если длина любого слова не равна пяти
     */
    public String match(String answer, String guess) {

        Objects.requireNonNull(answer, "answer не должен быть null");
        Objects.requireNonNull(guess, "guess не должен быть null");

        if (answer.length() != WORD_LENGTH
                || guess.length() != WORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Оба слова должны содержать 5 букв"
            );
        }

        char[] result = new char[WORD_LENGTH];

        // Каждая позиция ответа может участвовать только в одном совпадении.
        boolean[] used = new boolean[WORD_LENGTH];

        // Сначала резервируем точные совпадения, чтобы повторяющиеся буквы
        // не были ошибочно учтены на других позициях.
        for (int i = 0; i < WORD_LENGTH; i++) {

            if (answer.charAt(i) == guess.charAt(i)) {
                result[i] = '+';
                used[i] = true;
            }
        }

        // Оставшиеся буквы сопоставляем только с неиспользованными позициями ответа.
        for (int i = 0; i < WORD_LENGTH; i++) {

            if (result[i] == '+') {
                continue;
            }

            result[i] = '-';

            for (int j = 0; j < WORD_LENGTH; j++) {

                if (used[j] == false
                        && guess.charAt(i) == answer.charAt(j)) {

                    result[i] = '^';
                    used[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }
}
