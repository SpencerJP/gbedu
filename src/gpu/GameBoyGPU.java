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
    public static final int WIDTH_PIXELS = 160;
    public static final int HEIGHT_PIXELS = 144;
    private static final int TILE_COUNT = 384;
    private static final int TILE_PIXEL_SIZE = 8;



    private static final Color DARKEST_GREEN = new Color(15, 56, 15);
    private static final Color DARK_GREEN = new Color(48, 98, 48);
    private static final Color LIGHT_GREEN = new Color(139, 172, 15);
    private static final Color LIGHTEST_GREEN = new Color(155, 188, 15);
    private static final Color[] baseColoursGB = new Color[4];
    static {
        baseColoursGB[0] = LIGHTEST_GREEN;
        baseColoursGB[1] = LIGHT_GREEN;
        baseColoursGB[2] = DARK_GREEN;
        baseColoursGB[3] = DARKEST_GREEN;
    }


    private JFrame frame;
    private GameBoyLCD screen;
    public GpuModes mode;
    public int clock = 0;
    private int[][][] tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    private Color[] pixels = new Color[WIDTH_PIXELS*HEIGHT_PIXELS]; // long array of pixels. Wrapping is handled in code
    private int[] vram = new int[65536];
    private boolean LCDEnabled;
    private int bgTileBase = 0x0000;
    private int bgMapBase = 0x1800;
    private int winTileBase = 0x1800;

    private GameBoyGPU() {
        frame= new JFrame("Gameboy");
        screen=new GameBoyLCD(WIDTH_PIXELS, HEIGHT_PIXELS);
        screen.setBounds(0,0,WIDTH_PIXELS,HEIGHT_PIXELS);
        frame.add(screen);
        frame.setSize(WIDTH_PIXELS+20,HEIGHT_PIXELS + 40);
        frame.setLayout(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        mode = GpuModes.SCANLINE_OAM;
    }



    public static GameBoyGPU getInstance() {
        if(singletonInstance == null) {
            singletonInstance = new GameBoyGPU();
        }
        return singletonInstance;
    }

    public void enableLCD() {
        GpuRegisters.setDisplayOn(true);
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
                    GpuRegisters.incrementScanLine();
                    if(GpuRegisters.getCurrentScanline() == HEIGHT_PIXELS - 1)
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
                    GpuRegisters.incrementScanLine();
                    if(GpuRegisters.getCurrentScanline() > 153)
                    {
                        mode = GpuModes.SCANLINE_OAM;
                        GpuRegisters.setCurrentScanline(0);
                    }
                }
                break;

        }
    }
    private static int testint = 0;
    private void renderScan() {
        int mapOffset = (GpuRegisters.getBackgroundTilemap() == 1) ? 0x9C00 : 0x9800; // TODO this is not where they ARE STORED

        int line = GpuRegisters.getCurrentScanline();
        int scrollX = GpuRegisters.getScrollX();
       // int scrollY = GpuRegisters.getScrollY();
        int scrollY = 0;
        //System.out.println(scrollY);

        mapOffset += ((line + scrollY) & 0xFF) >> 3;
        //System.out.println(((line + scrollY) & 0xFF) >> 3);

        int lineOffset = (scrollX >> 3);

        int y = (line + scrollY) & 7;

        int x = scrollX & 7;

        int canvasOffSet = (line * WIDTH_PIXELS);

        Color color;
        //System.out.println("mapOffset=" + Util.byteToHex16(mapOffset) + ", lineOffset="+lineOffset + " bgTileset?:"+GpuRegisters.getBackgroundTileset());
        int tile = vram[mapOffset + lineOffset];
        //System.out.println(tile);

        if(GpuRegisters.getBackgroundTileset() == 1 && tile < 128) {
            tile += 256;
        }
        for(int i = 0; i < WIDTH_PIXELS; i++)
        {
            // Re-map the tile pixel through the palette
            color = baseColoursGB[tileset[tile][y][x]];

            // Plot the pixel to canvas
            if (color != baseColoursGB[0]) {
            }
            pixels[canvasOffSet] = color;

            canvasOffSet++; // next pixel

            // When this tile ends, read another
            x++;
            if(x == 8)
            {
                x = 0;
                lineOffset = (lineOffset + 1) & 31;
                tile = vram[mapOffset + lineOffset];
                if (GpuRegisters.getBackgroundTileset() == 1 && tile < 128) {
                    tile += 256;
                }
            }
        }
    }

    private void drawData() {
        screen.drawData(pixels);
    }

    public void addClockTime(int cycles) {
        this.clock = this.clock + cycles;
    }

    public void setVRAM(int address, int source) {
        vram[address] = source;
    }

    public void updateTile(int address) {
        // Work out which tile and row was updated
        int tileNum = (address >> 4) & 0xFF;
        int y = (address >> 1) & 7;

        int sx;
        for(int x = 0; x < 8; x++)
        {
            // Find bit index for this pixel
            sx = 1 << (7-x);

            // Update tile set
            //System.out.println("test");
            int color1 = (vram[address] & sx) != 0 ? 1 : 0;
            int color2 = (vram[address + 1] & sx) != 0 ? 2 : 0;
            //System.out.println("tileNum: " + tileNum + ", x: " + x + ", y: " + y + ", color: " + (color1 + color2));
            tileset[tileNum][y][x] = color1 + color2;
        }
    }

    public void resetTiles() {
        tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    }

    public void dumpVram() {
        String s = "VRAM DUMP:\n";
        int j = 0;
        for(int i = 0x8000; i < 0x9fff; i++) {
            if (vram[i] != 0) {
                if (j == 15) {
                    j = 0;
                    s = s + "\n";
                }
                s = s + Util.byteToHex(i) + " = " + vram[i] + " ";
                j++;
            }
        }
        System.out.println(s);
    }

    public void dumpTileset() {
        String s = "TILESET DUMP:\n";
        int z = 0;
        for(int i = 0; i < tileset.length; i++) {
            for(int j = 0; j < 8; j++) {
                for(int k = 0; k < 8; k++) {
                    if (tileset[i][j][k] != 0) {
                        if (z == 15) {
                            z = 0;
                            s = s + "\n";
                        }
                        s = s + Util.byteToHex(i) + " = " + tileset[i][j][k] + " ";
                        z++;
                    }
                }
            }

        }
        System.out.println(s);
    }

    public void dumpBackgroundTilemap(int tilemapNum) {

        String s = "TILEMAP "+ tilemapNum +" DUMP :\n";
        int startPos = (tilemapNum == 1) ? 0x9C00 : 0x9800;
        int endPos = (tilemapNum == 1) ? 0x9FFF : 0x9C00;
        int z = 0;
        for(int i =startPos; i < endPos; i++) {
            if(z == 32) {
                z = 0;
                s = s + "\n";
            }
            s = s + Util.byteToHex(Util.getMemory().getMemoryAtAddress(i));
            z++;
        }
        System.out.println(s);
    }

    public void printBackgroundTilemap(int tilemapNum) {
        String s = "TILEMAP/SET "+ tilemapNum +" DUMP :\n";
        int startPos = (tilemapNum == 1) ? 0x9C00 : 0x9800;
        int endPos = (tilemapNum == 1) ? 0x9FFF : 0x9BFF;
        //int mapOffset = (tilemapNum == 1) ? 0x8800 : 0x8000;
        int z = 0;
        int k = 0;
        for(int i = startPos; i < endPos; i++) {
            if(z == 256) {
                z = 0;
                s = s + "\n";
                k++;
                if (k == 8) {
                    k = 0;
                }
            }

            for(int j = 0; j <8;j++ ){
                //System.out.println("mOffset="+Util.byteToHex16(mapOffset)+", address=" + Util.byteToHex16(i) +", addedTogether=" + (mapOffset + Util.getMemory().getMemoryAtAddress(i)));
                s = s + tileset[Util.getMemory().getMemoryAtAddress(i)][k][j];
                z++;
            }
        }
        System.out.println(s);
    }

    public void printTile(int tilemapNum, int tileNum) {
        int mapOffset = (tilemapNum == 0) ? 0x8000 : 0x8800;
        tileNum = ((mapOffset + tileNum) >> 4) & 0xFF;
        String output = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                output = output + tileset[tileNum][i][j];
            }
            output = output + "\n";
        }
        System.out.println(output);
    }

    public void printTile(int tileNum) {
        tileNum = (tileNum >> 4) & 0xFF;
        String output = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                output = output + tileset[tileNum][i][j];
            }
            output = output + "\n";
        }
        System.out.println(output);
    }
}
