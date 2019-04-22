package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A level.
 */
final class Level {

    private final int height;

    final int width;

    @NotNull
    final byte[] roads;

    @NotNull
    final byte[] pickMap;

    @NotNull
    final byte[] unloadMap;

    @NotNull
    final SwitchGroup[] switchGroups;

    @NotNull
    private final List<LevelTruck> levelTrucks = new ArrayList<>();

    private final int packagesToPick;

    final LinkedList<LevelState> states = new LinkedList<>();

    Level(int height, int width, @NotNull byte[] roads, @NotNull byte[] elements) {
        this.height = height;
        this.width = width;
        this.roads = roads;

        int levelSize = width * height;

        int packages = 0;
        int warehouses = 0;

        unloadMap = new byte[levelSize];
        Arrays.fill(unloadMap, MapElement.EMPTY);
        pickMap = new byte[levelSize];
        Arrays.fill(pickMap, MapElement.EMPTY);

        List<Integer>[] disabledSwitchPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];
        List<Integer>[] enabledSwitchPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];
        List<Integer>[] switchedRoadPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];
        for (int switchIndex = 0; switchIndex < MapElement.NUMBER_OF_SWITCH_TYPES; switchIndex++) {
            enabledSwitchPositionsList[switchIndex] = new ArrayList<>();
            disabledSwitchPositionsList[switchIndex] = new ArrayList<>();
            switchedRoadPositionsList[switchIndex] = new ArrayList<>();
        }

        for (int currentPosition = 0; currentPosition < height * width; currentPosition++) {
            byte currentElement = elements[currentPosition];
            if (Arrays.binarySearch(MapElement.TRUCKS, currentElement) >= 0) {
                isReachable(currentPosition);
                levelTrucks.add(new LevelTruck(currentPosition, currentElement));
            } else if (Arrays.binarySearch(MapElement.PACKAGES, currentElement) >= 0) {
                isReachable(currentPosition);
                pickMap[currentPosition] = currentElement;
                packages += 1;
            } else if (Arrays.binarySearch(MapElement.WAREHOUSES, currentElement) >= 0) {
                isReachable(currentPosition + width);
                unloadMap[currentPosition + width] = MapElement.WAREHOUSE_TO_PACKAGES.get(currentElement);
                warehouses += 1;

            } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_ENABLED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_ENABLED, currentElement);
                enabledSwitchPositionsList[switchIndex].add(currentPosition);
            } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_DISABLED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_DISABLED, currentElement);
                disabledSwitchPositionsList[switchIndex].add(currentPosition);
            } else if (Arrays.binarySearch(MapElement.SWITCHES_ROAD_OPEN, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_ROAD_OPEN, currentElement);
                switchedRoadPositionsList[switchIndex].add(currentPosition);
            } else if (Arrays.binarySearch(MapElement.SWITCHES_ROAD_CLOSED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_ROAD_CLOSED, currentElement);
                switchedRoadPositionsList[switchIndex].add(currentPosition);

                byte switchedRoad = RoadElement.SWITCHED_ELEMENT[roads[currentPosition]];
                if (switchedRoad == RoadElement.ERROR) {
                    throw new IllegalArgumentException("Switched road invalid");
                }
                roads[currentPosition] = switchedRoad;
            }
        }
        if (packages != warehouses) {
            throw new IllegalArgumentException("Found " + packages + " packages but " + warehouses + " warehouses");
        }

        switchGroups = new SwitchGroup[MapElement.NUMBER_OF_SWITCH_TYPES];
        for (int switchIndex = 0; switchIndex < MapElement.NUMBER_OF_SWITCH_TYPES; switchIndex++) {
            List<Integer> enabledSwitchPositions = enabledSwitchPositionsList[switchIndex];
            List<Integer> disabledSwitchPositions = disabledSwitchPositionsList[switchIndex];
            List<Integer> switchedRoadPositions = switchedRoadPositionsList[switchIndex];
            if(enabledSwitchPositions.isEmpty()) {
                if(! disabledSwitchPositions.isEmpty()) {
                    throw new IllegalArgumentException("Found a disabled switch but no enabled switch");
                } else if(! switchedRoadPositions.isEmpty()) {
                    throw new IllegalArgumentException("Found a switched road but no switch");
                }
            } else if(switchedRoadPositions.isEmpty()) {
                throw new IllegalArgumentException("Found a switch but no switched road ");
            }
            SwitchGroup switchGroup = new SwitchGroup(
                    listToPrimitiveIntArray(switchedRoadPositions),
                    listToPrimitiveIntArray(enabledSwitchPositions),
                    listToPrimitiveIntArray(disabledSwitchPositions));
            switchGroups[switchIndex] = switchGroup;
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
        byte[] switchMaps = new byte[width*height];
        Arrays.fill(switchMaps, (byte)-1);
        for (byte switchId = 0; switchId < MapElement.NUMBER_OF_SWITCH_TYPES; switchId++) {
            SwitchGroup switchGroup = switchGroups[switchId];
            for (int enabledSwitch : switchGroup.enabledSwitches) {
                switchMaps[enabledSwitch] = switchId;
            }
        }

        boolean[] previousSwitchState = new boolean[MapElement.NUMBER_OF_SWITCH_TYPES];
        Arrays.fill(previousSwitchState, true);
        LevelState levelState = new LevelState(
                this,
                packagesToPick,
                roads,
                pickMap,
                unloadMap,
                trucks,
                switchMaps,
                previousSwitchState);
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

    static @NotNull int[] listToPrimitiveIntArray(@NotNull List<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    static class SwitchGroup {

        @NotNull final int[] roads;

        @NotNull final int[] enabledSwitches;

        @NotNull final int[] disabledSwitches;

        SwitchGroup(@NotNull int[] roads, @NotNull int[] enabledSwitches, @NotNull int[] disabledSwitches) {
            this.roads = roads;
            this.enabledSwitches = enabledSwitches;
            this.disabledSwitches = disabledSwitches;
        }
    }
}
