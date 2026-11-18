package ru.yandex.practicum;

public class WordMatcher {

    private static final int WORD_LENGTH = 5;

    public String match(String answer, String guess) {
        // Проверяем, что аргументы не null
        if (answer == null || guess == null) {
            throw new IllegalArgumentException(
                    "Слова не должны быть null"
            );
        }

        // Проверяем длину строк
        if (answer.length() != WORD_LENGTH
                || guess.length() != WORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Оба слова должны содержать 5 букв"
            );
        }

        // Массив для результата: '+', '^', '-'
        char[] result = new char[WORD_LENGTH];

        // Отслеживаем использованные буквы загаданного слова
        boolean[] used = new boolean[WORD_LENGTH];

        // Первый проход: точные совпадения
        for (int i = 0; i < WORD_LENGTH; i++) {

            // Если буквы совпали на одной позиции
            if (answer.charAt(i) == guess.charAt(i)) {
                result[i] = '+'; // Записываем '+'
                used[i] = true;  // Помечаем букву answer как использованную
            }
        }

        // Второй проход: буквы на других позициях
        for (int i = 0; i < WORD_LENGTH; i++) {

            // Если буква уже совпала на своей позиции
            if (result[i] == '+') {
                continue; // Переходим к следующей итерации внешнего цикла
            }

            // По умолчанию считаем, что буква отсутствует
            result[i] = '-';

            // Ищем текущую букву guess среди букв answer
            for (int j = 0; j < WORD_LENGTH; j++) {

                // Если буква answer ещё не использована
                // И совпадает с текущей буквой guess
                if (used[j] == false
                        && guess.charAt(i) == answer.charAt(j)) {

                    result[i] = '^'; // Буква есть, но на другой позиции
                    used[j] = true;  // Помечаем букву answer как использованную
                    break;           // Завершаем внутренний цикл
                }
            }
        }

        return new String(result);
    }
}
