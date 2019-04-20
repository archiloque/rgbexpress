package net.archiloque.rgbexpress;

import org.jetbrains.annotations.Nullable;

final class IntegerListElement {

    final int element;

    final @Nullable IntegerListElement previous;

    IntegerListElement(int element, @Nullable IntegerListElement previous) {
        this.element = element;
        this.previous = previous;
    }
}
