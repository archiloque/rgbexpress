package net.archiloque.rgbexpress;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class RoadElement {

    private RoadElement() {
    }

    // those we can find in a map
    private static final char EMPTY_CHAR = ' ';

    private static final char CROSSING_CHAR = '┼';

    private static final char LEFT_RIGHT_CHAR = '─';
    private static final char UP_DOWN_CHAR = '│';

    private static final char LEFT_UP_CHAR = '┘';
    private static final char RIGHT_UP_CHAR = '└';

    private static final char LEFT_DOWN_CHAR = '┐';
    private static final char RIGHT_DOWN_CHAR = '┌';

    private static final char LEFT_CHAR = '╴';
    private static final char RIGHT_CHAR = '╶';
    private static final char UP_CHAR = '╵';
    private static final char DOWN_CHAR = '╷';

    private static final char LEFT_UP_DOWN_CHAR = '┤';
    private static final char RIGHT_UP_DOWN_CHAR = '├';
    private static final char LEFT_RIGHT_UP_CHAR = '┴';
    private static final char LEFT_RIGHT_DOWN_CHAR = '┬';

    // those we can find in a map
    static final byte EMPTY = 0;

    private static final byte CROSSING = EMPTY + 1;

    private static final byte LEFT_RIGHT = CROSSING + 1;
    private static final byte UP_DOWN = LEFT_RIGHT + 1;

    private static final byte LEFT_UP = UP_DOWN + 1;
    private static final byte RIGHT_UP = LEFT_UP + 1;

    private static final byte LEFT_DOWN = RIGHT_UP + 1;
    private static final byte RIGHT_DOWN = LEFT_DOWN + 1;

    private static final byte LEFT = RIGHT_DOWN + 1;
    private static final byte RIGHT = LEFT + 1;
    private static final byte UP = RIGHT + 1;
    private static final byte DOWN = UP + 1;

    // those we only use internally
    private static final byte LEFT_UP_DOWN = DOWN + 1;
    private static final byte RIGHT_UP_DOWN = LEFT_UP_DOWN + 1;
    private static final byte LEFT_RIGHT_UP = RIGHT_UP_DOWN + 1;
    private static final byte LEFT_RIGHT_DOWN = LEFT_RIGHT_UP + 1;

    private static final byte ERROR = LEFT_RIGHT_DOWN + 1;

    static final char[] ALL_ELEMENTS = new char[]{
            EMPTY_CHAR,
            CROSSING_CHAR,
            LEFT_RIGHT_CHAR,
            UP_DOWN_CHAR,
            LEFT_UP_CHAR,
            RIGHT_UP_CHAR,
            LEFT_DOWN_CHAR,
            RIGHT_DOWN_CHAR,
            LEFT_CHAR,
            RIGHT_CHAR,
            UP_CHAR,
            DOWN_CHAR,
            LEFT_UP_DOWN_CHAR,
            RIGHT_UP_DOWN_CHAR,
            LEFT_RIGHT_UP_CHAR,
            LEFT_RIGHT_DOWN_CHAR,
    };

    static final boolean[][] CAN_GO = new boolean[][]{
            // UP
            {
                    false,// EMPTY
                    true,// CROSSING

                    false,//  LEFT_RIGHT
                    true,//  UP_DOWN

                    true,//  LEFT_UP
                    true,//  RIGHT_UP

                    false,//  LEFT_DOWN
                    false,//  RIGHT_DOWN

                    false,//  LEFT
                    false,//  RIGHT
                    true,//  UP
                    false,//  DOWN

                    true,//  LEFT_UP_DOWN
                    true,//  RIGHT_UP_DOWN
                    true,//  LEFT_RIGHT_UP
                    false,//  LEFT_RIGHT_DOWN
            },
            // DOWN
            {
                    false,// EMPTY
                    true,// CROSSING

                    false,//  LEFT_RIGHT
                    true,//  UP_DOWN

                    false,//  LEFT_UP
                    false,//  RIGHT_UP

                    true,//  LEFT_DOWN
                    true,//  RIGHT_DOWN

                    false,//  LEFT
                    false,//  RIGHT
                    false,//  UP
                    true,//  DOWN

                    true,//  LEFT_UP_DOWN
                    true,//  RIGHT_UP_DOWN
                    false,//  LEFT_RIGHT_UP
                    true,//  LEFT_RIGHT_DOWN
            },
            // LEFT
            {
                    false,// EMPTY
                    true,// CROSSING

                    true,//  LEFT_RIGHT
                    false,//  UP_DOWN

                    true,//  LEFT_UP
                    false,//  RIGHT_UP

                    true,//  LEFT_DOWN
                    false,//  RIGHT_DOWN

                    true,//  LEFT
                    false,//  RIGHT
                    false,//  UP
                    false,//  DOWN

                    true,//  LEFT_UP_DOWN
                    false,//  RIGHT_UP_DOWN
                    true,//  LEFT_RIGHT_UP
                    true,//  LEFT_RIGHT_DOWN
            },
            // RIGHT
            {
                    false,// EMPTY
                    true,// CROSSING

                    true,//  LEFT_RIGHT
                    false,//  UP_DOWN

                    false,//  LEFT_UP
                    true,//  RIGHT_UP

                    false,//  LEFT_DOWN
                    true,//  RIGHT_DOWN

                    false,//  LEFT
                    true,//  RIGHT
                    false,//  UP
                    false,//  DOWN

                    false,//  LEFT_UP_DOWN
                    true,//  RIGHT_UP_DOWN
                    true,//  LEFT_RIGHT_UP
                    true,//  LEFT_RIGHT_DOWN
            },
    };

    static final byte[][] REMOVE_DIRECTION = new byte[][]{
            // UP
            {
                    ERROR,// EMPTY
                    LEFT_RIGHT_DOWN,// CROSSING

                    ERROR,//  LEFT_RIGHT
                    DOWN,//  UP_DOWN

                    LEFT,//  LEFT_UP
                    RIGHT,//  RIGHT_UP

                    ERROR,//  LEFT_DOWN
                    ERROR,//  RIGHT_DOWN

                    ERROR,//  LEFT
                    ERROR,//  RIGHT
                    EMPTY,//  UP
                    ERROR,//  DOWN

                    LEFT_DOWN,//  LEFT_UP_DOWN
                    RIGHT_DOWN,//  RIGHT_UP_DOWN
                    LEFT_RIGHT,//  LEFT_RIGHT_UP
                    ERROR,//  LEFT_RIGHT_DOWN
            },
            // DOWN
            {
                    ERROR,// EMPTY
                    LEFT_RIGHT_UP,// CROSSING

                    ERROR,//  LEFT_RIGHT
                    UP,//  UP_DOWN

                    ERROR,//  LEFT_UP
                    ERROR,//  RIGHT_UP

                    LEFT,//  LEFT_DOWN
                    RIGHT,//  RIGHT_DOWN

                    ERROR,//  LEFT
                    ERROR,//  RIGHT
                    ERROR,//  UP
                    EMPTY,//  DOWN

                    LEFT_UP,//  LEFT_UP_DOWN
                    RIGHT_UP,//  RIGHT_UP_DOWN
                    ERROR,//  LEFT_RIGHT_UP
                    LEFT_RIGHT,//  LEFT_RIGHT_DOWN
            },
            // LEFT
            {
                    ERROR,// EMPTY
                    RIGHT_UP_DOWN,// CROSSING

                    RIGHT,//  LEFT_RIGHT
                    ERROR,//  UP_DOWN

                    UP,//  LEFT_UP
                    ERROR,//  RIGHT_UP

                    DOWN,//  LEFT_DOWN
                    ERROR,//  RIGHT_DOWN

                    EMPTY,//  LEFT
                    ERROR,//  RIGHT
                    ERROR,//  UP
                    ERROR,//  DOWN

                    UP_DOWN,//  LEFT_UP_DOWN
                    ERROR,//  RIGHT_UP_DOWN
                    RIGHT_UP,//  LEFT_RIGHT_UP
                    RIGHT_DOWN,//  LEFT_RIGHT_DOWN
            },
            // RIGHT
            {
                    ERROR,// EMPTY
                    LEFT_UP_DOWN,// CROSSING

                    LEFT,//  LEFT_RIGHT
                    ERROR,//  UP_DOWN

                    ERROR,//  LEFT_UP
                    UP,//  RIGHT_UP

                    ERROR,//  LEFT_DOWN
                    DOWN,//  RIGHT_DOWN

                    ERROR,//  LEFT
                    EMPTY,//  RIGHT
                    ERROR,//  UP
                    ERROR,//  DOWN

                    ERROR,//  LEFT_UP_DOWN
                    UP_DOWN,//  RIGHT_UP_DOWN
                    LEFT_UP,//  LEFT_RIGHT_UP
                    LEFT_DOWN,//  LEFT_RIGHT_DOWN
            },
    };

    static final Map<Character, Byte> CHAR_TO_BYTE = new HashMap<>(ALL_ELEMENTS.length);
    static final Map<Byte, Character> BYTE_TO_CHAR = new HashMap<>(ALL_ELEMENTS.length);

    static {
        for (byte i = 0; i < ALL_ELEMENTS.length; i++) {
            CHAR_TO_BYTE.put(ALL_ELEMENTS[i], i);
            BYTE_TO_CHAR.put(i, ALL_ELEMENTS[i]);
        }
        Arrays.sort(ALL_ELEMENTS);
    }

}
