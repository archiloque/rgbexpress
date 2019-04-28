package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A level.
 */
final class Level {

    int minimalNumberOfTurns = Integer.MAX_VALUE;

    AbstractLevelState bestSolutionLevelState;

    Truck[] bestSolutionTrucks;

    private final byte height;

    final byte width;

    final short size;

    final int numberOfTrucks;

    @NotNull
    final byte[] roadsMap;

    @NotNull
    private final byte[] initialRoadsSmallMap;

    @NotNull
    final byte[] roadsSmallMapIndexes;

    @NotNull
    final byte[] elements;

    @NotNull
    final byte[] pickMap;

    @NotNull
    final byte[] unloadMap;

    @NotNull
    final byte[] pickSmallMapIndexes;

    @NotNull
    private final byte[] initialPickSmallMap;

    @NotNull
    final byte[] unloadSmallMapIndexes;

    @NotNull
    private final byte[] initialUnloadSmallMap;

    @NotNull
    final SwitchGroup[] switchGroups;

    private int numberOfSwitches;

    @NotNull
    private List<LevelTruck> levelTrucks = new ArrayList<>();

    @NotNull
    byte[] trucksTypes;

    @NotNull
    final short[] bumpPositions;

    private final byte numberOfPackagesToPick;

    private final int numberOfPackagesToPickPerPackages;

    @NotNull
    final FiFoList<AbstractLevelState> states = new FiFoList<>();

