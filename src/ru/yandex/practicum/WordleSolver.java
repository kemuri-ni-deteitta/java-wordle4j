package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Подбирает слова из словаря на основе истории ходов Wordle.
 * Исключает ранее использованные слова и кандидатов,
 * противоречащих полученным подсказкам.
 */
public class WordleSolver {

    private final WordleDictionary dictionary;
    private final WordMatcher matcher;
    private final PrintWriter log;

    public WordleSolver(WordleDictionary dictionary,
                        WordMatcher matcher,
                        PrintWriter log) {

        this.dictionary = Objects.requireNonNull(dictionary);
        this.matcher = Objects.requireNonNull(matcher);
        this.log = Objects.requireNonNull(log);
    }

    /**
     * Выбирает случайное слово, согласующееся со всеми предыдущими ходами.
     * При пустой истории допускается любое слово из словаря.
     *
     * @param history история ходов с введёнными словами и подсказками
     * @return подходящее слово, ранее не использованное в истории
     * @throws NullPointerException если history равен null
     * @throws IllegalStateException если подходящих кандидатов не осталось
     */
    public String suggestWord(List<WordleGame.Turn> history) {

        Objects.requireNonNull(history, "history не должен быть null");

        List<String> candidates = new ArrayList<>();

        for (String candidate : dictionary.getWords()) {
            boolean suitable = true;

            for (WordleGame.Turn turn : history) {

                if (candidate.equals(turn.guessWord())) {
                    suitable = false;
                    break;
                }

                // Кандидат выступает предполагаемым ответом:
                // его подсказка для прошлого хода должна совпасть с сохранённой.
                String expectedFeedback =
                        matcher.match(candidate, turn.guessWord());

                if (!expectedFeedback.equals(turn.feedback())) {
                    suitable = false;
                    break;
                }
            }

            if (suitable) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException(
                    "Не найдено подходящих слов для подсказки"
            );
        }

        int index = ThreadLocalRandom.current()
                .nextInt(candidates.size());

        String suggestion = candidates.get(index);

        log.println("Подбор слова: найдено кандидатов: "
                + candidates.size() + ", предложено: " + suggestion);

        return suggestion;
    }
}
