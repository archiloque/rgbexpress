package net.archiloque.rgbexpress;

final class Direction {

    private Direction() {
    }

    final static byte UP = 0;
    final static byte DOWN = 1;
    final static byte LEFT = 2;
    final static byte RIGHT = 3;

    final static byte[] OPPOSITE = new byte[]{
            DOWN,
            UP,
            RIGHT,
            LEFT
    };

    final static char[] AS_CHAR = new char[]{
            '↑',
            '↓',
            '←',
            '→'
    };

}
