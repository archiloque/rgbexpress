package net.archiloque.rgbexpress;

import org.jetbrains.annotations.Nullable;

final class ByteListElement {

    final byte element;

    final int size;

    final @Nullable ByteListElement previous;

    ByteListElement(byte element, @Nullable ByteListElement previous) {
        this.element = element;
        this.previous = previous;
        this.size = (previous == null) ? 1 : (previous.size + 1);
    }
}
