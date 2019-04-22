package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Entry point
 */
public final class App {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

    public static void main(@NotNull String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No level specified, wil process all levels");
            processAllLevels();
        } else {
            processLevel(Path.of(args[0]));
        }
    }

    private static void processLevel(@NotNull Path path) throws IOException {
        print(path, "Reading level");
        Level level = LevelReader.readLevel(path);
        Path solutionFile = path.resolve("solution.txt");
        if (Files.exists(solutionFile)) {
            Files.delete(solutionFile);
        }

        print(path, "Solving level");
        long startTime = System.nanoTime();
        level.states.add(level.createLevelState());
        Truck[] solution = null;
        while ((solution == null) && (!level.states.isEmpty())) {
            LevelState nextCandidate = level.states.pop();
            solution = nextCandidate.processState();
            if (solution != null) {
                long stopTime = System.nanoTime();
                print(path, "Solved in " + LocalTime.MIN.plusNanos((stopTime - startTime)).toString());
                printSolution(solutionFile, level, solution);
            }
        }
        if (solution == null) {
            long stopTime = System.nanoTime();
            print(path, "Failed in " + LocalTime.MIN.plusNanos((stopTime - startTime)).toString());
        }
    }

    private static void printSolution(@NotNull Path solutionFile, @NotNull Level level, @NotNull Truck[] solution) throws IOException {
        List<String> content = new ArrayList<>();
        for (Truck truck : solution) {
            List<Integer> moves = new ArrayList<>();
            moves.add(truck.currentPosition);
            IntegerListElement position = truck.previousPositions;
            while (position != null) {
                moves.add((position.element));
                position = position.previous;
            }
            Collections.reverse(moves);

            char[][] map = level.printMap(level.roads);
            int previousPosition = -1;
            for (Integer currentPosition : moves) {
                if (previousPosition != -1) {
                    int previousLine = previousPosition / level.width;
                    int previousColumn = previousPosition % level.width;
                    char direction = getDirection(level, previousPosition, currentPosition);
                    map[previousLine][previousColumn] = direction;
                }
                previousPosition = currentPosition;
            }
            int firstLine = moves.get(0) / level.width;
            int firstColumn = moves.get(0) % level.width;
            map[firstLine][firstColumn] = '□';

            int previousLine = previousPosition / level.width;
            int previousColumn = previousPosition % level.width;
            map[previousLine][previousColumn] = 'X';

            for (char[] mapLine : map) {
                content.add(new String(mapLine));
            }
            content.add("");

            previousPosition = -1;
            for (Integer currentPosition : moves) {
                int currentLine = currentPosition / level.width;
                int currentColumn = currentPosition % level.width;
                StringBuilder currentText = new StringBuilder();
                if (previousPosition == -1) {
                    currentText.append("Start at ");
                } else {
                    char direction = getDirection(level, previousPosition, currentPosition);
                    currentText.append(direction).append(" ");
                }
                currentText.append("(").append(currentLine).append(",").append(currentColumn).append(")");

                if (level.pickMap[currentPosition] != MapElement.EMPTY) {
                    currentText.append(" maybe pick");
                } else if (level.unloadMap[currentPosition] != MapElement.EMPTY) {
                    currentText.append(" unload");
                } else if (level.switchMap[currentPosition] != null) {
                    currentText.append(" switch");
                }
                previousPosition = currentPosition;
                content.add(currentText.toString());
            }
            content.add("");
        }
        Files.write(solutionFile, content);
    }

    private static char getDirection(@NotNull Level level, int previousPosition, int currentPosition) {
        byte direction;
        if ((previousPosition + 1) == currentPosition) {
            direction = Direction.RIGHT;
        } else if ((previousPosition - 1) == currentPosition) {
            direction = Direction.LEFT;
        } else if ((previousPosition - level.width) == currentPosition) {
            direction = Direction.UP;
        } else if ((previousPosition + level.width) == currentPosition) {
            direction = Direction.DOWN;
        } else {
            throw new IllegalArgumentException("");
        }
        return Direction.AS_CHAR[direction];
    }

    private static void processAllLevels() throws IOException {
        File rootDir = new File("levels");
        try (final Stream<Path> files = Files.walk(rootDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            files.
                    filter(path -> path.getFileName().equals(Path.of("elements.txt"))).
                    forEach(path -> {
                        try {
                            processLevel(path.getParent());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private static void print(@NotNull Path path, @NotNull String message) {
        System.out.println(DATE_FORMAT.format(new Date()) + " " + path.toAbsolutePath() + " " + message);
    }

}
