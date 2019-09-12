package mmu;


import bootrom.BootRom;
import gpu.GameBoyGPU;
import main.Util;
import gpu.GpuRegisters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import cpu.GameBoyCPU;


public class GameBoyMMU {

	private File file;
	private int[] addressSpace;
	public boolean disableBootrom;
	private static GameBoyMMU singletonInstance;
	private IORegisters ior;
	
	private GameBoyMMU() {
		addressSpace = new int[65536];
		ior = new IORegisters(this);

	}
	
	public void initialize(String filename, boolean disableBootrom) throws IOException {
	    this.disableBootrom = disableBootrom;
	    if(disableBootrom) {
	        Util.getCPU().setProgramCounter(0x100);
	        Util.getGPU().enableLCD();
        }
		file = new File(filename);

		FileInputStream fStream = new FileInputStream(file);
		
		try {
			byte[] buff = new byte[65536];
			int loadromLength = (new Long(file.length())).intValue();
			fStream.read(buff, 0, loadromLength);
            for(int i = 0; i < buff.length; i++) {
            	addressSpace[i] = buff[i];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
            fStream.close();
		}
		powerUp();
	}

	private void powerUp() {
	}

	public static GameBoyMMU getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GameBoyMMU();
		}
		return singletonInstance;
	}
	
	public int getMemoryAtAddress(int address) throws ArrayIndexOutOfBoundsException {
		if (disableBootrom) {
			if((address & 0xff00) == 0xff00) {
				return getHRAM(address);
			}
			return addressSpace[address] & 0xFF;
		}
		else if(address < BootRom.data.length) {
			return BootRom.data[address] & 0xFF;
		}
		return addressSpace[address] & 0xFF;
	}

	private int getHRAM(int address) {
		switch(address) {
			case 0xff00:
				return Util.getJoypad().joypadRegister;
			default:
				return addressSpace[address] & 0xFF;
		}
	}

	public int[] getAddressSpace() {
		return addressSpace;
	}
	
	public void dump() {
		for(int b : addressSpace) {
			System.out.print(Util.byteToHex(b) + " ");
		}
	}

	public static ArrayList<Integer> addressWatchlist = new ArrayList<Integer>();
	public static void addAddressToWatchlist(int address) {
		addressWatchlist.add(address);
	}
	public void setMemoryAtAddress(int address, int source) {
		if((address & 0xff00) == 0xff00) {
			hRAMHandler(address, source);
			return;
		}
		if(addressWatchlist.contains(address)) {
//			System.out.println("0x"+Util.byteToHex16(address) + " -> 0x" + Util.byteToHex(source) + " / 0b" + Util.toBinaryString(source)  + " at programCounter " + Util.byteToHex16(Util.getCPU().getProgramCounter()));
		}

		switch(address & 0xF000) {
			case 0x8000:
			case 0x9000:
				Util.getGPU().setVRAM(address, source);
				Util.getGPU().updateTile(address);
				addressSpace[address] = source;
				if(Util.isDebugMode && address >= 0x9800 && address <= 0x9fff) {
					GameBoyGPU.getInstance().debugUpdateBackgroundWindow();
				}
				break;
			case 0xF000:
                addressSpace[address] = source;
			default:
				addressSpace[address] = source;
				break;

		}
	}

	private void hRAMHandler(int address, int source) {
        addressSpace[address] = source;
		if((address == IORegisters.BOOTROM_STATUS) && source == 0x01) {
			disableBootrom = true;
			GameBoyCPU.getInstance().resetDebugPositions();
			GameBoyGPU.getInstance().resetVRAM();
		}
		if(address == GpuRegisters.LCDC) {
			GpuRegisters.setLCDC(source);
		}
		if(Util.isDebugMode && address == GpuRegisters.SCROLL_Y) {
			GameBoyGPU.getInstance().debugUpdateBackgroundWindow();
		}

		if(Util.isDebugMode && address == GpuRegisters.SCROLL_X) {
			GameBoyGPU.getInstance().debugUpdateBackgroundWindow();
		}
	}
	
	

	

	
	
}
