package gpu;

import main.Util;

public class GpuRegisters {
    public static final int LCDC = 0xff40;
    public static final int scrollY = 0xFF42;
    public static final int scrollX = 0xFF43;
    public static final int currentScanLine = 0xFF44;
    public static final int backgroundPalette = 0xFF47;

    public static int getLCDC() {
        return Util.getMemory().getMemoryAtAddress(LCDC);
    }

    public static void setLCDC(int val) {
        Util.getMemory().setMemoryAtAddress(LCDC, val);
    }

    public static int getScrollX() {
        return Util.getMemory().getMemoryAtAddress(scrollX);
    }

    public static int getScrollY() {
        return Util.getMemory().getMemoryAtAddress(scrollY);
    }

    public static void setScrollX(int x) {
        Util.getMemory().setMemoryAtAddress(scrollX, x);
    }

    public static void setScrollY(int y) {
        Util.getMemory().setMemoryAtAddress(scrollY, y);
    }

    public static int getCurrentScanline() {
        return Util.getMemory().getMemoryAtAddress(currentScanLine);
    }

    public static void setCurrentScanline(int line) {
        Util.getMemory().setMemoryAtAddress(currentScanLine, line);
    }

    public static void setBackgroundPalette(int val) {
        Util.getMemory().setMemoryAtAddress(backgroundPalette, val);
    }


    public static boolean getLCDCBackground() {
        return Util.getBit(getLCDC(), 0);
    }

    public static boolean getLCDCSprites() {
        return Util.getBit(getLCDC(), 1);
    }

    public static int getLCDCSpriteSize() {
        if (Util.getBit(getLCDC(), 2)) {
            return 8*16;
        }
        else {
            return 8*8;
        }
    }

    public static int getBackgroundTilemap() {
        return Util.getBit(getLCDC(), 3) ? 1 : 0;
    }

    public static int getBackgroundTileset() {
        return Util.getBit(getLCDC(), 4) ? 1 : 0;
    }

    public static boolean getWindowOn() {
        return Util.getBit(getLCDC(), 5);
    }

    public static int getWindowTilemap() {
        return Util.getBit(getLCDC(), 6) ? 1 : 0;
    }

    public static boolean getDisplayOn() {
        return Util.getBit(getLCDC(), 7);
    }


    public static void setLCDCBackground(boolean set) {
        if (set) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 0));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 0));
        }
    }

    public static void setLCDCSprites(boolean set) {
        if (set) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 1));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 1));
        }
    }

    public static void setLCDCSpriteSize(int i) {
        if (i == 8*16) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 2));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 2));
        }
    }

    public static void setBackgroundTilemap(int i) {
        if (i == 1) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 3));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 3));
        }
    }

    public static void setBackgroundTileset(int i) {
        if (i == 1) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 4));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 4));
        }
    }
    public static void setWindowOn(boolean set) {
        if (set) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 5));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 5));
        }
    }

    public static void setWindowTilemap(int i) {
        if (i == 1) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 6));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 6));
        }
    }

    public static void setDisplayOn(boolean set) {
        if (set) {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.setBit(getLCDC(), 7));
        }
        else {
            Util.getMemory().setMemoryAtAddress(LCDC,Util.resetBit(getLCDC(), 7));
        }
    }

    public static void incrementScanLine() {
        Util.getMemory().setMemoryAtAddress(currentScanLine, Util.getMemory().getMemoryAtAddress(currentScanLine) + 1);
    }
}
