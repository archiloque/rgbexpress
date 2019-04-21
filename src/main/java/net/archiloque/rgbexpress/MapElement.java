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

    static final char[] ALL_ELEMENTS = new char[]{
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

    static final Map<Byte, Byte> WAREHOUSE_TO_PACKAGES = new HashMap<>(WAREHOUSES.length);
    static final boolean[][] CAN_PICK = new boolean[ALL_ELEMENTS.length][];

    static {
        for (byte i = 0; i < ALL_ELEMENTS.length; i++) {
            char c = ALL_ELEMENTS[i];
            CHAR_TO_BYTE.put(c, i);
        }

        WAREHOUSE_TO_PACKAGES.put(RED_WAREHOUSE, RED_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(BLUE_WAREHOUSE, BLUE_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(GREEN_WAREHOUSE, GREEN_PACKAGE);
        WAREHOUSE_TO_PACKAGES.put(YELLOW_WAREHOUSE, YELLOW_PACKAGE);

        Arrays.sort(ALL_ELEMENTS);
        Arrays.sort(TRUCKS);
        Arrays.sort(PACKAGES);
        Arrays.sort(WAREHOUSES);

        CAN_PICK[RED_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_PICK[RED_TRUCK][RED_PACKAGE] = true;

        CAN_PICK[BLUE_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_PICK[BLUE_TRUCK][BLUE_PACKAGE] = true;

        CAN_PICK[GREEN_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_PICK[GREEN_TRUCK][GREEN_PACKAGE] = true;

        CAN_PICK[YELLOW_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_PICK[YELLOW_TRUCK][YELLOW_PACKAGE] = true;

        CAN_PICK[WHITE_TRUCK] = new boolean[ALL_ELEMENTS.length];
        CAN_PICK[WHITE_TRUCK][RED_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][BLUE_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][GREEN_PACKAGE] = true;
        CAN_PICK[WHITE_TRUCK][YELLOW_PACKAGE] = true;
    }

}
