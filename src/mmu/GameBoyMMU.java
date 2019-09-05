package mmu;


import bootrom.BootRom;
import main.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class GameBoyMMU {
	
	private File bootrom;
	private File file;
	private int[] memory;
	public boolean disableBootrom;
	private static GameBoyMMU singletonInstance;
	private IORegisters ior;
	
	private GameBoyMMU() {
		memory = new int[65536];
		ior = new IORegisters(this);

	}
	
	public void initialize(String filename) throws IOException {
		file = new File(filename);

		FileInputStream fStream = new FileInputStream(file);
		
		try {
			byte[] buff = new byte[65536];
			int loadromLength = (new Long(file.length())).intValue();
			fStream.read(buff, 0, loadromLength);
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
		if (disableBootrom) {
			return memory[address] & 0xFF;
		}
		else if(address < BootRom.data.length) {
			return BootRom.data[address] & 0xFF;
		}
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
				Util.getGPU().setVRAM(address, source);
				Util.getGPU().updateTile(address);
				memory[address] = source;
				break;
			case 0xF000:
				if((address == IORegisters.BOOTROM_STATUS) && source == 0x01) {
					disableBootrom = true;
				}
				memory[address] = source;
				break;
			default:
				memory[address] = source;
				break;

		}
	}
	
	

	

	
	
}
