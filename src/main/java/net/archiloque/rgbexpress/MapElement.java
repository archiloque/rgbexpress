package net.archiloque.rgbexpress;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class MapElement {

    private MapElement() {
    }

    private static final char EMPTY_CHAR = ' ';

    private static final char RED_TRUCK_CHAR = 'ʀ';
    private static final char RED_PACKAGE_CHAR = 'r';
    private static final char RED_WAREHOUSE_CHAR = 'R';

    private static final char GREEN_TRUCK_CHAR = 'ɢ';
    private static final char GREEN_PACKAGE_CHAR = 'g';
    private static final char GREEN_WAREHOUSE_CHAR = 'G';

    private static final char BLUE_TRUCK_CHAR = 'ʙ';
    private static final char BLUE_PACKAGE_CHAR = 'b';
    private static final char BLUE_WAREHOUSE_CHAR = 'B';

    private static final char YELLOW_TRUCK_CHAR = 'ʏ';
    private static final char YELLOW_PACKAGE_CHAR = 'y';
    private static final char YELLOW_WAREHOUSE_CHAR = 'Y';

    private static final char WHITE_TRUCK_CHAR = 'ᴡ';

    private static final char SWITCH_1_BUTTON_ENABLED_CHAR = 'ᵘ';
    private static final char SWITCH_1_BUTTON_DISABLED_CHAR = 'ᵤ';
    private static final char SWITCH_1_ROAD_OPEN_CHAR = 'u';
    private static final char SWITCH_1_ROAD_CLOSED_CHAR = 'U';

    private static final char SWITCH_2_BUTTON_ENABLED_CHAR = 'ᵛ';
    private static final char SWITCH_2_BUTTON_DISABLED_CHAR = 'ᵥ';
    private static final char SWITCH_2_ROAD_OPEN_CHAR = 'v';
    private static final char SWITCH_2_ROAD_CLOSED_CHAR = 'V';

    private static final char SWITCH_3_BUTTON_ENABLED_CHAR = 'ᵒ';
    private static final char SWITCH_3_BUTTON_DISABLED_CHAR = 'ₒ';
    private static final char SWITCH_3_ROAD_OPEN_CHAR = 'o';
    private static final char SWITCH_3_ROAD_CLOSED_CHAR = 'O';

    private static final char SWITCH_4_BUTTON_ENABLED_CHAR = 'ⁱ';
    private static final char SWITCH_4_BUTTON_DISABLED_CHAR = 'ᵢ';
    private static final char SWITCH_4_ROAD_OPEN_CHAR = 'i';
    private static final char SWITCH_4_ROAD_CLOSED_CHAR = 'I';

    private static final char BUMP_CHAR = '○';

    static final byte EMPTY = 0;
    static final byte RED_PACKAGE = EMPTY + 1;
    static final byte GREEN_PACKAGE = RED_PACKAGE + 1;
    static final byte BLUE_PACKAGE = GREEN_PACKAGE + 1;
    static final byte YELLOW_PACKAGE = BLUE_PACKAGE + 1;

    static final byte RED_TRUCK = YELLOW_PACKAGE + 1;
    private static final byte RED_WAREHOUSE = RED_TRUCK + 1;

    static final byte GREEN_TRUCK = RED_WAREHOUSE + 1;
    private static final byte GREEN_WAREHOUSE = GREEN_TRUCK + 1;

    static final byte BLUE_TRUCK = GREEN_WAREHOUSE + 1;
    private static final byte BLUE_WAREHOUSE = BLUE_TRUCK + 1;

    static final byte YELLOW_TRUCK = BLUE_WAREHOUSE + 1;
    private static final byte YELLOW_WAREHOUSE = YELLOW_TRUCK + 1;

    static final byte WHITE_TRUCK = YELLOW_WAREHOUSE + 1;

    private static final byte SWITCH_1_BUTTON_ENABLED = WHITE_TRUCK + 1;
    private static final byte SWITCH_1_BUTTON_DISABLED = SWITCH_1_BUTTON_ENABLED + 1;
    private static final byte SWITCH_1_ROAD_OPEN = SWITCH_1_BUTTON_DISABLED + 1;
    private static final byte SWITCH_1_ROAD_CLOSED = SWITCH_1_ROAD_OPEN + 1;

    private static final byte SWITCH_2_BUTTON_ENABLED = SWITCH_1_ROAD_CLOSED + 1;
    private static final byte SWITCH_2_BUTTON_DISABLED = SWITCH_2_BUTTON_ENABLED + 1;
    private static final byte SWITCH_2_ROAD_OPEN = SWITCH_2_BUTTON_DISABLED + 1;
    private static final byte SWITCH_2_ROAD_CLOSED = SWITCH_2_ROAD_OPEN + 1;

    private static final byte SWITCH_3_BUTTON_ENABLED = SWITCH_2_ROAD_CLOSED + 1;
    private static final byte SWITCH_3_BUTTON_DISABLED = SWITCH_3_BUTTON_ENABLED + 1;
    private static final byte SWITCH_3_ROAD_OPEN = SWITCH_3_BUTTON_DISABLED + 1;
    private static final byte SWITCH_3_ROAD_CLOSED = SWITCH_3_ROAD_OPEN + 1;

    private static final byte SWITCH_4_BUTTON_ENABLED = SWITCH_3_ROAD_CLOSED + 1;
    private static final byte SWITCH_4_BUTTON_DISABLED = SWITCH_4_BUTTON_ENABLED + 1;
    private static final byte SWITCH_4_ROAD_OPEN = SWITCH_4_BUTTON_DISABLED + 1;
    private static final byte SWITCH_4_ROAD_CLOSED = SWITCH_4_ROAD_OPEN + 1;

    static final byte BUMP = SWITCH_4_ROAD_CLOSED + 1;

    static final int NUMBER_OF_SWITCH_TYPES = 4;

    static final byte BYTE_SIZE = 8;
    static final byte RED_SHIFT = 0;
    static final byte GREEN_SHIFT = RED_SHIFT + BYTE_SIZE;
    static final byte BLUE_SHIFT = GREEN_SHIFT + BYTE_SIZE;
    static final byte YELLOW_SHIFT = BLUE_SHIFT + BYTE_SIZE;

    static final char[] ALL_ELEMENTS = new char[]{
            EMPTY_CHAR,

            RED_PACKAGE_CHAR,
            GREEN_PACKAGE_CHAR,
            BLUE_PACKAGE_CHAR,
            YELLOW_PACKAGE_CHAR,

            RED_TRUCK_CHAR,
            RED_WAREHOUSE_CHAR,

            GREEN_TRUCK_CHAR,
            GREEN_WAREHOUSE_CHAR,

            BLUE_TRUCK_CHAR,
            BLUE_WAREHOUSE_CHAR,

            YELLOW_TRUCK_CHAR,
            YELLOW_WAREHOUSE_CHAR,

            WHITE_TRUCK_CHAR,

            SWITCH_1_BUTTON_ENABLED_CHAR,
            SWITCH_1_BUTTON_DISABLED_CHAR,
            SWITCH_1_ROAD_OPEN_CHAR,
            SWITCH_1_ROAD_CLOSED_CHAR,

            SWITCH_2_BUTTON_ENABLED_CHAR,
            SWITCH_2_BUTTON_DISABLED_CHAR,
            SWITCH_2_ROAD_OPEN_CHAR,
            SWITCH_2_ROAD_CLOSED_CHAR,

            SWITCH_3_BUTTON_ENABLED_CHAR,
            SWITCH_3_BUTTON_DISABLED_CHAR,
            SWITCH_3_ROAD_OPEN_CHAR,
            SWITCH_3_ROAD_CLOSED_CHAR,

            SWITCH_4_BUTTON_ENABLED_CHAR,
            SWITCH_4_BUTTON_DISABLED_CHAR,
            SWITCH_4_ROAD_OPEN_CHAR,
            SWITCH_4_ROAD_CLOSED_CHAR,

            BUMP_CHAR,
    };

    static final Map<Character, Byte> CHAR_TO_BYTE = new HashMap<>(ALL_ELEMENTS.length);

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

    static final byte[] SWITCHES_BUTTONS_ENABLED = new byte[]{
            SWITCH_1_BUTTON_ENABLED,
            SWITCH_2_BUTTON_ENABLED,
            SWITCH_3_BUTTON_ENABLED,
            SWITCH_4_BUTTON_ENABLED,
    };

    static final byte[] SWITCHES_BUTTONS_DISABLED = new byte[]{
            SWITCH_1_BUTTON_DISABLED,
            SWITCH_2_BUTTON_DISABLED,
            SWITCH_3_BUTTON_DISABLED,
            SWITCH_4_BUTTON_DISABLED,
    };

    static final byte[] SWITCHES_ROAD_OPEN = new byte[]{
            SWITCH_1_ROAD_OPEN,
            SWITCH_2_ROAD_OPEN,
            SWITCH_3_ROAD_OPEN,
            SWITCH_4_ROAD_OPEN,
    };

    static final byte[] SWITCHES_ROAD_CLOSED = new byte[]{
            SWITCH_1_ROAD_CLOSED,
            SWITCH_2_ROAD_CLOSED,
            SWITCH_3_ROAD_CLOSED,
            SWITCH_4_ROAD_CLOSED,
    };

    static final Map<Byte, Byte> WAREHOUSE_TO_PACKAGES = new HashMap<>(WAREHOUSES.length);
    static final boolean[][] CAN_UNLOAD = new boolean[ALL_ELEMENTS.length][];
    static final Map<Byte, String> PACKAGE_TO_NAME = new HashMap<>();
    static final Map<Byte, String> TRUCK_TO_NAME = new HashMap<>();
    static final byte[] SHIFT_PER_PACKAGE = new byte[]{
            -1,
            RED_SHIFT,
            GREEN_SHIFT,
            BLUE_SHIFT,
            YELLOW_SHIFT,
    };

    static {
        for (byte i = 0; i < ALL_ELEMENTS.length; i++) {
            char c = ALL_ELEMENTS[i];
            CHAR_TO_BYTE.put(c, i);
        }

        WAREHOUSE_TO_PACKAGES.put(RED_WAREHOUSE, RED_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(BLUE_WAREHOUSE, BLUE_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(GREEN_WAREHOUSE, GREEN_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(YELLOW_WAREHOUSE, YELLOW_PACKAGE);

        PACKAGE_TO_NAME.put(RED_PACKAGE, "red");
        PACKAGE_TO_NAME.put(BLUE_PACKAGE, "blue");
        PACKAGE_TO_NAME.put(GREEN_PACKAGE, "green");
        PACKAGE_TO_NAME.put(YELLOW_PACKAGE, "yellow");

        TRUCK_TO_NAME.put(RED_TRUCK, "red");
        TRUCK_TO_NAME.put(BLUE_TRUCK, "blue");
        TRUCK_TO_NAME.put(GREEN_TRUCK, "green");
        TRUCK_TO_NAME.put(YELLOW_TRUCK, "yellow");
        TRUCK_TO_NAME.put(WHITE_TRUCK, "white");

        Arrays.sort(ALL_ELEMENTS);
        Arrays.sort(TRUCKS);
        Arrays.sort(PACKAGES);
        Arrays.sort(WAREHOUSES);

        CAN_UNLOAD[RED_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_UNLOAD[RED_TRUCK][RED_PACKAGE] = true;

        CAN_UNLOAD[BLUE_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_UNLOAD[BLUE_TRUCK][BLUE_PACKAGE] = true;

        CAN_UNLOAD[GREEN_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_UNLOAD[GREEN_TRUCK][GREEN_PACKAGE] = true;

        CAN_UNLOAD[YELLOW_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_UNLOAD[YELLOW_TRUCK][YELLOW_PACKAGE] = true;

        CAN_UNLOAD[WHITE_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_UNLOAD[WHITE_TRUCK][RED_PACKAGE] = true;
        CAN_UNLOAD[WHITE_TRUCK][BLUE_PACKAGE] = true;
        CAN_UNLOAD[WHITE_TRUCK][GREEN_PACKAGE] = true;
        CAN_UNLOAD[WHITE_TRUCK][YELLOW_PACKAGE] = true;
    }

}
