package mmu;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import main.Util;


public class GameBoyMMU {
	
	private File bootrom;
	private File file;
	private int[] memory;
	private static GameBoyMMU singletonInstance;
	
	private GameBoyMMU() {
		memory = new int[65536];
	}
	
	public void initialize(String filename) throws IOException {
		bootrom = new File("DMG_ROM.bin");
		file = new File(filename);
		  
		FileInputStream fStream = new FileInputStream(bootrom);
		FileInputStream fStream2 = new FileInputStream(file);
		
		try {
			byte[] buff = new byte[65536];
			int bootromLength = (new Long(bootrom.length())).intValue();
            fStream.read(buff, 0, bootromLength);
			int loadromLength = (new Long(file.length())).intValue();
			fStream2.read(buff, 256, loadromLength);
            for(int i = 0; i < buff.length; i++) {
            	memory[i] = buff[i];
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
		return memory[address] & 0xFF;
		
	}
	
	public int[] getMemory() {
		return memory;
	}
	
	public void dump() {
		for(int b : memory) {
			System.out.print(Util.byteToHex(b) + " ");
		}
	}

	public void setMemoryAtAddress(int address, int source) {
		switch(address & 0xF000) {
			case 0x8000:
			case 0x9000:
				//System.out.println("$" + Util.byteToHex16(address).toUpperCase() + ": $" + Util.byteToHex(source).toUpperCase());
				Util.getGPU().setVRAM(address, source);
				Util.getGPU().updateTile(address);
				memory[address] = source;
			default:
				memory[address] = source;
				break;

		}
	}

	

	
	
}
