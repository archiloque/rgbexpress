package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

final class LevelReader {

    @NotNull
    static Level readLevel(@NotNull Path path) throws IOException {
        Path elementsFile = getFile(path, "elements.txt");
        Path roadsFile = getFile(path, "roads.txt");

        List<String> elementsContent = Files.readAllLines(elementsFile);
        List<String> roadsContent = Files.readAllLines(roadsFile);

        if (elementsContent.size() != roadsContent.size()) {
            throw new IllegalArgumentException("Files have different sizes");
        }

        int height = elementsContent.size();
        int width = elementsContent.get(0).length();

        byte[] roads = new byte[height * width];
        byte[] elements = new byte[height * width];

        for (int lineIndex = 0; lineIndex < height; lineIndex++) {
            String elementsLine = elementsContent.get(lineIndex);
            checkSize(elementsFile.toAbsolutePath().toString(), lineIndex, elementsLine, width);
            String roadsLine = roadsContent.get(lineIndex);
            checkSize(roadsFile.toAbsolutePath().toString(), lineIndex, roadsLine, width);
            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                int position = (lineIndex * width) + columnIndex;

                char roadChar = roadsLine.charAt(columnIndex);
                checkIfIn(roadChar, RoadElement.ALL_ELEMENTS);
                roads[position] = RoadElement.CHAR_TO_BYTE.get(roadChar);

                char elementChar = elementsLine.charAt(columnIndex);
                if (Arrays.binarySearch(MapElement.ALL_ELEMENTS, elementChar) >= 0) {
                    elements[position] = MapElement.CHAR_TO_BYTE.get(elementChar);
                    ;
                } else {
                    checkIfIn(elementChar, RoadElement.ALL_ELEMENTS);
                }
            }
        }
        Level level = new Level(height, width, roads, elements);
        return level;
    }

    private static @NotNull Path getFile(@NotNull Path directory, @NotNull String fileName) throws FileNotFoundException {
        Path file = directory.resolve(fileName);
        if (!Files.exists(file)) {
            throw new FileNotFoundException(file.toAbsolutePath().toString());
        }
        return file;
    }

    private static void checkIfIn(char content, @NotNull char[] possibleContent) throws IllegalArgumentException {
        if (Arrays.binarySearch(possibleContent, content) < 0) {
            throw new IllegalArgumentException("Unknown value [" + content + "]");
        }
    }

    private static void checkSize(@NotNull String file, int line, @NotNull String content, int size) throws IllegalArgumentException {
        if (content.length() != size) {
            throw new IllegalArgumentException("[" + content + "] is not " + size + " characters long at line " + line + " of " + file);
        }
    }

}
