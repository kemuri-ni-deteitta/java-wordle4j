package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleGame game;

    @BeforeEach
    void setUp() {
        PrintWriter log = new PrintWriter(System.out);

        WordleDictionary dictionary = new WordleDictionary(
                List.of("маска", "кошка", "парус"),
                log
        );

        WordMatcher matcher = new WordMatcher();

        game = new WordleGame(
                dictionary,
                matcher,
                log,
                "маска"
        );
    }

    @Test
    void shouldInitializeGame() {
        assertEquals(6, game.getRemainingAttempts());
        assertEquals(WordleGame.GameStatus.IN_PROGRESS, game.getStatus());
        assertTrue(game.getHistory().isEmpty());
        assertFalse(game.isFinished());
    }

    @Test
    void shouldProcessValidMove()
            throws InvalidWordException, WordNotFoundInDictionary {

        WordleGame.Turn turn = game.makeMove("кошка");

        assertEquals("кошка", turn.guessWord());
        assertEquals("---++", turn.feedback());

        assertEquals(5, game.getRemainingAttempts());
        assertEquals(1, game.getHistory().size());
        assertEquals(turn, game.getHistory().get(0));

        assertEquals(WordleGame.GameStatus.IN_PROGRESS, game.getStatus());
        assertFalse(game.isFinished());
    }

    @Test
    void shouldNormalizeInput()
            throws InvalidWordException, WordNotFoundInDictionary {

        WordleGame.Turn turn = game.makeMove("  КОШКА  ");

        assertEquals("кошка", turn.guessWord());
        assertEquals("---++", turn.feedback());
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void shouldRejectInvalidWord() {
        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove("кот")
        );

        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove("hello")
        );

        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove("12345")
        );

        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove(null)
        );
    }

    @Test
    void shouldRejectUnknownWord() {
        assertThrows(
                WordNotFoundInDictionary.class,
                () -> game.makeMove("театр")
        );
    }

    @Test
    void shouldNotDecreaseAttemptsOnInvalidInput() {
        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove("12345")
        );

        assertThrows(
                WordNotFoundInDictionary.class,
                () -> game.makeMove("театр")
        );

        assertEquals(6, game.getRemainingAttempts());
        assertTrue(game.getHistory().isEmpty());
        assertEquals(WordleGame.GameStatus.IN_PROGRESS, game.getStatus());
        assertFalse(game.isFinished());
    }

    @Test
    void shouldWinWhenAnswerIsGuessed()
            throws InvalidWordException, WordNotFoundInDictionary {

        WordleGame.Turn turn = game.makeMove("маска");

        assertEquals("маска", turn.guessWord());
        assertEquals("+++++", turn.feedback());

        assertEquals(5, game.getRemainingAttempts());
        assertEquals(1, game.getHistory().size());

        assertEquals(WordleGame.GameStatus.WON, game.getStatus());
        assertTrue(game.isFinished());

        assertEquals("маска", game.revealAnswer());
    }

    @Test
    void shouldLoseAfterSixWrongMoves()
            throws InvalidWordException, WordNotFoundInDictionary {

        // Повтор одного слова допустим: каждый такой ход расходует попытку.
        for (int i = 0; i < 6; i++) {
            game.makeMove("кошка");
        }

        assertEquals(0, game.getRemainingAttempts());
        assertEquals(6, game.getHistory().size());

        assertEquals(WordleGame.GameStatus.LOST, game.getStatus());
        assertTrue(game.isFinished());

        assertEquals("маска", game.revealAnswer());
    }

    @Test
    void shouldRejectMovesAfterGameFinished()
            throws InvalidWordException, WordNotFoundInDictionary {

        game.makeMove("маска");

        assertThrows(
                IllegalStateException.class,
                () -> game.makeMove("кошка")
        );

        assertEquals(WordleGame.GameStatus.WON, game.getStatus());
        assertEquals(5, game.getRemainingAttempts());
        assertEquals(1, game.getHistory().size());
    }

    @Test
    void shouldReturnImmutableHistory()
            throws InvalidWordException, WordNotFoundInDictionary {

        WordleGame.Turn turn = game.makeMove("кошка");

        List<WordleGame.Turn> history = game.getHistory();

        assertEquals(1, history.size());
        assertEquals(turn, history.get(0));

        assertThrows(
                UnsupportedOperationException.class,
                () -> history.add(
                        new WordleGame.Turn("маска", "+++++")
                )
        );

        assertEquals(1, game.getHistory().size());
    }

    @Test
    void shouldNotRevealAnswerBeforeFinish() {
        assertThrows(
                IllegalStateException.class,
                () -> game.revealAnswer()
        );

        assertEquals(WordleGame.GameStatus.IN_PROGRESS, game.getStatus());
        assertFalse(game.isFinished());
    }
}
