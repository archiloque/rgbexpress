package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

abstract class AbstractLevelState {

    static final boolean LOG = "true".equals(System.getenv("LOG"));

    private static final int BYTE_MASK = 0xff;
    private static final int HALF_BYTE_MASK = 0xf;

    private static final int RED_BYTE_MASK = BYTE_MASK << MapElement.RED_SHIFT;
    private static final int GREEN_BYTE_MASK = BYTE_MASK << MapElement.GREEN_SHIFT;
    private static final int BLUE_BYTE_MASK = BYTE_MASK << MapElement.BLUE_SHIFT;
    private static final int YELLOW_BYTE_MASK = HALF_BYTE_MASK << MapElement.YELLOW_SHIFT;

    private static final int RED_ITEM_VALUE = 1 << MapElement.RED_SHIFT;
    private static final int GREEN_ITEM_VALUE = 1 << MapElement.GREEN_SHIFT;
    private static final int BLUE_ITEM_VALUE = 1 << MapElement.BLUE_SHIFT;
    private static final int YELLOW_ITEM_VALUE = 1 << MapElement.YELLOW_SHIFT;
    private static final int WHITE_ITEM_VALUE = RED_ITEM_VALUE + GREEN_ITEM_VALUE + BLUE_ITEM_VALUE + YELLOW_ITEM_VALUE;

    abstract @Nullable Truck[] processState() throws IOException;

    final boolean anyTruckHere(final @NotNull Truck[] trucks, final int position) {
        for (Truck truck : trucks) {
            if ((truck != null) && (truck.currentPosition == position)) {
                return true;
            }
        }
        return false;
    }

    final void log(int truckIndex, @NotNull String message) {
        char[] repeat = new char[truckIndex];
        Arrays.fill(repeat, ' ');
        System.out.println(String.valueOf(repeat) + message);
    }

    final void woops(Level level, int position) {
        Level.Coordinate coordinate = level.getCoordinate(position);
        throw new IllegalArgumentException("Woops for (" + coordinate.line + ", " + coordinate.column + ")");
    }

    final byte getElementInMap(int position, byte[] map, byte[] indexMap) {
        short index = indexMap[position];
        if (index == -1) {
            return MapElement.EMPTY;
        } else {
            return map[index];
        }
    }


    /**
     * If the cargo full ?
     *
     * @param cargo the cargo
     */
    final boolean cargoFull(int cargo) {
        return (cargo >> 16) != 0;
    }

    /**
     * If the cargo full ?
     *
     * @param cargo the cargo
     */
    final byte getLastPackage(int cargo) {
        return (byte) (cargo & BYTE_MASK);
    }

    final int unloadPackage(int cargo) {
        return cargo >> 8;
    }

    final int loadPackage(int cargo, byte pack) {
        return (cargo << 8) + pack;
    }

    final int removeOnePackage(int packages, byte packageType) {
        switch (packageType) {
            case MapElement.RED_PACKAGE:
                return packages - RED_ITEM_VALUE;
            case MapElement.GREEN_PACKAGE:
                return packages - GREEN_ITEM_VALUE;
            case MapElement.BLUE_PACKAGE:
                return packages - BLUE_ITEM_VALUE;
            case MapElement.YELLOW_PACKAGE:
                return packages - YELLOW_ITEM_VALUE;
            default:
                throw new IllegalArgumentException("" + packageType);
        }
    }

    final int addOneTruck(int trucks, byte truckType) {
        switch (truckType) {
            case MapElement.RED_TRUCK:
                return trucks + RED_ITEM_VALUE;
            case MapElement.GREEN_TRUCK:
                return trucks + GREEN_ITEM_VALUE;
            case MapElement.BLUE_TRUCK:
                return trucks + BLUE_ITEM_VALUE;
            case MapElement.YELLOW_TRUCK:
                return trucks + YELLOW_ITEM_VALUE;
            case MapElement.WHITE_TRUCK:
                return trucks + WHITE_ITEM_VALUE;
            default:
                throw new IllegalArgumentException("" + truckType);
        }
    }

    final boolean enoughTrucksForPackages(int trucks, int packages) {
        if (((packages & RED_BYTE_MASK) > 0) && ((trucks & RED_BYTE_MASK) == 0)) {
            return false;
        }
        if (((packages & GREEN_BYTE_MASK) > 0) && ((trucks & GREEN_BYTE_MASK) == 0)) {
            return false;
        }
        if (((packages & BLUE_BYTE_MASK) > 0) && ((trucks & BLUE_BYTE_MASK) == 0)) {
            return false;
        }
        if (((packages & YELLOW_BYTE_MASK) > 0) && ((trucks & YELLOW_BYTE_MASK) == 0)) {
            return false;
        }
        return true;
    }

}
