package mmu;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import main.Util;


public class GameBoyMMU {
	
	private File file;
	private int[] memory;
	private int length = -1;
	private static GameBoyMMU singletonInstance;
	
	private GameBoyMMU() {
		memory = new int[65536];
	}
	
	public void initialize(String filename) throws IOException {
		file = new File(filename); 
		  
		FileInputStream fStream = new FileInputStream(file);
		
		try {
			this.length = (new Long(file.length())).intValue();
			byte[] buff = new byte[65536];
            fStream.read(buff, 0, (new Long(file.length())).intValue());
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
		memory[0xff44] = 0x90;
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

	public int getLength() {
		return length;
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
				Util.getGPU().setVRAM(address, source);
				Util.getGPU().updateTile(address);
				memory[address] = source;
			default:
				memory[address] = source;
				break;

		}
	}

	

	
	
}
