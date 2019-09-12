package gpu;

import main.Util;
import mmu.GameBoyMMU;
import mmu.Interrupts;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import joypad.Controller;

public class GameBoyGPU implements Runnable {

    private static GameBoyGPU singletonInstance;
    private static final int SCAN_OAM_TIME = 80;
    private static final int SCAN_VRAM_TIME = 172;
    private static final int HBLANK_TIME = 204;
    private static final int VBLANK_TIME = 456;
    static final int WIDTH_PIXELS = 160;
    static final int HEIGHT_PIXELS = 144;
    private static final int TILE_COUNT = 384;
    private static final int TILE_PIXEL_SIZE = 8;

    public static final int DEBUG_BACKGROUND_DIMENSION = 256;
    private Color[] backgroundPixels = new Color[DEBUG_BACKGROUND_DIMENSION*DEBUG_BACKGROUND_DIMENSION*2];

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
    private JFrame debugFrame;
    private DebugWindow debugWindow;


    public int clock = 0;
    private int[][][] tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    public Color[] pixels = new Color[WIDTH_PIXELS*(HEIGHT_PIXELS - 1)]; // long array of pixels. Wrapping is handled in code
    
    private int[] vram = new int[65536];
    private Sprite[] oam = new Sprite[40];
    private boolean LCDEnabled = false;


    private GameBoyGPU() {
        frame= new JFrame("Gameboy");
        screen=new GameBoyLCD(WIDTH_PIXELS, HEIGHT_PIXELS);
        screen.setBounds(0,0,WIDTH_PIXELS,HEIGHT_PIXELS);
        frame.add(screen);
        frame.setSize(WIDTH_PIXELS+16,HEIGHT_PIXELS + 38);

        frame.setLayout(null);
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Dimension size = frame.getPreferredSize();
        frame.setLocation((int) ((bounds.width / 2) - (size.getWidth() / 2)),
                (int) ((bounds.height / 2) - (size.getHeight() / 2)));
        frame.setVisible(true);
        frame.addKeyListener(new Controller());

        GpuRegisters.setStatMode(SCANLINE_OAM);

        if(Util.isDebugMode) {
            debugFrame= new JFrame("Debug");
            debugWindow=new DebugWindow(DEBUG_BACKGROUND_DIMENSION, DEBUG_BACKGROUND_DIMENSION*2);
            debugWindow.setBounds(0,0,DEBUG_BACKGROUND_DIMENSION,DEBUG_BACKGROUND_DIMENSION*2);
            debugWindow.addScrollLines(0,0);
            debugFrame.add(debugWindow);
            debugFrame.setSize(DEBUG_BACKGROUND_DIMENSION+16,DEBUG_BACKGROUND_DIMENSION*2 + 38);
            debugFrame.setLayout(null);
            debugFrame.setVisible(true);
            debugFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        }
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        for(Color c : pixels) {
        	c = baseColoursGB[0];
        }
    }



    public static GameBoyGPU getInstance() {
        if(singletonInstance == null) {
            singletonInstance = new GameBoyGPU();
        }
        return singletonInstance;
    }

    public void debugUpdateBackgroundWindow() {
        Color[] palette = createPalette();
        int lineNum = 0;

        int x = 0;
        int currentPixel = 0;
        for(int i = 0x9800; i < 0xA000;){
        	
            int tileNum = vram[i];
            int y = lineNum & 7;
            x = 0;
            int lineOffset = 0;
            for(int j = 0; j < DEBUG_BACKGROUND_DIMENSION; j++)
            {
                backgroundPixels[currentPixel] = palette[tileset[tileNum][y][x]];
                currentPixel++;

                x++;
                if(x == 8)
                {
                    x = 0;
                    lineOffset = lineOffset + 1;
                    tileNum = vram[i + lineOffset];
                }
            }
            lineNum++;
            if(y == 7) {
            	i = i + 32;
            }
        }
        int scrollX = GpuRegisters.getScrollX();
        int scrollY = GpuRegisters.getScrollY();
        debugWindow.drawData(backgroundPixels, scrollX, scrollY);


    }


    public void enableLCD() {
        this.LCDEnabled = true;
    }

    public void disableLCD() {
        this.LCDEnabled = false;
    }
    
    public static final int HBLANK = 0;
    public static final int VBLANK = 1;
    public static final int SCANLINE_OAM = 2;
    public static final int SCANLINE_VRAM = 3;

