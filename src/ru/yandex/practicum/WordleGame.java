package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Управляет одним матчем Wordle: обрабатывает ходы, учитывает попытки,
 * сохраняет историю и определяет результат игры.
 * Сравнение слов делегируется {@link WordMatcher}.
 */
public class WordleGame {

    private static final int MAX_ATTEMPTS = 6;

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final WordMatcher matcher;
    private final PrintWriter log;

    private final List<Turn> history = new ArrayList<>();
    private GameStatus status;

    public enum GameStatus {
        IN_PROGRESS, WON, LOST
    }

    /**
     * Результат одного хода с нормализованным словом и подсказкой,
     * где «+» означает точное совпадение, «^» — другую позицию,
     * а «-» — отсутствие доступного совпадения.
     */
    public record Turn(String guessWord, String feedback) {
    }

    /**
     * Создаёт новый матч со случайным словом из словаря
     * и шестью доступными попытками.
     */
    public WordleGame(WordleDictionary dictionary,
                      WordMatcher matcher,
                      PrintWriter log) {

        this(
                dictionary,
                matcher,
                log,
                Objects.requireNonNull(dictionary).getRandomWord()
        );
    }

    /**
     * Создаёт матч с заданным словом для детерминированных сценариев.
     *
     * @param dictionary словарь допустимых слов
     * @param matcher алгоритм сравнения слов
     * @param log журнал работы игры
     * @param answer загаданное слово в нормализованном формате
     * @throws NullPointerException если какой-либо аргумент равен null
     * @throws IllegalArgumentException если ответ не соответствует
     *                                  формату или отсутствует в словаре
     */
    WordleGame(WordleDictionary dictionary,
               WordMatcher matcher,
               PrintWriter log,
               String answer) {

        this.dictionary = Objects.requireNonNull(dictionary);
        this.matcher = Objects.requireNonNull(matcher);
        this.log = Objects.requireNonNull(log);
        this.answer = Objects.requireNonNull(answer);

        if (!WordleDictionary.isValidWord(answer)
                || !dictionary.contains(answer)) {
            throw new IllegalArgumentException(
                    "Загаданное слово должно присутствовать в словаре"
            );
        }

        this.steps = MAX_ATTEMPTS;
        this.status = GameStatus.IN_PROGRESS;

        log.println("Создан новый матч. Попыток: " + steps);
    }

    /**
     * Обрабатывает ход и обновляет состояние матча.
     * Некорректный ввод не расходует попытку и не сохраняется в истории.
     *
     * @param inputWord введённое слово
     * @return результат хода с нормализованным словом и подсказкой
     * @throws InvalidWordException если слово имеет недопустимый формат
     * @throws WordNotFoundInDictionary если слово отсутствует в словаре
     * @throws IllegalStateException если матч уже завершён
     */
    public Turn makeMove(String inputWord) throws InvalidWordException, WordNotFoundInDictionary {

        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Матч уже завершён");
        }

        if (inputWord == null) {
            throw new InvalidWordException("Слово не может быть null");
        }

        String guessWord = WordleDictionary.normalize(inputWord);

        if (!WordleDictionary.isValidWord(guessWord)) {
            throw new InvalidWordException(
                    "Введите слово из пяти русских букв"
            );
        }

        if (!dictionary.contains(guessWord)) {
            throw new WordNotFoundInDictionary(guessWord);
        }

        String feedback = matcher.match(answer, guessWord);

        Turn turn = new Turn(guessWord, feedback);
        history.add(turn);

        steps--;

        // Победа имеет приоритет над исчерпанием попыток на последнем ходе.
        if (guessWord.equals(answer)) {
            status = GameStatus.WON;
        } else if (steps == 0) {
            status = GameStatus.LOST;
        }

        StringBuilder logMessage = new StringBuilder();

        logMessage.append("Ход: ").append(guessWord)
                .append(", результат: ").append(feedback)
                .append(", осталось попыток: ").append(steps)
                .append(", состояние: ").append(status);

        log.println(logMessage.toString());
        return turn;
    }

    public int getRemainingAttempts() {
        return steps;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean isFinished() {
        return status != GameStatus.IN_PROGRESS;
    }

    /**
     * Возвращает неизменяемую копию истории ходов
     * в порядке их выполнения.
     *
     * @return история принятых ходов
     */
    public List<Turn> getHistory() {
        return List.copyOf(history);
    }

    /**
     * Раскрывает загаданное слово только после завершения матча.
     *
     * @return загаданное слово
     * @throws IllegalStateException если матч ещё продолжается
     */
    public String revealAnswer() {
        if (!isFinished()) {
            throw new IllegalStateException("Матч ещё не завершён");
        }

        return answer;
    }
}
