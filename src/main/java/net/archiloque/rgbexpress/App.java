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
import java.util.Arrays;
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
        level.createInitStates();
        Truck[] solution = null;
        while ((solution == null) && (!level.states.isEmpty())) {
            LevelState nextCandidate = level.states.pop();
            solution = nextCandidate.processState();
            if (solution != null) {
                long stopTime = System.nanoTime();
                print(path, "Solved in " + LocalTime.MIN.plusNanos((stopTime - startTime)).toString());
                printSolution(solutionFile, level, nextCandidate, solution);
            }
        }
        if (solution == null) {
            long stopTime = System.nanoTime();
            print(path, "Failed in " + LocalTime.MIN.plusNanos((stopTime - startTime)).toString());
        }
    }

    private static void printSolution(
            @NotNull Path solutionFile,
            @NotNull Level level,
            @NotNull LevelState levelState,
            @NotNull Truck[] solution) throws IOException {
        List<String> content = new ArrayList<>();

        if(level.bumpPositions.length != 0) {
            for (int bumpPosition : level.bumpPositions) {
                boolean bumpEnabled = levelState.initialBumpMap[bumpPosition];
                Level.Coordinate coordinate = level.getCoordinate(bumpPosition);
                content.add("Bump at (" + coordinate.line + ", " + coordinate.column + ") is " + (bumpEnabled ? "enabled" : "disabled"));
            }
            content.add("");
        }

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
                    Level.Coordinate previousCoordinates = level.getCoordinate(previousPosition);
                    char direction = getDirection(level, previousPosition, currentPosition);
                    map[previousCoordinates.line][previousCoordinates.column] = direction;
                }
                previousPosition = currentPosition;
            }
            Level.Coordinate firstPositionCoordinates = level.getCoordinate(moves.get(0));
            map[firstPositionCoordinates.line][firstPositionCoordinates.column] = '□';

            Level.Coordinate previousPositionCoordinates = level.getCoordinate(previousPosition);
            map[previousPositionCoordinates.line][previousPositionCoordinates.column] = 'X';

            for (char[] mapLine : map) {
                content.add(new String(mapLine));
            }
            content.add("");

            previousPosition = -1;
            for (Integer currentPosition : moves) {
                Level.Coordinate currentCoordinates = level.getCoordinate(currentPosition);
                StringBuilder currentText = new StringBuilder();
                if (previousPosition == -1) {
                    currentText.append("Start at ");
                } else {
                    char direction = getDirection(level, previousPosition, currentPosition);
                    currentText.append(direction).append(" ");
                }
                currentText.
                        append("(").
                        append(currentCoordinates.line).
                        append(",").
                        append(currentCoordinates.column).
                        append(")");

                if (previousPosition == -1) {
                    currentText.append(" (").append(MapElement.TRUCK_TO_NAME.get(truck.type)).append(" truck)");
                }
                if (level.pickMap[currentPosition] != MapElement.EMPTY) {
                    currentText.append(" maybe pick a ").append(MapElement.PACKAGE_TO_NAME.get(level.pickMap[currentPosition])).append(" package");
                } else if (level.unloadMap[currentPosition] != MapElement.EMPTY) {
                    currentText.append(" unload a ").append(MapElement.PACKAGE_TO_NAME.get(level.unloadMap[currentPosition])).append(" package");
                } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_ENABLED, level.elements[currentPosition]) >= 0) {
                    currentText.append(" click");
                } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_DISABLED, level.elements[currentPosition]) >= 0) {
                    currentText.append(" click");
                } else if(Arrays.binarySearch(level.bumpPositions, currentPosition) >= 0) {
                    currentText.append(" maybe bump a package or pick one");
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
