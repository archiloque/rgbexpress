package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

abstract class AbstractLevelState {

    static final boolean LOG = "true".equals(System.getenv("LOG"));

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
        return (byte) (cargo & 0xff);
    }

    final int unloadPackage(int cargo) {
        return cargo >> 8;
    }

    final int loadPackage(int cargo, byte pack) {
        return (cargo << 8) + pack;
    }
}
