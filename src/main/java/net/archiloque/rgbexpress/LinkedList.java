package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LinkedList<E> {

    private @Nullable ListElement<E> current;

    private @Nullable ListElement<E> last;

    LinkedList() {
    }

    void add(@NotNull E element) {
        ListElement<E> newLast = new ListElement<>(element);
        if (last != null) {
            last.next = newLast;
        }
        if (current == null) {
            current = newLast;
        }
        last = newLast;
    }

    @Nullable E pop() {
        if (current != null) {
            E currentElement = current.element;
            current = current.next;
            if (current == null) {
                last = null;
            }
            return currentElement;
        } else {
            return null;
        }
    }

    boolean isEmpty() {
        return current == null;
    }

    private final static class ListElement<E> {

        private final @NotNull E element;

        private @Nullable ListElement<E> next;

        ListElement(@NotNull E element) {
            this.element = element;
        }
    }

}