    Level(byte height, byte width, @NotNull byte[] roadsMap, @NotNull byte[] elements) {
        this.height = height;
        this.width = width;
        this.roadsMap = roadsMap;
        this.elements = elements;

        size = (short) (width * height);

        byte packages = 0;
        byte warehouses = 0;
        int packagesToPickPerPackages = 0;

        unloadMap = new byte[size];
        Arrays.fill(unloadMap, MapElement.EMPTY);
        pickMap = new byte[size];
        Arrays.fill(pickMap, MapElement.EMPTY);

        List<Short> possiblePickMapList = new ArrayList<>();
        List<Short> possibleUnloadMapList = new ArrayList<>();
        List<Short> roadMapList = new ArrayList<>();

        List<Short> bumpList = new ArrayList<>();

        List<Short>[] disabledSwitchPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];
        List<Short>[] enabledSwitchPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];
        List<Short>[] switchedRoadPositionsList = new List[MapElement.NUMBER_OF_SWITCH_TYPES];

        for (int switchIndex = 0; switchIndex < MapElement.NUMBER_OF_SWITCH_TYPES; switchIndex++) {
            enabledSwitchPositionsList[switchIndex] = new ArrayList<>();
            disabledSwitchPositionsList[switchIndex] = new ArrayList<>();
            switchedRoadPositionsList[switchIndex] = new ArrayList<>();
        }

        for (short currentPosition = 0; currentPosition < height * width; currentPosition++) {
            byte currentRoad = roadsMap[currentPosition];
            if (currentRoad != RoadElement.EMPTY) {
                roadMapList.add(currentPosition);
            }

            byte currentElement = elements[currentPosition];
            if (Arrays.binarySearch(MapElement.TRUCKS, currentElement) >= 0) {
                isReachable(currentPosition);
                levelTrucks.add(new LevelTruck(currentPosition, currentElement));
            } else if (Arrays.binarySearch(MapElement.PACKAGES, currentElement) >= 0) {
                isReachable(currentPosition);
                pickMap[currentPosition] = currentElement;
                packages += 1;
                possiblePickMapList.add(currentPosition);
                packagesToPickPerPackages += (1 << MapElement.SHIFT_PER_PACKAGE[currentElement]);
            } else if (Arrays.binarySearch(MapElement.WAREHOUSES, currentElement) >= 0) {
                isReachable(currentPosition + width);
                unloadMap[currentPosition + width] = MapElement.WAREHOUSE_TO_PACKAGES.get(currentElement);
                warehouses += 1;
                possibleUnloadMapList.add((short) (currentPosition + width));
            } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_ENABLED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_ENABLED, currentElement);
                enabledSwitchPositionsList[switchIndex].add(currentPosition);
                numberOfSwitches += 1;
            } else if (Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_DISABLED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_BUTTONS_DISABLED, currentElement);
                disabledSwitchPositionsList[switchIndex].add(currentPosition);
                numberOfSwitches += 1;
            } else if (Arrays.binarySearch(MapElement.SWITCHES_ROAD_OPEN, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_ROAD_OPEN, currentElement);
                switchedRoadPositionsList[switchIndex].add(currentPosition);
            } else if (Arrays.binarySearch(MapElement.SWITCHES_ROAD_CLOSED, currentElement) >= 0) {
                int switchIndex = Arrays.binarySearch(MapElement.SWITCHES_ROAD_CLOSED, currentElement);
                switchedRoadPositionsList[switchIndex].add(currentPosition);

                byte switchedRoad = RoadElement.SWITCHED_ELEMENT[roadsMap[currentPosition]];
                if (switchedRoad == RoadElement.ERROR) {
                    throw new IllegalArgumentException("Switched road invalid");
                }
                roadsMap[currentPosition] = switchedRoad;
            } else if (currentElement == MapElement.BUMP) {
                bumpList.add(currentPosition);
                possiblePickMapList.add(currentPosition);
                possibleUnloadMapList.add(currentPosition);
            }
        }

        if (packages != warehouses) {
            throw new IllegalArgumentException("Found " + packages + " packages but " + warehouses + " warehouses");
        }

        numberOfTrucks = levelTrucks.size();
        trucksTypes = new byte[numberOfTrucks];
        for (int truckIndex = 0; truckIndex < numberOfTrucks; truckIndex++) {
            trucksTypes[truckIndex] = levelTrucks.get(truckIndex).type;
        }

        bumpPositions = listToPrimitiveShortArray(bumpList);

        // dealing with switches
        switchGroups = new SwitchGroup[MapElement.NUMBER_OF_SWITCH_TYPES];
        for (int switchIndex = 0; switchIndex < MapElement.NUMBER_OF_SWITCH_TYPES; switchIndex++) {
            List<Short> enabledSwitchPositions = enabledSwitchPositionsList[switchIndex];
            List<Short> disabledSwitchPositions = disabledSwitchPositionsList[switchIndex];
            List<Short> switchedRoadPositions = switchedRoadPositionsList[switchIndex];
            if (enabledSwitchPositions.isEmpty()) {
                if (!disabledSwitchPositions.isEmpty()) {
                    throw new IllegalArgumentException("Found a disabled switch but no enabled switch");
                } else if (!switchedRoadPositions.isEmpty()) {
                    throw new IllegalArgumentException("Found a switched road but no switch");
                }
            } else if (switchedRoadPositions.isEmpty()) {
                throw new IllegalArgumentException("Found a switch but no switched road ");
            }
            SwitchGroup switchGroup = new SwitchGroup(
                    listToPrimitiveShortArray(switchedRoadPositions),
                    listToPrimitiveShortArray(enabledSwitchPositions),
                    listToPrimitiveShortArray(disabledSwitchPositions));
            switchGroups[switchIndex] = switchGroup;
        }

        numberOfPackagesToPick = packages;
        numberOfPackagesToPickPerPackages = packagesToPickPerPackages;

        pickSmallMapIndexes = new byte[size];
        initialPickSmallMap = new byte[possiblePickMapList.size()];
        createSmallMap(possiblePickMapList, pickSmallMapIndexes, initialPickSmallMap, pickMap);

        unloadSmallMapIndexes = new byte[size];
        initialUnloadSmallMap = new byte[possibleUnloadMapList.size()];
        createSmallMap(possibleUnloadMapList, unloadSmallMapIndexes, initialUnloadSmallMap, unloadMap);


        roadsSmallMapIndexes = new byte[size];
        initialRoadsSmallMap = new byte[roadMapList.size()];
        createSmallMap(roadMapList, roadsSmallMapIndexes, initialRoadsSmallMap, roadsMap);

    }

    private void createSmallMap(
            @NotNull List<Short> possibleMapList,
            @NotNull byte[] smallMapIndexes,
            @NotNull byte[] smallMap,
            @NotNull byte[] fullMap) {
        Arrays.fill(smallMapIndexes, (byte) -1);
        byte currentIndexPick = 0;
        for (Short position : possibleMapList) {
            smallMap[currentIndexPick] = fullMap[position];
            smallMapIndexes[position] = currentIndexPick;
            currentIndexPick += 1;
        }
    }

    private void isReachable(int position) {
        if (roadsMap[position] == RoadElement.EMPTY) {
            Coordinate positionCoordinates = getCoordinate(position);
            throw new IllegalArgumentException("Element at (" + positionCoordinates.line + ", " + positionCoordinates.column + ") is not reachable");
        }
    }

    void createInitStates() {
        Truck[] trucks = new Truck[levelTrucks.size()];
        for (int i = 0; i < levelTrucks.size(); i++) {
            LevelTruck levelTruck = levelTrucks.get(i);
            trucks[i] = new Truck(
                    levelTruck.position,
                    false,
                    0,
                    new byte[0]);
        }

        if ((numberOfSwitches == 0) && (bumpPositions.length == 0)) {
            LevelState levelState = new LevelState(
                    this,
                    0,
                    numberOfPackagesToPickPerPackages,
                    initialRoadsSmallMap,
                    initialPickSmallMap,
                    initialUnloadSmallMap,
                    trucks
            );
            states.add(levelState);
            return;
        }

        byte[] switchMaps = new byte[size];
        Arrays.fill(switchMaps, (byte) -1);
        for (byte switchId = 0; switchId < MapElement.NUMBER_OF_SWITCH_TYPES; switchId++) {
            SwitchGroup switchGroup = switchGroups[switchId];
            for (int enabledSwitch : switchGroup.enabledSwitches) {
                switchMaps[enabledSwitch] = switchId;
            }
        }

        boolean[] previousSwitchState = new boolean[MapElement.NUMBER_OF_SWITCH_TYPES];
        Arrays.fill(previousSwitchState, true);
        if (bumpPositions.length == 0) {
            boolean[] bumpsMap = new boolean[size];
            Arrays.fill(bumpsMap, false);

            FullLevelState levelState = new FullLevelState(
                    this,
                    0,
                    bumpsMap,
                    numberOfPackagesToPickPerPackages,
                    initialRoadsSmallMap,
                    initialPickSmallMap,
                    initialUnloadSmallMap,
                    bumpsMap,
                    trucks,
                    switchMaps,
                    previousSwitchState);
            states.add(levelState);
        } else {
            int numberOfBumps = bumpPositions.length;
            int numberOfPossibilities = (int) Math.pow(2, numberOfBumps);
            for (short bumpCode = 0; bumpCode <= numberOfPossibilities; bumpCode++) {
                boolean[] bumpsMap = new boolean[size];
                Arrays.fill(bumpsMap, false);
                String bumpBinarystring = Integer.toBinaryString(bumpCode);
                bumpBinarystring = String.format("%0" + numberOfBumps + "d", Integer.parseInt(bumpBinarystring));
                for (int bumpIndex = 0; bumpIndex < numberOfBumps; bumpIndex++) {
                    if (bumpBinarystring.charAt(bumpIndex) == '1') {
                        bumpsMap[bumpPositions[bumpIndex]] = true;
                    }
                }
                FullLevelState levelState = new FullLevelState(
                        this,
                        0,
                        bumpsMap,
                        numberOfPackagesToPickPerPackages,
                        initialRoadsSmallMap,
                        initialPickSmallMap,
                        initialUnloadSmallMap,
                        bumpsMap,
                        trucks,
                        switchMaps,
                        previousSwitchState);
                states.add(levelState);
            }
        }
    }

    boolean solve() {
        while (!states.isEmpty()) {
            states.pop().processState();
        }

        return false;
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

    @NotNull Coordinate getCoordinate(int position) {
        int line = position / width;
        int column = position % width;
        return new Coordinate(line, column);
    }

    private static final class LevelTruck {

        private final short position;
        private final byte type;

        private LevelTruck(short position, byte type) {
            this.position = position;
            this.type = type;
        }
    }

    private static @NotNull int[] listToPrimitiveIntArray(@NotNull List<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private static @NotNull short[] listToPrimitiveShortArray(@NotNull List<Short> list) {
        short[] result = new short[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    static final class SwitchGroup {

        @NotNull
        final short[] roads;

        @NotNull
        final short[] enabledSwitches;

        @NotNull
        final short[] disabledSwitches;

        SwitchGroup(@NotNull short[] roads, @NotNull short[] enabledSwitches, @NotNull short[] disabledSwitches) {
            this.roads = roads;
            this.enabledSwitches = enabledSwitches;
            this.disabledSwitches = disabledSwitches;
        }
    }

    static final class Coordinate {

        final int line;
        final int column;

        Coordinate(int line, int column) {
            this.line = line;
            this.column = column;
        }
    }
}
