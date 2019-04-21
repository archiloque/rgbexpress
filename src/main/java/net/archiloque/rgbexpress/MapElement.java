package net.archiloque.rgbexpress;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class MapElement {

    private MapElement() {
    }

    static final char EMPTY_CHAR = ' ';

    static final char RED_TRUCK_CHAR = 'ʀ';
    static final char RED_PACKAGE_CHAR = 'r';
    static final char RED_WAREHOUSE_CHAR = 'R';

    static final char GREEN_TRUCK_CHAR = 'ɢ';
    static final char GREEN_PACKAGE_CHAR = 'g';
    static final char GREEN_WAREHOUSE_CHAR = 'G';

    static final char BLUE_TRUCK_CHAR = 'ʙ';
    static final char BLUE_PACKAGE_CHAR = 'b';
    static final char BLUE_WAREHOUSE_CHAR = 'B';

    static final char YELLOW_TRUCK_CHAR = 'ʏ';
    static final char YELLOW_PACKAGE_CHAR = 'y';
    static final char YELLOW_WAREHOUSE_CHAR = 'Y';

    static final char WHITE_TRUCK_CHAR = 'ᴡ';

    static final char SWITCH_1_BUTTON_CHAR = 'ᴜ';
    static final char SWITCH_1_ROAD_OPEN_CHAR = 'u';
    static final char SWITCH_1_ROAD_CLOSED_CHAR = 'U';

    static final char SWITCH_2_BUTTON_CHAR = 'ᴠ';
    static final char SWITCH_2_ROAD_OPEN_CHAR = 'v';
    static final char SWITCH_2_ROAD_CLOSED_CHAR = 'V';

    static final char SWITCH_3_BUTTON_CHAR = 'ᴢ';
    static final char SWITCH_3_ROAD_OPEN_CHAR = 'z';
    static final char SWITCH_3_ROAD_CLOSED_CHAR = 'Z';

    static final byte EMPTY = 0;

    static final byte RED_TRUCK = EMPTY + 1;
    static final byte RED_PACKAGE = RED_TRUCK + 1;
    static final byte RED_WAREHOUSE = RED_PACKAGE + 1;

    static final byte GREEN_TRUCK = RED_WAREHOUSE + 1;
    static final byte GREEN_PACKAGE = GREEN_TRUCK + 1;
    static final byte GREEN_WAREHOUSE = GREEN_PACKAGE + 1;

    static final byte BLUE_TRUCK = GREEN_WAREHOUSE + 1;
    static final byte BLUE_PACKAGE = BLUE_TRUCK + 1;
    static final byte BLUE_WAREHOUSE = BLUE_PACKAGE + 1;

    static final byte YELLOW_TRUCK = BLUE_WAREHOUSE + 1;
    static final byte YELLOW_PACKAGE = YELLOW_TRUCK + 1;
    static final byte YELLOW_WAREHOUSE = YELLOW_PACKAGE + 1;

    static final byte WHITE_TRUCK = YELLOW_WAREHOUSE + 1;

    static final byte SWITCH_1_BUTTON = WHITE_TRUCK + 1;
    static final byte SWITCH_1_ROAD_OPEN = SWITCH_1_BUTTON + 1;
    static final byte SWITCH_1_ROAD_CLOSED = SWITCH_1_ROAD_OPEN + 1;

    static final byte SWITCH_2_BUTTON = SWITCH_1_ROAD_CLOSED + 1;
    static final byte SWITCH_2_ROAD_OPEN = SWITCH_2_BUTTON + 1;
    static final byte SWITCH_2_ROAD_CLOSED = SWITCH_2_ROAD_OPEN + 1;

    static final byte SWITCH_3_BUTTON = SWITCH_2_ROAD_CLOSED + 1;
    static final byte SWITCH_3_ROAD_OPEN = SWITCH_3_BUTTON + 1;
    static final byte SWITCH_3_ROAD_CLOSED = SWITCH_3_ROAD_OPEN + 1;

    static final int NUMBER_OF_SWITCH_TYPES = 3;

    static final char[] ELEMENTS_IN_MAP = new char[]{
            EMPTY_CHAR,

            RED_TRUCK_CHAR,
            RED_PACKAGE_CHAR,
            RED_WAREHOUSE_CHAR,

            GREEN_TRUCK_CHAR,
            GREEN_PACKAGE_CHAR,
            GREEN_WAREHOUSE_CHAR,

            BLUE_TRUCK_CHAR,
            BLUE_PACKAGE_CHAR,
            BLUE_WAREHOUSE_CHAR,

            YELLOW_TRUCK_CHAR,
            YELLOW_PACKAGE_CHAR,
            YELLOW_WAREHOUSE_CHAR,

            WHITE_TRUCK_CHAR,

            SWITCH_1_BUTTON_CHAR,
            SWITCH_1_ROAD_OPEN_CHAR,
            SWITCH_1_ROAD_CLOSED_CHAR,

            SWITCH_2_BUTTON_CHAR,
            SWITCH_2_ROAD_OPEN_CHAR,
            SWITCH_2_ROAD_CLOSED_CHAR,

            SWITCH_3_BUTTON_CHAR,
            SWITCH_3_ROAD_OPEN_CHAR,
            SWITCH_3_ROAD_CLOSED_CHAR,
    };

    static final Map<Character, Byte> CHAR_TO_BYTE = new HashMap<>(ELEMENTS_IN_MAP.length);

    static final byte[] TRUCKS = new byte[]{
            RED_TRUCK,
            GREEN_TRUCK,
            BLUE_TRUCK,
            YELLOW_TRUCK,
            WHITE_TRUCK,
    };

    static final byte[] PACKAGES = new byte[]{
            RED_PACKAGE,
            GREEN_PACKAGE,
            BLUE_PACKAGE,
            YELLOW_PACKAGE,
    };

    static final byte[] WAREHOUSES = new byte[]{
            RED_WAREHOUSE,
            GREEN_WAREHOUSE,
            BLUE_WAREHOUSE,
            YELLOW_WAREHOUSE,
    };

    static final byte[] SWITCHES_BUTTONS = new byte[]{
            SWITCH_1_BUTTON,
            SWITCH_2_BUTTON,
            SWITCH_3_BUTTON,
    };

    static final byte[] SWITCHES_ROAD_OPEN = new byte[]{
            SWITCH_1_ROAD_OPEN,
            SWITCH_2_ROAD_OPEN,
            SWITCH_3_ROAD_OPEN,
    };

    static final byte[] SWITCHES_ROAD_CLOSED = new byte[]{
            SWITCH_1_ROAD_CLOSED,
            SWITCH_2_ROAD_CLOSED,
            SWITCH_3_ROAD_CLOSED,
    };

    static final Map<Byte, Byte> WAREHOUSE_TO_PACKAGES = new HashMap<>(WAREHOUSES.length);
    static final boolean[][] CAN_PICK = new boolean[ELEMENTS_IN_MAP.length][];

    static {
        for (byte i = 0; i < ELEMENTS_IN_MAP.length; i++) {
            char c = ELEMENTS_IN_MAP[i];
            CHAR_TO_BYTE.put(c, i);
        }

        WAREHOUSE_TO_PACKAGES.put(RED_WAREHOUSE, RED_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(BLUE_WAREHOUSE, BLUE_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(GREEN_WAREHOUSE, GREEN_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(YELLOW_WAREHOUSE, YELLOW_PACKAGE);

        Arrays.sort(ELEMENTS_IN_MAP);
        Arrays.sort(TRUCKS);
        Arrays.sort(PACKAGES);
        Arrays.sort(WAREHOUSES);

        CAN_PICK[RED_TRUCK] = new boolean[ELEMENTS_IN_MAP.length];
        CAN_PICK[RED_TRUCK][RED_PACKAGE] = true;

        CAN_PICK[BLUE_TRUCK] = new boolean[ELEMENTS_IN_MAP.length];
        CAN_PICK[BLUE_TRUCK][BLUE_PACKAGE] = true;

        CAN_PICK[GREEN_TRUCK] = new boolean[ELEMENTS_IN_MAP.length];
        CAN_PICK[GREEN_TRUCK][GREEN_PACKAGE] = true;

        CAN_PICK[YELLOW_TRUCK] = new boolean[ELEMENTS_IN_MAP.length];
        CAN_PICK[YELLOW_TRUCK][YELLOW_PACKAGE] = true;

        CAN_PICK[WHITE_TRUCK] = new boolean[ELEMENTS_IN_MAP.length];
        CAN_PICK[WHITE_TRUCK][RED_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][BLUE_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][GREEN_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][YELLOW_PACKAGE] = true;
    }

}
