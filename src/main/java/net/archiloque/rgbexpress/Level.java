package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A level.
 */
final class Level {

    final int height;

    final int width;

    @NotNull
    final byte[] roads;

    @NotNull
    private final byte[] elements;

    @NotNull
    final byte[] dumpMap;

    @NotNull
    final byte[] pickMap;

    @NotNull
    private final List<LevelTruck> levelTrucks = new ArrayList<>();

    private final int packagesToPick;

    final LinkedList<LevelState> states = new LinkedList<>();

    Level(int height, int width, @NotNull byte[] roads, @NotNull byte[] elements) {
        this.height = height;
        this.width = width;
        this.roads = roads;
        this.elements = elements;

        int packages = 0;
        int warehouses = 0;
        dumpMap = new byte[width * height];
        Arrays.fill(dumpMap, MapElement.EMPTY);
        pickMap = new byte[width * height];
        Arrays.fill(pickMap, MapElement.EMPTY);

        for (int position = 0; position < height * width; position++) {
            byte currentElement = elements[position];
            if (Arrays.binarySearch(MapElement.TRUCKS, currentElement) >= 0) {
                isReachable(position);
                levelTrucks.add(new LevelTruck(position, currentElement));
            } else if (Arrays.binarySearch(MapElement.PACKAGES, currentElement) >= 0) {
                isReachable(position);
                pickMap[position] = currentElement;
                packages += 1;
            } else if (Arrays.binarySearch(MapElement.WAREHOUSES, currentElement) >= 0) {
                isReachable(position + width);
                dumpMap[position + width] = MapElement.WAREHOUSE_TO_PACKAGES.get(currentElement);
                warehouses += 1;
            }
        }
        if (packages != warehouses) {
            throw new IllegalArgumentException("Found " + packages + " packages but " + warehouses + " warehouses");
        }
        packagesToPick = packages;
    }

    private void isReachable(int position) {
        if (roads[position] == RoadElement.EMPTY) {
            throw new IllegalArgumentException("Element at (" + (position / width) + ", " + (position % width) + ") is not reachable");
        }
    }

    @NotNull LevelState createLevelState() {
        Truck[] trucks = new Truck[levelTrucks.size()];
        for (int i = 0; i < levelTrucks.size(); i++) {
            LevelTruck levelTruck = levelTrucks.get(i);
            trucks[i] = new Truck(
                    levelTruck.position,
                    levelTruck.type,
                    Truck.STATUS_NOT_STARTED,
                    null,
                    null);
        }
        LevelState levelState = new LevelState(
                this,
                packagesToPick,
                roads,
                pickMap,
                trucks);
        return levelState;
    }

    @NotNull char[][] printMap(@NotNull byte[] roads) {
        char[][] result = new char[height][];
        for (int line = 0; line < height; line++) {
            char[] content = new char[width];
            for (int column = 0; column < width; column++) {
                byte contentByte = roads[(line * width) + column];
                content[column] = RoadElement.BYTE_TO_CHAR.get(contentByte);
            }
            result[line] = content;
        }
        return result;
    }

    private static final class LevelTruck {

        private final int position;
        private final byte type;

        private LevelTruck(int position, byte type) {
            this.position = position;
            this.type = type;
        }
    }
}