    @Override
    public void run() {
        int currentMode = GpuRegisters.getStatMode();
        switch(currentMode) {
            case SCANLINE_OAM:
                if(clock >= SCAN_OAM_TIME) {
                    clock = 0;
                    GpuRegisters.setStatMode(SCANLINE_VRAM);
                }
                break;
            case SCANLINE_VRAM:
                if(clock >= SCAN_VRAM_TIME) {
                    clock = 0;
                    GpuRegisters.setStatMode(HBLANK);
                    renderScan();
                }
                break;
            case HBLANK:
                if(clock >= HBLANK_TIME) {
                    clock = 0;
                    GpuRegisters.incrementScanLine();
                    if(GpuRegisters.getCurrentScanline() == HEIGHT_PIXELS - 1)
                    {
                    	//renderSprites();
                        drawData();
                        GpuRegisters.setStatMode(VBLANK);
                    }
                    else
                    {
                        GpuRegisters.setStatMode(SCANLINE_OAM);
                    }
                }
                break;
            case VBLANK:
                if(clock >= VBLANK_TIME) {
                    clock = 0;
                    Interrupts.setVblankInterrupt();
                    GpuRegisters.incrementScanLine();
                    if(GpuRegisters.getCurrentScanline() > 153)
                    {
                        GpuRegisters.setStatMode(SCANLINE_OAM);
                        GpuRegisters.setCurrentScanline(0);
                    }
                }
                break;
            default:
                break;
        }
    }



	private void renderScan() {
    	if (!LCDEnabled) {
    		return;
    	}

        int bgTilemap = GpuRegisters.getBackgroundTilemap();
        int bgTileset = GpuRegisters.getBackgroundTileset();
        bgTileset = 0;
        int line = GpuRegisters.getCurrentScanline();
        if (line == 3) {
        	int v = 0;
        }
        int scrollX = GpuRegisters.getScrollX();
        int scrollY = GpuRegisters.getScrollY();
        Color[] palette = createPalette();
        int mapOffset = bgTilemap == 1 ? 0x9C00 : 0x9800;

        mapOffset = mapOffset + (((line + scrollY & 0xFF) >> 3) << 5);
        int lineOffset = (scrollX >> 3);

        int y = (line + scrollY) & 7;

        int x = scrollX & 7;

        int canvasOffSet = (line * WIDTH_PIXELS);

        Color color;
        
        int tile = vram[mapOffset + lineOffset];
//        if(bgTileset == 1 && tile < 128) {
//            tile += 256;
//        }
        for(int i = 0; i < WIDTH_PIXELS; i++)
        {
            color = palette[tileset[tile][y][x]];
            pixels[canvasOffSet] = color;
            canvasOffSet++;

            x++;
            if(x == 8)
            {
                x = 0;
                lineOffset = (lineOffset + 1) & 31;
                tile = vram[mapOffset + lineOffset];
                if (bgTileset == 1 && tile < 128) {
                    tile += 256;
                }
            }
        }
    }
	

    private void renderSprites() {
    	for(int i = 0; i < oam.length; i++) {
    		
    	}
		
	}
    


    public void updateTile(int address) {
        if((address & 1) == 1) {
            address--;
        }
        // Work out which tile and row was updated
        int tileNum = (address >> 4) & 0xFF;
        int y = (address >> 1) & 7;
        
        int sx;
        for(int x = 0; x < 8; x++)
        {
            // Find bit index for this pixel
            sx = 1 << (7-x);

            // Update tile set
            int color1 = (vram[address] & sx) != 0 ? 1 : 0;
            int color2 = (vram[address + 1] & sx) != 0 ? 2 : 0;
            tileset[tileNum][y][x] = color1 + color2;
        }
    }

    public void updateSpriteData(int address, int value) {
    		address -=0xFE00;
    	    int obj=address>>2;
    	    if(obj<40)
    	    {
    	      switch(address & 0b11)
    	      {
    	        case 0:
    	        	oam[obj].pos.y = value - 0x10;
    	        	break;
    	        case 1:
    	        	oam[obj].pos.x = value - 0x8;
    	        	break;
    	        case 2:
    	          if(GpuRegisters.getLCDCSpriteSize() == 8*16) {
    	        	  oam[obj].tileNum = (value&0xFE);
    	          }
    	          else {
    	        	  oam[obj].tileNum = value;
    	          }
    	          break;
    	        case 3:
    	        	oam[obj].palette = ((value & 0x10) == 1)? 1 : 0;
    	        	oam[obj].xFlip = ((value & 0x20) == 1)? true: false;
    	        	oam[obj].yFlip = ((value & 0x40) == 1)? true: false;
    	        	oam[obj].hasPriority = ((value & 0x80) ==  0)? true : false;
    	          break;
    	     }
    	    }
    }
    
