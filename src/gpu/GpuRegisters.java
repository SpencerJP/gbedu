package gpu;

import main.Util;

public class GpuRegisters {
    public static final int LCDC = 0xff40;
    public static final int SCROLL_Y = 0xFF42;
    public static final int SCROLL_X = 0xFF43;
    public static final int CURRENT_SCAN_LINE = 0xFF44;
    public static final int BACKGROUND_PALETTE = 0xFF47;

    // bit positions in LCDC
    public static final int LCD_DISPLAY_ENABLED = 7;
    public static final int WINDOW_TILEMAP_SELECT = 6;
    public static final int WINDOW_DISPLAY_ENABLED = 5;
    public static final int TILE_DATA_SELECT = 4;
    public static final int BG_TILEMAP_SELECT = 3;
    public static final int SPRITE_SIZE = 2;
    public static final int SPRITES_ENABLED = 1;
    public static final int BG_WINDOW_PRIORITY = 0;

    public static int getLCDC() {
        return Util.getMemory().getMemoryAtAddress(LCDC);
    }

    public static void setLCDC(int val) {
    	if (val == 0x91) {
    		GameBoyGPU.getInstance().enableLCD();
    	}
        
    }

    public static int getScrollX() {
        return Util.getMemory().getMemoryAtAddress(SCROLL_X);
    }

    public static int getScrollY() {
        return Util.getMemory().getMemoryAtAddress(SCROLL_Y);
    }

    public static int getCurrentScanline() {
        return Util.getMemory().getMemoryAtAddress(CURRENT_SCAN_LINE);
    }

    public static void setCurrentScanline(int line) {
        Util.getMemory().setMemoryAtAddress(CURRENT_SCAN_LINE, line);
    }

    public static boolean getLCDCBackground() {
        return Util.getBit(getLCDC(), BG_WINDOW_PRIORITY);
    }

    public static boolean getLCDCSprites() {
        return Util.getBit(getLCDC(), SPRITES_ENABLED);
    }

    public static int getLCDCSpriteSize() {
        if (Util.getBit(getLCDC(), SPRITE_SIZE)) {
            return 8*16;
        }
        else {
            return 8*8;
        }
    }

    public static int getBackgroundTilemap() {
        return Util.getBit(getLCDC(), BG_TILEMAP_SELECT) ? 1 : 0;
    }

    public static int getBackgroundTileset() {
        // this one is backwards for some reason
        return Util.getBit(getLCDC(), TILE_DATA_SELECT) ? 0 : 1;
    }

    public static boolean getWindowOn() {
        return Util.getBit(getLCDC(), WINDOW_DISPLAY_ENABLED);
    }

    public static int getWindowTilemap() {
        return Util.getBit(getLCDC(), WINDOW_TILEMAP_SELECT) ? 1 : 0;
    }

    public static boolean getDisplayOn() {
        return Util.getBit(getLCDC(), LCD_DISPLAY_ENABLED);
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
        Util.getMemory().setMemoryAtAddress(CURRENT_SCAN_LINE, Util.getMemory().getMemoryAtAddress(CURRENT_SCAN_LINE) + 1);
    }

    public static int getBGPalette() {
        return Util.getMemory().getMemoryAtAddress(BACKGROUND_PALETTE);
    }
}
