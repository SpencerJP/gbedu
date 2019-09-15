package gpu;

import main.Util;
import mmu.Interrupts;

public class GpuRegisters {

    public static final int LCDC = 0xFF40;
    public static final int STAT = 0xFF41;
    public static final int SCROLL_Y = 0xFF42;
    public static final int SCROLL_X = 0xFF43;
    public static final int CURRENT_SCAN_LINE = 0xFF44;
    public static final int LYC_COMPARE = 0xFF45;
    public static final int DMA_TRANSFER = 0xFF46;
    public static final int BACKGROUND_PALETTE = 0xFF47;
    public static final int WINDOW_X = 0xFF4A;
    public static final int WINDOW_Y = 0xFF4B;



    // bit positions in LCDC
    public static final int LCD_DISPLAY_ENABLED = 7;
    public static final int WINDOW_TILEMAP_SELECT = 6;
    public static final int WINDOW_DISPLAY_ENABLED = 5;
    public static final int TILE_DATA_SELECT = 4;
    public static final int BG_TILEMAP_SELECT = 3;
    public static final int SPRITE_SIZE = 2;
    public static final int SPRITES_ENABLED = 1;
    public static final int BG_WINDOW_PRIORITY = 0;

    //LCDC
    private static boolean displayEnabled = false;
    private static int windowTilemap = 0;
    private static boolean windowEnabled = false;
    private static int tileDataSelect = 0;
    private static int bgTilemapSelect = 0;
    private static int spriteSize = 8*8;
    private static boolean spritesEnabled = false;
    private static boolean bgWindowPriority = false;


    //STAT Register bit positions
    public static final int COINCIDENCE_INTERRUPT = 6;
    public static final int OAM_INTERRUPT = 5;
    public static final int VBLANK_INTERRUPT = 4;
    public static final int HBLANK_INTERRUPT = 3;
    public static final int COINCIDENCE_FLAG = 2;
//    bits 1 and 0 for statmode

    //Actual values
    private static boolean coincidenceInterrupt = false;



    private static boolean oamInterrupt = false;
    private static boolean vblankInterrupt = false;
    private static boolean hblankInterrupt = false;
    private static boolean coincidenceFlag = false;

    //STAT Modes
    private static final int SCANLINE_OAM = 2;
    private static int statMode = SCANLINE_OAM;

    private static int scrollX = 0;
    private static int scrollY = 0;
    private static int currentScanLine = 0;
    private static int palette = 0;
    private static int windowX = 0;
    private static int windowY = 0;
    private static int compareLY = 0;

    public static void setRegisters(int address, int value) {
        value &= 0xFF;
        switch(address) {
            case LCDC:
                setLCDC(value);
                break;
            case STAT:
                setStatRegister(value);
                break;
            case SCROLL_X:
                scrollX = value;
                GameBoyGPU.getInstance().drawDebugDataInSwingThread();
                break;
            case SCROLL_Y:
                scrollY = value;
                GameBoyGPU.getInstance().drawDebugDataInSwingThread();
                break;
            case CURRENT_SCAN_LINE:
                //I'm pretty sure this is read only.
                break;
            case LYC_COMPARE:
                compareLY = value;
                break;
            case DMA_TRANSFER:
                // dunno yet
                break;
            case BACKGROUND_PALETTE:
                palette = value;
                break;
            case WINDOW_X:
                windowX = value;
                break;
            case WINDOW_Y:
                windowY = value;
                break;
        }
    }

    public static int getRegisters(int address) {
        switch(address) {
            case LCDC:
                return getLCDC();
            case STAT:
                return getStatRegister();
            case SCROLL_X:
                return scrollX;
            case SCROLL_Y:
                return scrollY;
            case CURRENT_SCAN_LINE:
                return currentScanLine;
            case LYC_COMPARE:
                return compareLY;
            case DMA_TRANSFER:
                // dunno yet
                break;
            case BACKGROUND_PALETTE:
                return palette;
            case WINDOW_X:
                return windowX;
            case WINDOW_Y:
                return windowY;
        }
        return 0;
    }




    public static int getStatMode() {
        return statMode;
    }

    public static void setStatMode(int mode) {
        statMode = mode;
    }

