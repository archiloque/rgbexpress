package net.archiloque.rgbexpress;

import org.jetbrains.annotations.Nullable;

final class ByteListElement {

    final byte element;

    final @Nullable ByteListElement previous;

    ByteListElement(byte element, @Nullable ByteListElement previous) {
        this.element = element;
        this.previous = previous;
    }
}
