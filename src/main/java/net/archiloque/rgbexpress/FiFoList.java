package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FiFoList<E> {

    private @Nullable ListElement<E> current;

    FiFoList() {
    }

    void add(@NotNull E element) {
        current = new ListElement<>(element, current);
    }

    @Nullable E pop() {
        if (current != null) {
            E element = current.element;
            current = current.next;
            return element;
        } else {
            return null;
        }
    }

    boolean isEmpty() {
        return current == null;
    }

    private final static class ListElement<E> {

        private final @NotNull E element;

        private final @Nullable ListElement<E> next;

        ListElement(@NotNull E element, @Nullable ListElement<E> next) {
            this.element = element;
            this.next = next;
        }
    }

}