    public static int getStatRegister() {
        int i = 0;
        if(coincidenceInterrupt) {
            i = i | (1 << COINCIDENCE_INTERRUPT);
        }
        if(oamInterrupt) {
            i = i | (1 << OAM_INTERRUPT);
        }
        if(vblankInterrupt) {
            i = i | (1 << VBLANK_INTERRUPT);
        }
        if(hblankInterrupt) {
            i = i | (1 << HBLANK_INTERRUPT);
        }
        if(coincidenceFlag) {
            i = i | (1 << COINCIDENCE_FLAG);
        }
        i = i | statMode;
        return i;
    }

    public static void setStatRegister(int val) {
        coincidenceInterrupt = ((val >> COINCIDENCE_INTERRUPT) & 1 ) == 1;
        oamInterrupt = ((val >> OAM_INTERRUPT) & 1 ) == 1;
        vblankInterrupt = ((val >> VBLANK_INTERRUPT) & 1 ) == 1;
        hblankInterrupt = ((val >> HBLANK_INTERRUPT) & 1 ) == 1;
    }

    public static int getLCDC() {
        int i = 0;
        if(displayEnabled) {
            i = i | (1 << LCD_DISPLAY_ENABLED);
        }
        if(windowTilemap == 1) {
            i = i | (1 << WINDOW_TILEMAP_SELECT);
        }
        if(tileDataSelect == 1) {
            i = i | (1 << WINDOW_DISPLAY_ENABLED);
        }
        if(bgTilemapSelect == 1) {
            i = i | (1 << TILE_DATA_SELECT);
        }
        if(spriteSize == 8*16) {
            i = i | (1 << SPRITE_SIZE);
        }
        if(spritesEnabled) {
            i = i | (1 << SPRITES_ENABLED);
        }
        if(bgWindowPriority) {
            i = i | (1 << BG_WINDOW_PRIORITY);
        }

        return i;
    }

    public static void setLCDC(int val) {
        displayEnabled = ((val >> LCD_DISPLAY_ENABLED) & 1 ) == 1;
        windowTilemap = ((val >> WINDOW_TILEMAP_SELECT) & 1 );
        windowEnabled = ((val >> WINDOW_DISPLAY_ENABLED) & 1 ) == 1;
        tileDataSelect = ((val >> TILE_DATA_SELECT) & 1 );
        bgTilemapSelect = ((val >> BG_TILEMAP_SELECT) & 1 );
        spriteSize = ((val >> SPRITE_SIZE) & 1 ) == 1 ? 8*16 : 8*8;
        spritesEnabled = ((val >> SPRITES_ENABLED) & 1 ) == 1;
        bgWindowPriority = ((val >> BG_WINDOW_PRIORITY) & 1) == 1;
    }

    public static int getScrollX() {
        return scrollX;
    }

    public static int getScrollY() {
        return scrollY;
    }

    public static int getCurrentScanline() {
        return currentScanLine;
    }

    public static boolean getBackgroundPriority() {
        return bgWindowPriority;
    }

    public static boolean getSpritesEnabled() {
        return spritesEnabled;
    }

    public static int getSpriteSize() {
        return spriteSize;
    }

    public static int getBackgroundTilemap() {
        return bgTilemapSelect;
    }

    public static int getSelectedTileset() {
        return tileDataSelect;
    }

    public static boolean getWindowOn() {
        return windowEnabled;
    }

    public static int getWindowTilemap() {
        return windowTilemap;
    }

    public static boolean getDisplayOn() {
        return displayEnabled;
    }

    public static void incrementScanLine() {
        currentScanLine++;
        if(compareLY == currentScanLine) {
            coincidenceFlag = true;
            setCoincidenceInterrupt(true);
            Util.getInterrupts().setLCDStatInterrupt();
        }
    }

    public static int getBGPalette() {
        return palette;
    }

    public static void setCurrentScanline(int i) {
        currentScanLine = i;
    }

    public static void setCoincidenceInterrupt(boolean coincidenceInterrupt) {
        GpuRegisters.coincidenceInterrupt = coincidenceInterrupt;
    }

    public static void setOamInterrupt(boolean oamInterrupt) {
        GpuRegisters.oamInterrupt = oamInterrupt;
    }

    public static void setVblankInterrupt(boolean vblankInterrupt) {
        GpuRegisters.vblankInterrupt = vblankInterrupt;
    }

    public static void setHblankInterrupt(boolean hblankInterrupt) {
        GpuRegisters.hblankInterrupt = hblankInterrupt;
    }
}
