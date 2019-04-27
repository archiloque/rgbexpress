package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;

final class Truck {

    final boolean stopped;

    final @NotNull byte[] previousPositions;

    final short currentPosition;

    final int cargo;

    Truck(short currentPosition,
          boolean stopped,
          int cargo,
          @NotNull byte[] previousPositions) {
        this.currentPosition = currentPosition;
        this.stopped = stopped;
        this.cargo = cargo;
        this.previousPositions = previousPositions;
    }

}