    private Color[] createPalette() {
        Color[] palette = new Color[4];
        int paletteRegister = GpuRegisters.getBGPalette();
        int color0 = paletteRegister & 0b11;
        int color1 = (paletteRegister >> 2) & 0b11;
        int color2 = (paletteRegister >> 4)  & 0b11;
        int color3 = (paletteRegister >> 6)  & 0b11;
        palette[0] = baseColoursGB[color0];
        palette[1] = baseColoursGB[color1];
        palette[2] = baseColoursGB[color2];
        palette[3] = baseColoursGB[color3];
        return palette;
    }

    public void drawData() {
        screen.drawData(pixels);
    }

    public void addClockTime(int cycles) {
        this.clock = this.clock + cycles;
    }

    public void setVRAM(int address, int source) {
        vram[address] = source;
    }

    public void resetVRAM() {
        vram = new int[65536];
    }
    public void resetTiles() {
        tileset = new int[TILE_COUNT][TILE_PIXEL_SIZE][TILE_PIXEL_SIZE];
    }

    public void dumpVram() {
        String s = "VRAM DUMP:\n";
        int j = 0;
        for(int i = 0; i < vram.length; i++) {
            if (vram[i] != 0) {
                if (j == 15) {
                    j = 0;
                    s = s + "\n";
                }
                s = s + i + " = " + vram[i] + " ";
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

        String s = "TILEMAP "+ tilemapNum +" DUMP: \n";
        int startPos = (tilemapNum == 1) ? 0x9C00 : 0x9800;
        int endPos = (tilemapNum == 1) ? 0x9FFF : 0x9C00;
        int z = 0;
        int j = 1;
        s = s + "    ";
        int line1chars = 0;
        for(int i = 0; i < 32; i++) {
            if ( i < 10) {
                s = s + " " + i + " ";
            }
            else {
                s = s + i + " ";
            }
            line1chars += 3;
        }
        s = s + "\n     ";
        for(int i = 0; i < line1chars; i++) {
            s = s + "-";
        }
        s = s + "\n0-> ";
        for(int i =startPos; i < endPos; i++) {
            if(z == 32) {
                z = 0;
                s = s + "\n";
                if( j >= 10) {
                    s = s + j + "->";
                    j++;
                }
                else {
                    s = s + j + "-> ";
                    j++;
                }
            }
            s = s + Util.byteToHex(Util.getMemory().getMemoryAtAddress(i)) + " ";
            z++;
        }
        System.out.println(s);
    }

    public void dumpSetBackgroundTilemap(int tilemapNum) {

        String s = "TILEMAP "+ tilemapNum +" DUMP :\n";
        int startPos = (tilemapNum == 1) ? 0x9C00 : 0x9800;
        int endPos = (tilemapNum == 1) ? 0x9FFF : 0x9C00;
        int z = 0;
        for(int i =startPos; i < endPos; i++) {
        	if (Util.getMemory().getMemoryAtAddress(i) != 0) {

                if(z == 32) {
                    z = 0;
                    s = s + "\n";
                }
                s = s + Util.byteToHex16(i) + "-> " +Util.byteToHex(Util.getMemory().getMemoryAtAddress(i)) + " ";
                z++;
        	}
        }
        System.out.println(s);
    }
    public void printBackgroundTilemap(int tilemapNum) {
        String s = "TILEMAP/SET "+ tilemapNum +" DUMP:\n";
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
                s = s + tileset[Util.getMemory().getMemoryAtAddress(i)][k][j];
                z++;
            }
        }
        System.out.println(s);
    }

    public void printTile(int tileNum) {
        System.out.println("TILE DUMP: 0x" + Util.byteToHex16(tileNum) + "");
        String output = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
            	//System.out.println(Arrays.toString(tileset[tileNum]));
            	String character = (tileset[tileNum][i][j] == 0) ? "0" : tileset[tileNum][i][j]+"";
                output = output + character;
            }
            output = output + "\n";
        }
        System.out.println(output);
    }
   
}
