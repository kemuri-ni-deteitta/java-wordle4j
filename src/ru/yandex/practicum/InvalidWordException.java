
package ru.yandex.practicum;

/**
 * Сигнализирует о некорректном формате слова, введённого игроком.
 */
public class InvalidWordException extends Exception {

    public InvalidWordException(String message) {
        super(message);
    }
}
