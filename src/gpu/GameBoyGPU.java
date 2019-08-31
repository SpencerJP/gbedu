package gpu;

import main.Util;
import mmu.GameBoyMMU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameBoyGPU implements Runnable {

    private static GameBoyGPU singletonInstance;
    private static final int SCAN_OAM_TIME = 80;
    private static final int SCAN_VRAM_TIME = 172;
    private static final int HBLANK_TIME = 204;
    private static final int VBLANK_TIME = 456;
    private static final int WIDTH_PIXELS = 160;
    private static final int HEIGHT_PIXELS = 144;
    private static final int TILE_COUNT = 384;
    private static final int TILE_PIXEL_SIZE = 8;


    private static final Color DARKEST_GREEN = new Color(15, 56, 15);
    private static final Color DARK_GREEN = new Color(48, 98, 48);
    private static final Color LIGHT_GREEN = new Color(139, 172, 15);
    private static final Color LIGHTEST_GREEN = new Color(155, 188, 15);
    private static final Color[] baseColoursGB = new Color[4];
    static {
        baseColoursGB[0] = DARKEST_GREEN;
        baseColoursGB[1] = DARK_GREEN;
        baseColoursGB[2] = LIGHT_GREEN;
        baseColoursGB[3] = LIGHTEST_GREEN;
    }


    private JFrame frame;
    private JPanel screen;
    private GpuModes mode;
    private int clock = 0;
    private int line = 0;
    private int[][][] tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    private Color[][] pixels = new Color[WIDTH_PIXELS][HEIGHT_PIXELS];
    private int[] vram = new int[65536];
    private boolean LCDEnabled;

    private GameBoyGPU() {
        frame= new JFrame("Gameboy");
        screen=new JPanel();
        screen.setBounds(WIDTH_PIXELS,HEIGHT_PIXELS,WIDTH_PIXELS,HEIGHT_PIXELS);
        frame.add(screen);
        frame.setSize(WIDTH_PIXELS+50,HEIGHT_PIXELS+50);
        frame.setLayout(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        mode = GpuModes.HBLANK;
    }



    public static GameBoyGPU getInstance() {
        if(singletonInstance == null) {
            singletonInstance = new GameBoyGPU();
        }
        return singletonInstance;
    }

    public void enableLCD() {
        GameBoyMMU mmu = Util.getMemory();
        int lCDCRegister = mmu.getMemoryAtAddress(0xff44);
        lCDCRegister = Util.setBit(lCDCRegister, 0);
        mmu.setMemoryAtAddress(0xff44, lCDCRegister);
        this.LCDEnabled = true;
    }

    public void disableLCD() {
        this.LCDEnabled = false;
    }


    @Override
    public void run() {
        switch(mode) {
            case SCANLINE_OAM:
                if(clock >= SCAN_OAM_TIME) {
                    clock = 0;
                    mode = GpuModes.SCANLINE_VRAM;
                }
                break;
            case SCANLINE_VRAM:
                if(clock >= SCAN_VRAM_TIME) {
                    clock = 0;
                    mode = GpuModes.HBLANK;
                    renderScan();
                }
                break;
            case HBLANK:
                if(clock >= HBLANK_TIME) {
                    clock = 0;
                    line++;
                    if(line == WIDTH_PIXELS - 1)
                    {
                        mode = GpuModes.VBLANK;
                        drawData();
                    }
                    else
                    {
                        mode = GpuModes.SCANLINE_OAM;
                    }
                }
                break;
            case VBLANK:
                if(clock >= VBLANK_TIME) {
                    clock = 0;
                    line++;
                    if(line > 153)
                    {
                        mode = GpuModes.SCANLINE_OAM;
                        line = 0;
                    }
                }
                break;

        }
    }

    private void renderScan() {

    }

    private void drawData() {

    }

    public void addClockTime(int cycles) {
        this.clock = this.clock + cycles;
    }

    public void setVRAM(int address, int source) {
        vram[address] = source;
    }

    public void updateTile(int address) {
        address &= 0x1FFE;

        // Work out which tile and row was updated
        int tileNum = (address >> 4) & 0xFF;
        int y = (address >> 1) & 7;

        int sx;
        for(int x = 0; x < 8; x++)
        {
            // Find bit index for this pixel
            sx = 1 << (7-x);

            // Update tile set
            tileset[tileNum][y][x] = ((vram[address] & sx) != 0) ? 1 : 0 + (((vram[address +1] & sx) != 0) ? 2 : 0);
        }
    }

    public void resetTiles() {
        tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    }
}
