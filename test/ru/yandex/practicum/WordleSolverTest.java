package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleSolverTest {

    private WordleDictionary dictionary;
    private WordMatcher matcher;
    private WordleSolver solver;

    @BeforeEach
    void setUp() {
        PrintWriter log = new PrintWriter(System.out);

        dictionary = new WordleDictionary(
                List.of(
                        "маска",
                        "банка",
                        "миска",
                        "кошка",
                        "парус",
                        "масса",
                        "шапка"
                ),
                log
        );

        matcher = new WordMatcher();
        solver = new WordleSolver(dictionary, matcher, log);
    }

    @Test
    void shouldSuggestWordWithoutHistory() {
        String suggestion = solver.suggestWord(List.of());

        assertTrue(dictionary.contains(suggestion));
    }

    @Test
    void shouldRespectPreviousFeedback() {
        List<WordleGame.Turn> history = List.of(
                new WordleGame.Turn("кошка", "---++")
        );

        String suggestion = solver.suggestWord(history);

        assertEquals(
                "---++",
                matcher.match(suggestion, "кошка")
        );
    }

    @Test
    void shouldRespectAllPreviousTurns() {
        List<WordleGame.Turn> history = List.of(
                new WordleGame.Turn("кошка", "---++"),
                new WordleGame.Turn("банка", "-+-++")
        );

        String suggestion = solver.suggestWord(history);

        assertEquals("маска", suggestion);
    }

    @Test
    void shouldNotSuggestAlreadyGuessedWord() {
        List<WordleGame.Turn> history = List.of(
                new WordleGame.Turn("кошка", "---++")
        );

        String suggestion = solver.suggestWord(history);

        assertNotEquals("кошка", suggestion);
        assertTrue(dictionary.contains(suggestion));
    }

    @Test
    void shouldHandleRepeatedLetters() {
        List<WordleGame.Turn> history = List.of(
                new WordleGame.Turn("масса", "+++-+")
        );

        String suggestion = solver.suggestWord(history);

        assertEquals("маска", suggestion);
    }

    @Test
    void shouldThrowExceptionWhenNoCandidates() {
        // Все слова тестового словаря содержат «а», поэтому подсказка «-----» невозможна.
        List<WordleGame.Turn> history = List.of(
                new WordleGame.Turn("кошка", "-----")
        );

        assertThrows(
                IllegalStateException.class,
                () -> solver.suggestWord(history)
        );
    }

    @Test
    void shouldRejectNullHistory() {
        assertThrows(
                NullPointerException.class,
                () -> solver.suggestWord(null)
        );
    }
}
