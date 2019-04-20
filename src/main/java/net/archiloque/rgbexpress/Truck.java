package net.archiloque.rgbexpress;

import org.jetbrains.annotations.Nullable;

final class Truck {

    static final byte STATUS_NOT_STARTED = 0;
    static final byte STATUS_DRIVING = 1;
    static final byte STATUS_STOPPED = 2;

    final @Nullable IntegerListElement previousPositions;

    final int currentPosition;

    final byte type;

    byte status;

    @Nullable ByteListElement cargo;

    Truck(int currentPosition,
          byte type,
          byte status,
          @Nullable ByteListElement cargo,
          @Nullable IntegerListElement previousPositions) {
        this.currentPosition = currentPosition;
        this.type = type;
        this.status = status;
        this.cargo = cargo;
        this.previousPositions = previousPositions;
    }

}
