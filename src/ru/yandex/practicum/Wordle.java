package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Точка входа в консольную игру Wordle.
 * Управляет взаимодействием с пользователем, запуском матча и журналированием.
 */
public class Wordle {

    private static final Path DICTIONARY_FILE = Path.of("words_ru.txt");
    private static final Path LOG_FILE = Path.of("wordle.log");

    public static void main(String[] args) {

        try (PrintWriter log = new PrintWriter(
                Files.newBufferedWriter(LOG_FILE, StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load(DICTIONARY_FILE.toString());

                WordMatcher matcher = new WordMatcher();
                WordleGame game = new WordleGame(dictionary, matcher, log);
                WordleSolver solver = new WordleSolver(dictionary, matcher, log);

                log.println("Компоненты игры успешно инициализированы");

                System.out.println("Добро пожаловать в Wordle!");
                System.out.println("Угадайте слово из пяти букв за шесть попыток.");
                System.out.println("Нажмите Enter для автоматического подбора слова.");

                while (!game.isFinished()) {

                    System.out.println(
                            "Осталось попыток: " + game.getRemainingAttempts()
                    );

                    System.out.print("Введите слово: ");

                    // При завершении потока ввода прерываем матч без объявления победы или поражения.
                    if (!scanner.hasNextLine()) {
                        log.println("Консольный ввод завершён");
                        System.out.println("Ввод завершён. Игра остановлена.");
                        return;
                    }

                    String inputWord = scanner.nextLine();

                    try {
                        // Пустой ввод передаёт выбор следующего слова компьютеру.
                        if (inputWord.isEmpty()) {
                            inputWord = solver.suggestWord(game.getHistory());
                            System.out.println("Предложенное слово: " + inputWord);
                        }

                        WordleGame.Turn turn = game.makeMove(inputWord);

                        System.out.println("> " + turn.guessWord());
                        System.out.println("> " + turn.feedback());

                    } catch (InvalidWordException | WordNotFoundInDictionary e) {
                        // Ошибки пользовательского ввода не должны завершать игровой цикл.
                        System.out.println(e.getMessage());
                    }
                }

                if (game.getStatus() == WordleGame.GameStatus.WON) {
                    System.out.println("Поздравляем! Вы угадали слово.");
                } else {
                    System.out.println("Попытки закончились. Вы проиграли.");
                }

                System.out.println("Загаданное слово: " + game.revealAnswer());

                log.println("Матч завершён. Состояние: " + game.getStatus());

            } catch (Exception e) {
                log.println("Ошибка при выполнении приложения");
                e.printStackTrace(log);
                log.flush();

                System.out.println("Ошибка приложения. Подробности в журнале.");
            }

        } catch (IOException e) {
            System.err.println("Не удалось создать журнал: " + e.getMessage());
        }
    }
}
